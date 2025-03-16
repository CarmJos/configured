package cc.carm.lib.configuration.source.loader;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.function.ValueValidator;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.meta.ConfigurationMetadata;
import cc.carm.lib.configuration.source.meta.StandardMeta;
import cc.carm.lib.configuration.source.option.StandardOptions;
import cc.carm.lib.configuration.value.ConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Function;

/**
 * Configuration initializer,
 * used to initialize {@link ConfigValue}s from {@link Configuration} classes.
 */
public class ConfigurationInitializer {

    protected @NotNull PathGenerator pathGenerator;
    protected @NotNull ConfigInitializeHandler<Field, ConfigValue<?, ?>> valueInitializer;
    protected @NotNull ConfigInitializeHandler<Class<? extends Configuration>, Object> classInitializer;

    public ConfigurationInitializer() {
        this(PathGenerator.of(), ConfigInitializeHandler.start(), ConfigInitializeHandler.start());
    }

    public ConfigurationInitializer(@NotNull PathGenerator pathGenerator,
                                    @NotNull ConfigInitializeHandler<Field, ConfigValue<?, ?>> valueInitializer,
                                    @NotNull ConfigInitializeHandler<Class<? extends Configuration>, Object> classInitializer) {
        this.pathGenerator = pathGenerator;
        this.valueInitializer = valueInitializer;
        this.classInitializer = classInitializer;
    }

    public void pathGenerator(@NotNull PathGenerator pathGenerator) {
        this.pathGenerator = pathGenerator;
    }

    public @NotNull PathGenerator pathGenerator() {
        return pathGenerator;
    }

    public ConfigInitializeHandler<Field, ConfigValue<?, ?>> fieldInitializer() {
        return valueInitializer;
    }

    public void fieldInitializer(@NotNull ConfigInitializeHandler<Field, ConfigValue<?, ?>> fieldInitializer) {
        this.valueInitializer = fieldInitializer;
    }

    public ConfigInitializeHandler<Class<? extends Configuration>, Object> classInitializer() {
        return classInitializer;
    }

    public void classInitializer(@NotNull ConfigInitializeHandler<Class<? extends Configuration>, Object> classInitializer) {
        this.classInitializer = classInitializer;
    }

    public void appendFieldInitializer(@NotNull ConfigInitializeHandler<Field, ConfigValue<?, ?>> fieldInitializer) {
        this.valueInitializer = this.valueInitializer.andThen(fieldInitializer);
    }

    public void appendClassInitializer(@NotNull ConfigInitializeHandler<Class<? extends Configuration>, Object> classInitializer) {
        this.classInitializer = this.classInitializer.andThen(classInitializer);
    }

    public <T, A extends Annotation> void registerClassAnnotation(@NotNull Class<A> annotation,
                                                                  @NotNull ConfigurationMetadata<T> metadata,
                                                                  @NotNull Function<A, T> extractor) {
        appendClassInitializer((holder, path, clazz, instance) -> {
            A data = clazz.getAnnotation(annotation);
            if (data == null) return;
            holder.metadata(path).setIfAbsent(metadata, extractor.apply(data));
        });
    }

    public <T, A extends Annotation> void registerFieldAnnotation(@NotNull Class<A> annotation,
                                                                  @NotNull ConfigurationMetadata<T> metadata,
                                                                  @NotNull Function<A, T> extractor) {
        appendFieldInitializer((holder, path, field, instance) -> {
            A data = field.getAnnotation(annotation);
            if (data == null) return;
            holder.metadata(path).setIfAbsent(metadata, extractor.apply(data));
        });
    }

    public <T, A extends Annotation> void registerAnnotation(@NotNull Class<A> annotation,
                                                             @NotNull ConfigurationMetadata<T> metadata,
                                                             @NotNull Function<A, T> extractor) {
        registerClassAnnotation(annotation, metadata, extractor);
        registerFieldAnnotation(annotation, metadata, extractor);
    }

    public <A extends Annotation> void registerValidAnnotation(@NotNull Class<A> annotation,
                                                               @NotNull Function<A, ValueValidator<Object>> builder) {
        appendFieldInitializer((holder, path, field, instance) -> {
            A data = field.getAnnotation(annotation);
            if (data == null) return;
            instance.validate((h, t) -> builder.apply(data).validate(h, t));
        });
    }

