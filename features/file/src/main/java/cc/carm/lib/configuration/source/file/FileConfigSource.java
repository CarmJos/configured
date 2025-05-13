package cc.carm.lib.configuration.source.file;

import cc.carm.lib.configuration.function.DataConsumer;
import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.option.FileConfigOptions;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.source.section.ConfigureSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Objects;

public abstract class FileConfigSource<SECTION extends ConfigureSection, ORIGINAL, SELF extends FileConfigSource<SECTION, ORIGINAL, SELF>>
    extends ConfigureSource<SECTION, ORIGINAL, SELF> {

    protected final @NotNull File file;
    protected final @Nullable String resourcePath;

    protected FileConfigSource(@NotNull ConfigurationHolder<? extends SELF> holder, long lastUpdateMillis,
                               @NotNull File file, @Nullable String resourcePath) {
        super(holder, lastUpdateMillis);
        this.file = file;
        this.resourcePath = resourcePath;
    }


    public Charset charset() {
        return holder().options().get(FileConfigOptions.CHARSET);
    }

    public boolean copyDefaults() {
        return holder().options().get(FileConfigOptions.COPY_DEFAULTS);
    }

    public void initializeFile() throws IOException {
        if (this.file.exists()) return;

        File parent = this.file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Failed to create directory " + file.getParentFile().getAbsolutePath());
        }

        if (resourcePath != null && copyDefaults()) {
            try {
                saveResource(resourcePath, false);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        if (!this.file.exists() && !this.file.createNewFile()) {
            throw new IOException("Failed to create file " + file.getAbsolutePath());
        }
    }

    protected <R> R fileInputStream(@NotNull DataFunction<InputStream, R> loader) throws Exception {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            return loader.handle(is);
        }
    }

    protected <R> R fileReader(@NotNull DataFunction<Reader, R> loader) throws Exception {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            try (Reader r = new InputStreamReader(is, charset())) {
                return loader.handle(r);
            }
        }
    }

    protected <R> R fileReadString(@NotNull DataFunction<String, R> loader) throws Exception {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            try (Reader r = new InputStreamReader(is, charset())) {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[1024];
                int len;
                while ((len = r.read(buf)) > 0) {
                    sb.append(buf, 0, len);
                }
                return loader.handle(sb.toString());
            }
        }
    }

    protected void fileReadString(@NotNull DataConsumer<String> loader) throws Exception {
        try (InputStream is = Files.newInputStream(file.toPath())) {
            try (Reader r = new InputStreamReader(is, charset())) {
                StringBuilder sb = new StringBuilder();
                char[] buf = new char[1024];
                int len;
                while ((len = r.read(buf)) > 0) {
                    sb.append(buf, 0, len);
                }
                loader.accept(sb.toString());
            }
        }
    }

    protected void fileOutputStream(@NotNull DataConsumer<OutputStream> stream) throws Exception {
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            stream.accept(os);
        }
    }

    protected void fileWriter(@NotNull DataConsumer<Writer> writer) throws Exception {
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            try (Writer w = new OutputStreamWriter(os, charset())) {
                writer.accept(w);
            }
        }
    }

    protected void saveResource(@NotNull String resourcePath, boolean replace)
        throws IOException, IllegalArgumentException {
        Objects.requireNonNull(resourcePath, "ResourcePath cannot be null");
        if (resourcePath.isEmpty()) throw new IllegalArgumentException("ResourcePath cannot be empty");

        resourcePath = resourcePath.replace('\\', '/');

        URL url = this.getClass().getClassLoader().getResource(resourcePath);
        if (url == null) throw new IllegalArgumentException("The resource '" + resourcePath + "' not exists");

        File outDir = file.getParentFile();

        if (!outDir.exists() && !outDir.mkdirs()) throw new IOException("Failed to create directory " + outDir);
        if (!file.exists() || replace) {
            try (OutputStream out = Files.newOutputStream(file.toPath())) {
                URLConnection connection = url.openConnection();
                connection.setUseCaches(false);
                try (InputStream in = connection.getInputStream()) {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        }
    }

    @Nullable
    protected InputStream getResource(@NotNull String filename) {
        try {
            URL url = this.getClass().getClassLoader().getResource(filename);
            if (url == null) return null;
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

}
