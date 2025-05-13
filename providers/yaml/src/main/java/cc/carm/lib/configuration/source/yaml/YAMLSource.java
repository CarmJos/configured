package cc.carm.lib.configuration.source.yaml;

import cc.carm.lib.configuration.commentable.Commentable;
import cc.carm.lib.configuration.commentable.CommentableOptions;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.file.FileConfigSource;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.source.section.SourcedSection;
import cc.carm.lib.yamlcommentupdater.CommentedSection;
import cc.carm.lib.yamlcommentupdater.CommentedYAMLWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.representer.Representer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YAMLSource
    extends FileConfigSource<SourcedSection, Map<String, Object>, YAMLSource>
    implements CommentedSection {

    protected final @NotNull YamlConstructor yamlConstructor;
    protected final @NotNull YamlRepresenter yamlRepresenter;
    protected final @NotNull Yaml yaml;

    protected @Nullable SourcedSection rootSection;

    protected YAMLSource(@NotNull ConfigurationHolder<? extends YAMLSource> holder,
                         @NotNull File file, @Nullable String resourcePath) {
        super(holder, 0, file, resourcePath);
        this.yamlConstructor = new YamlConstructor(loaderOptions());
        this.yamlRepresenter = new YamlRepresenter(dumperOptions());
        this.yaml = new Yaml(this.yamlConstructor, this.yamlRepresenter, dumperOptions());

        initialize();
    }

    public void initialize() {
        try {
            initializeFile();
            onReload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected @NotNull YAMLSource self() {
        return this;
    }

    @Override
    public @NotNull Map<String, Object> original() {
        return section().data();
    }

    @Override
    public @NotNull SourcedSection section() {
        return Objects.requireNonNull(this.rootSection, "Root section is not initialized.");
    }

    public @NotNull LoaderOptions loaderOptions() {
        return holder().options().get(YAMLOptions.LOADER);
    }

    public @NotNull DumperOptions dumperOptions() {
        return holder().options().get(YAMLOptions.DUMPER);
    }

    @NotNull
    private MappingNode toNodeTree(@NotNull final ConfigureSection section) {
        List<NodeTuple> nodeTuples = new ArrayList<>();
        for (final Map.Entry<String, Object> entry : section.getValues(false).entrySet()) {
            Node keyNode = this.yaml.represent(entry.getKey());
            Node valueNode;
            if (entry.getValue() instanceof ConfigureSection) {
                valueNode = this.toNodeTree((ConfigureSection) entry.getValue());
            } else {
                valueNode = this.yaml.represent(entry.getValue());
            }
            nodeTuples.add(new NodeTuple(keyNode, valueNode));
        }

        return new MappingNode(Tag.MAP, nodeTuples, DumperOptions.FlowStyle.BLOCK);
    }


    @NotNull
    public String saveToString(ConfigureSection section) {
        MappingNode mappingNode = this.toNodeTree(section);
        StringWriter writer = new StringWriter();
        if (mappingNode.getValue().isEmpty()) {
            mappingNode.setFlowStyle(DumperOptions.FlowStyle.FLOW);
        }
        this.yaml.serialize(mappingNode, writer);
        return writer.toString();
    }

    @Override
    public void save() throws Exception {
        CommentedYAMLWriter writer = new CommentedYAMLWriter(
            String.valueOf(this.pathSeparator()),
            dumperOptions().getIndent(),
            holder.option(CommentableOptions.COMMENT_EMPTY_VALUE)
        );
        try {
            fileWriter(w -> w.write(writer.saveToString(this)));
        } catch (Exception ex) {
            fileWriter(w -> w.write(saveToString(section())));
        }
    }

    @Override
    protected void onReload() throws Exception {
        this.rootSection = fileReadString(this::loadFromString);
    }

    @Override
    public String toString() {
        return this.saveToString(section());
    }

    public @NotNull SourcedSection loadFromString(@NotNull String data) throws Exception {
        MappingNode mappingNode;
        try (Reader reader = new UnicodeReader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)))) {
            Node rawNode = this.yaml.compose(reader);
            mappingNode = (MappingNode) rawNode;
        }
        if (mappingNode == null) return SourcedSection.root(this);


        Map<String, Object> map = new LinkedHashMap<>();
        this.constructMap(mappingNode, map);
        return SourcedSection.root(this, map);
    }

    private void constructMap(@NotNull MappingNode mappingNode, @NotNull Map<String, Object> section) {
        this.yamlConstructor.flattenMapping(mappingNode);
        for (final NodeTuple nodeTuple : mappingNode.getValue()) {

            final Node keyNode = nodeTuple.getKeyNode();
            final String key = String.valueOf(this.yamlConstructor.construct(keyNode));
            Node valueNode = nodeTuple.getValueNode();

            while (valueNode instanceof AnchorNode) {
                valueNode = ((AnchorNode) valueNode).getRealNode();
            }

            if (valueNode instanceof MappingNode) {
                Map<String, Object> child = new LinkedHashMap<>();
                this.constructMap((MappingNode) valueNode, child);
                section.put(key, child);
            } else {
                section.put(key, this.yamlConstructor.construct(valueNode));
            }
        }
    }

    @Override
    public String serializeValue(@NotNull String key, @NotNull Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return saveToString(SourcedSection.root(this, map));
    }

    @Override
    public @Nullable Set<String> getKeys(@Nullable String sectionKey, boolean deep) {
        if (sectionKey == null) return section().getKeys(deep);
        ConfigureSection sub = section().getSection(sectionKey);
        if (sub == null) return null;
        return sub.getKeys(deep);
    }

    @Override
    public @Nullable Object getValue(@NotNull String key) {
        return get(key);
    }

    @Override
    public @Nullable String getInlineComment(@NotNull String key) {
        return Commentable.getInlineComment(holder(), key);
    }

    @Override
    public @Nullable List<String> getHeaderComments(@Nullable String key) {
        return Commentable.getHeaderComments(holder(), key);
    }

    @Override
    public @Nullable List<String> getFooterComments(@Nullable String key) {
        return Commentable.getFooterComments(holder(), key);
    }

    public static class YamlRepresenter extends Representer {

        public YamlRepresenter(@NotNull final DumperOptions dumperOptions) {
            super(dumperOptions);
            this.multiRepresenters.put(ConfigureSection.class, new RepresentMap() {
                @NotNull
                @Override
                public Node representData(@NotNull final Object object) {
                    return super.representData(((ConfigureSection) object).getValues(false));
                }
            });
            this.multiRepresenters.remove(Enum.class);
        }
    }

    public static class YamlConstructor extends SafeConstructor {

        public YamlConstructor(@NotNull final LoaderOptions loaderOptions) {
            super(loaderOptions);
        }

        @Nullable
        Object construct(@NotNull final Node node) {
            return this.constructObject(node);
        }

        @Override
        public void flattenMapping(@NotNull final MappingNode mappingNode) {
            super.flattenMapping(mappingNode);
        }
    }


}
