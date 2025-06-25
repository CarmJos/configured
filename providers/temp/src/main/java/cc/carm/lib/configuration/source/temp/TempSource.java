package cc.carm.lib.configuration.source.temp;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.section.ConfigureSource;
import cc.carm.lib.configuration.source.section.SourcedSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class TempSource extends ConfigureSource<SourcedSection, Map<String, Object>, TempSource> {


    protected @NotNull SourcedSection rootSection;

    protected TempSource(@NotNull ConfigurationHolder<? extends TempSource> holder,
                         @NotNull Map<String, Object> defaults) {
        super(holder, 0);
        this.rootSection = SourcedSection.root(this, defaults);
    }

    @Override
    protected @NotNull TempSource self() {
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

    @Override
    public void save() throws Exception {
        // Nothing to do here.
    }

    @Override
    protected void onReload() throws Exception {
        // Also nothing to do, because this is a temporary source.
    }

}
