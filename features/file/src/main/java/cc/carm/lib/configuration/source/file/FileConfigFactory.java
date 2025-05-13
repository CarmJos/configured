package cc.carm.lib.configuration.source.file;

import cc.carm.lib.configuration.source.ConfigurationFactory;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

public abstract class FileConfigFactory<SOURCE extends FileConfigSource<?, ?, SOURCE>,
    HOLDER extends ConfigurationHolder<SOURCE>, SELF extends FileConfigFactory<SOURCE, HOLDER, SELF>>
    extends ConfigurationFactory<SOURCE, HOLDER, SELF> {


    protected @NotNull File file;
    protected @Nullable String resourcePath;

    public FileConfigFactory(@NotNull File file) {
        this.file = file;
    }

    public SELF file(@NotNull File file) {
        this.file = file;
        return self();
    }

    public SELF file(@NotNull Path path) {
        return file(path.toFile());
    }

    public SELF file(@NotNull File parent, @NotNull String fileName) {
        return file(new File(parent, fileName));
    }

    public SELF resourcePath(@Nullable String resourcePath) {
        this.resourcePath = resourcePath;
        return self();
    }

}