    public @Nullable String getFieldPath(@NotNull ConfigurationHolder<?> holder, @Nullable String parentPath, @NotNull Field field) {
        return pathGenerator.getFieldPath(holder, parentPath, field);
    }

    public @Nullable String getClassPath(@NotNull ConfigurationHolder<?> holder, @Nullable String parentPath,
                                         @NotNull Class<?> clazz, @Nullable Field clazzField) {
        return pathGenerator.getClassPath(holder, parentPath, clazz, clazzField);
    }

    public void initialize(@NotNull ConfigurationHolder<?> holder,
                           @NotNull Configuration config) throws Exception {
        initializeInstance(holder, config, null, null);
        if (holder.option(StandardOptions.SET_DEFAULTS)) holder.save();
    }

    public void initialize(@NotNull ConfigurationHolder<?> holder,
                           @NotNull Class<? extends Configuration> clazz) throws Exception {
        initializeStaticClass(holder, clazz, null, null);
        if (holder.option(StandardOptions.SET_DEFAULTS)) holder.save();
    }


    // 针对实例类的初始化方法
    protected void initializeInstance(@NotNull ConfigurationHolder<?> holder, @NotNull Configuration root,
                                      @Nullable String parentPath, @Nullable Field configField) {
        String path = getClassPath(holder, parentPath, root.getClass(), configField);
        try {
            this.classInitializer.whenInitialize(holder, path, root.getClass(), root);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Arrays.stream(root.getClass().getDeclaredFields()).forEach(field -> initializeField(holder, root, field, path));
    }

    // 针对静态类的初始化方法
    @SuppressWarnings("unchecked")
    protected void initializeStaticClass(@NotNull ConfigurationHolder<?> holder,
                                         @NotNull Class<?> clazz,
                                         @Nullable String parentPath, @Nullable Field configField) {
        if (!Configuration.class.isAssignableFrom(clazz)) return; // Only Configuration class can be initialized.

        String path = getClassPath(holder, parentPath, clazz, configField);

        try {
            this.classInitializer.whenInitialize(holder, path, (Class<? extends Configuration>) clazz, configField);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Field field : clazz.getDeclaredFields()) {
            initializeField(holder, clazz, field, path);
        }

        if (holder.option(StandardOptions.LOAD_SUB_CLASSES)) {
            Class<?>[] classes = clazz.getDeclaredClasses();
            for (int i = classes.length - 1; i >= 0; i--) {   // 逆向加载，保持顺序。
                initializeStaticClass(holder, classes[i], path, null);
            }
        }
    }

    protected void initializeField(@NotNull ConfigurationHolder<?> holder,
                                   @NotNull Object source, @NotNull Field field, @Nullable String parent) {
        try {
            field.setAccessible(true);
            Object object = field.get(source);
//
            if (object instanceof ConfigValue<?, ?>) {
                // 目标是 ConfigValue 实例，进行具体的初始化注入
                ConfigValue<?, ?> value = (ConfigValue<?, ?>) object;
                String path = getFieldPath(holder, parent, field);
                if (path == null) return;
                value.initialize(holder, path);
                holder.metadata(path).set(StandardMeta.VALUE, value); // Mark the minimal config value unit.
                if (holder.option(StandardOptions.SET_DEFAULTS)) {
                    value.setDefault(); // Set default value.
                }
                try {
                    this.valueInitializer.whenInitialize(holder, path, field, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (holder.option(StandardOptions.PRELOAD)) {
                    value.get(); // Preload the value by calling #get method.
                }
            } else if (source instanceof Configuration && object instanceof Configuration) {
                // 当且仅当 源字段与字段 均为Configuration实例时，才对目标字段进行下一步初始化加载。
                initializeInstance(holder, (Configuration) object, parent, field);
            } else if (source instanceof Class<?> && object instanceof Class<?>) {
                // 当且仅当 源字段与字段 均为静态类时，才对目标字段进行下一步初始化加载。
                initializeStaticClass(holder, (Class<?>) object, parent, field);
            }

            // 以上判断实现以下规范：
            // - 实例类中仅加载 ConfigValue实例 与 Configuration实例
            // - 静态类中仅加载 静态ConfigValue实例 与 静态Configuration类

        } catch (IllegalAccessException ignored) {
        }
    }

}
