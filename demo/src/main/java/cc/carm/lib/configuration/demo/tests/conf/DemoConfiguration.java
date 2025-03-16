package cc.carm.lib.configuration.demo.tests.conf;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.*;
import cc.carm.lib.configuration.demo.tests.model.ItemStack;
import cc.carm.lib.configuration.demo.tests.model.UserRecord;
import cc.carm.lib.configuration.value.standard.ConfiguredList;
import cc.carm.lib.configuration.value.standard.ConfiguredMap;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@ConfigPath(root = true)
@HeaderComments({"此处内容将显示在配置文件的最上方"})
@FooterComments({
        "------------------------------------------------",
        "此处内容将显示在配置文件的最下方",
        "可用于显示版权信息等",
        "感谢您使用 https://github.com/CarmJos/configured !"
})
public interface DemoConfiguration extends Configuration {

    @ConfigPath(root = true)
    @ConfigVersion(2)
    ConfiguredValue<Double> VERSION = ConfiguredValue.of(Double.class, 2.0D);

    @ConfigPath(root = true)
    @FooterComments({"此处内容将显示在配置条目的下方", "可用于补充说明，但一般不建议使用"})
    ConfiguredValue<Long> TEST_NUMBER = ConfiguredValue.with(Long.class)
            .validate(l -> l > 100, "数值必须大于100")
            .validate(l -> l < 100000000, "数值必须小于100000000")
            .defaults(123456789L).build();

    @HeaderComments({"枚举类型测试"})
    @FooterComments({"上述的枚举内容本质上是通过STRING解析的"})
    ConfiguredValue<ChronoUnit> TEST_ENUM = ConfiguredValue.of(ChronoUnit.class, ChronoUnit.DAYS);

    @HeaderComments({"空值测试"})
    @InlineComment("空值Inline注释")
    ConfiguredMap<String, String> EMPTY = ConfiguredMap.builderOf(String.class, String.class)
            .asLinkedMap().fromString()
            .build();

    @ConfigPath("registered_users") // 通过注解规定配置文件中的路径，若不进行注解则以变量名自动生成。
    @HeaderComments({"Section类型数据测试"}) // 通过注解给配置添加注释。
    @InlineComment("默认地注释会加到Section的首行末尾") // 通过注解给配置添加注释。
    ConfiguredList<UserRecord> ALLOWLISTS = ConfiguredList.builderOf(UserRecord.class).fromSection()
            .parse(UserRecord::deserialize).serialize(UserRecord::serialize)
            .defaults(UserRecord.CARM).build();

    @HeaderComments({
            "------------------------------------------------",
            "[ID - ItemStack]对照表", "", "用于测试Map类型的解析与序列化保存"
    })
    @FooterComments("------------------------------------------------")
    ConfiguredMap<Integer, ItemStack> ITEMS = ConfiguredMap.builderOf(Integer.class, ItemStack.class)
            .asLinkedMap().fromSection()
            .parseKey(data -> Integer.parseInt(data))
            .parse(ItemStack::deserialize).serialize(ItemStack::serialize)
            .defaults(m -> {
                m.put(1, new ItemStack("stone", 64));
                m.put(2, new ItemStack("iron", 64, "铁锭", Arrays.asList("一些铁锭", "可以制造东西")));
            })
            .build();


    /**
     * 支持内部类的直接注册。
     * 注意，需要启用 {@link  cc.carm.lib.configuration.source.option.StandardOptions#LOAD_SUB_CLASSES}
     */
    class SUB implements Configuration {

        @ConfigPath(value = "uuid-value", root = true)
        public static final ConfiguredValue<UUID> UUID_CONFIG_VALUE = ConfiguredValue
                .builderOf(UUID.class).fromString()
                .parse((holder, data) -> UUID.fromString(data))
                .build();

        @HeaderComments({"内部类的内部类测试", "通过这种方式，您可以轻易实现多层次的配置文件结构"})
        @FooterComments({"-------------"})
        public interface That extends Configuration {

            ConfiguredList<UUID> OPERATORS = ConfiguredList
                    .builderOf(UUID.class).fromString()
                    .parse(s -> Objects.requireNonNull(UUID.fromString(s)))
                    .build();

        }
    }


}
