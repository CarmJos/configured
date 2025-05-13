<div align=center>
<img src=".doc/images/banner.png"  alt="Banner"/>

[![version](https://img.shields.io/github/v/release/CarmJos/configured)](https://github.com/CarmJos/configured/releases)
[![License](https://img.shields.io/github/license/CarmJos/configured)](https://www.gnu.org/licenses/lgpl-3.0.html)
[![workflow](https://github.com/CarmJos/configured/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/configured/actions/workflows/maven.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/carmjos/configured/badge)](https://www.codefactor.io/repository/github/carmjos/configured)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/configured)
![](https://visitor-badge.glitch.me/badge?page_id=configured.readme)

README
LANGUAGES [ [
*
*English
**](README.md) | [中文](README_CN.md)  ]
</div>

# configured
_(
config
framework)_

<img src=".doc/images/logo-bg.svg" width="150px" alt="logo" align="right" style="float: right"/>

_
*
*"
Once
set,
Simple
get."
**_

A
simple,
easy-to-use
and
universal
solution
for
managing,
loading,
reading,
and
updating
configuration
files.

Supported
*
*JSON
**,
*
*YAML
**,
*
*Hocon
**,
*
*TOML
**,
*
*SQL
**,
*
*MongoDB
**...
and
much
more!

## Features & Advantages

Supported [YAML](providers/yaml), [JSON](providers/json), [HOCON](providers/hocon)
and [SQL](providers/sql)
based
configuration
files
format.

-
Class-based
mechanism
for
initializing,
loading,
retrieving,
and
updating
configuration
files,
ensuring
convenience
and
efficiency.
-
Supports
manual
serialization
and
deserialization
of
complex
configurations.
-
Offers
multiple
builder
forms
for
rapid
construction
of
`ConfigValue<?>`
objects.
-
Enables
specification
of
configuration
paths,
comments,
and
more
via
annotations.

## Development

For
the
latest
JavaDoc
release, [CLICK HERE](https://CarmJos.github.io/configured).

For
a
detailed
development
guide, [CLICK HERE](.doc/README.md).

### Preview

To
quickly
demonstrate
the
applicability
of
the
project,
here
are
a
few
practical
demonstrations:

- [Database configuration.](demo/src/main/java/cc/carm/lib/configuration/demo/DatabaseConfiguration.java)
- [Demonstration of configuration instance classes.](demo/src/main/java/cc/carm/lib/configuration/demo/tests/conf/DemoConfiguration.java)

Check
out
all
code
demonstrations [HERE](demo/src/main/java/cc/carm/lib/configuration/demo/DatabaseConfiguration.java).
For
more
examples,
see
the [Development Guide](.doc/README.md).

```java

@ConfigPath(root = true)
@HeaderComments("Configurations for sample")
public interface SampleConfig extends Configuration {

    @InlineComment("Enabled?") // Inline comment
    ConfiguredValue<Boolean> ENABLED = ConfiguredValue.of(true);

    @HeaderComments("Server configurations") // Header comment
    ConfiguredValue<Integer> PORT = ConfiguredValue.of(Integer.class);

    @HeaderComments({"[ UUID >-----------------------------------", "A lot of UUIDs"})
    @FooterComments("[ UUID >-----------------------------------")
    ConfiguredList<UUID> UUIDS = ConfiguredList.builderOf(UUID.class).fromString()
            .parse(UUID::fromString).serialize(UUID::toString)
            .defaults(
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
            ).build();

    @ConfigPath("info") // Custom path
    interface INFO extends Configuration {

        @HeaderComments("Configure your name!") // Header comment
        ConfiguredValue<String> NAME = ConfiguredValue.of("Joker");

        @ConfigPath("how-old-are-you") // Custom path
        ConfiguredValue<Integer> AGE = ConfiguredValue.of(24);

    }

}

```

```java
public class Sample {
    public static void main(String[] args) {
        // 1. Make a configuration provider from a file.
        ConfigurationHolder<?> holder = YAMLConfigFactory.from("target/config.yml")
                .resourcePath("configs/sample.yml")
                .indent(4) // Optional: Set the indentation of the configuration file.
                .build();

        // 2. Initialize the configuration classes or instances.
        holder.initialize(SampleConfig.class);
        // 3. Enjoy using the configuration!
        System.out.println("Enabled? -> " + SampleConfig.ENABLED.resolve()); // true
        SampleConfig.ENABLED.set(false);
        System.out.println("And now? -> " + SampleConfig.ENABLED.resolve()); // false
        // p.s. Changes not save so enable value will still be true in the next run.

        System.out.println("Your name is " + SampleConfig.INFO.NAME.resolve() + " (age=" + SampleConfig.INFO.AGE.resolve() + ")!");
    }
}

```

```yaml
# Configurations for sample

enabled: true #Enabled?

# Server configurations
port:

# [ UUID >-----------------------------------
# A lot of UUIDs
uuids:
  - 00000000-0000-0000-0000-000000000000
  - 00000000-0000-0000-0000-000000000001
# [ UUID >-----------------------------------

info:
  # Configure your name!
  name: Joker
  how-old-are-you: 24
```

### Dependencies

#### Maven Dependency

<details>
<summary>Remote Repository Configuration</summary>

```xml

<project>
    <repositories>

        <repository>
            <!-- Using Maven Central Repository for secure and stable updates, though synchronization might be needed. -->
            <id>maven</id>
            <name>Maven Central</name>
            <url>https://repo1.maven.org/maven2</url>
        </repository>

        <repository>
            <!-- Using GitHub dependencies for real-time updates, configuration required (recommended). -->
            <id>configured</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/configured</url>
        </repository>

    </repositories>
</project>
```

</details>

<details>
<summary>Generic Native Dependency</summary>

```xml

<project>
    <dependencies>
        <!-- Basic implementation part, requiring custom implementation of “Provider” and “Wrapper”. -->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-core</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!-- YAML file-based implementation, compatible with all Java environments. -->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-yaml</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!-- JSON file-based implementation, compatible with all Java environments. -->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-gson</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

    </dependencies>
</project>
```

</details>

#### Gradle Dependency

<details>
<summary>Remote Repository Configuration</summary>

```groovy
repositories {

    // Using Maven Central Repository for secure and stable updates, though synchronization might be needed.
    mavenCentral()

    // Using GitHub dependencies for real-time updates, configuration required (recommended).
    maven { url 'https://maven.pkg.github.com/CarmJos/configured' }

}
```

</details>

<details>
<summary>Generic Native Dependency</summary>

```groovy

dependencies {

    // Basic implementation part, requiring custom implementation of “Provider” and “Wrapper”.
    api "cc.carm.lib:configured-core:[LATEST RELEASE]"

    // YAML file-based implementation, compatible with all Java environments.
    api "cc.carm.lib:configured-yaml:[LATEST RELEASE]"

    // JSON file-based implementation, compatible with all Java environments.
    api "cc.carm.lib:configured-gson:[LATEST RELEASE]"

}
```

</details>

## Derived Projects

### [
*
*MineConfiguration
**](https://github.com/CarmJos/MineConfiguration) (by @CarmJos)

configured
for
MineCraft!
Easily
manage
configurations
on
MineCraft-related
server
platforms.

Currently,
it
supports
BungeeCord,
Velocity,
Bukkit (
Spigot)
servers,
with
more
platforms
to
be
supported
soon.

## Support and Donation

If
you
appreciate
this
plugin,
consider
supporting
me
with
a
donation!

Thank
you
for
supporting
open-source
projects!

Many
thanks
to
Jetbrains
for
kindly
providing
a
license
for
us
to
work
on
this
and
other
open-source
projects.

[![](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/?from=https://github.com/CarmJos/configured)

Many
thanks
to [ArtformGames](https://github.com/ArtformGames)
for
their
strong
support
and
active
contribution
to
this
project!

<img src="https://raw.githubusercontent.com/ArtformGames/.github/master/logo/logo_full.svg" width="317px" height="117px" alt="ArtformGames">

## Open Source License

This
project's
source
code
is
licensed
under
the [GNU LESSER GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/lgpl-3.0.html).
