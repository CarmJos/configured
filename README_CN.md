<div align=center>
<img src=".doc/images/banner.png"  alt="Banner"/>

[![version](https://img.shields.io/github/v/release/CarmJos/configured)](https://github.com/CarmJos/configured/releases)
[![License](https://img.shields.io/github/license/CarmJos/configured)](https://www.gnu.org/licenses/lgpl-3.0.html)
[![workflow](https://github.com/CarmJos/configured/actions/workflows/maven.yml/badge.svg?branch=master)](https://github.com/CarmJos/configured/actions/workflows/maven.yml)
[![CodeFactor](https://www.codefactor.io/repository/github/carmjos/configured/badge)](https://www.codefactor.io/repository/github/carmjos/configured)
![CodeSize](https://img.shields.io/github/languages/code-size/CarmJos/configured)
![](https://visitor-badge.glitch.me/badge?page_id=configured.readme)

README
LANGUAGES [ [English](README.md) | [
*
*中文
**](README_CN.md)  ]

</div>

# configured
_(
配置文件框架)_

<img src=".doc/images/logo-bg.svg" width="150px" alt="logo" align="right" style="float: right"/>

*
*一次配置，轻松读取！
**

一款简单便捷的通用配置文件加载、读取与更新工具，可自定义配置的格式。

## 特性 & 优势

支持 [YAML](providers/yaml), [JSON](providers/gson), [HOCON](providers/hocon)
和 [SQL](providers/sql)
等多种配置文件格式。

-
基于类的配置文件初始化、加载、获取与更新机制，方便快捷。
-
支持复杂配置的手动序列化、反序列化。
-
提供多种builder形式，快速构建
`ConfigValue<?>`
对象。
-
支持通过注解规定配置对应的路径、注释等信息。

## 开发

详细开发介绍请 [点击这里](.doc/README.md) ,
JavaDoc(
最新Release)
请 [点击这里](https://CarmJos.github.io/configured) 。

### 示例代码

为快速的展示该项目的适用性，这里有几个实际演示：

- [数据库配置文件实例](demo/src/main/java/cc/carm/lib/configuration/demo/DatabaseConfiguration.java)
- [全种类配置实例类演示](demo/src/main/java/cc/carm/lib/configuration/demo/tests/conf/DemoConfiguration.java)

您可以 [点击这里](demo/src/main/java/cc/carm/lib/configuration/demo)
直接查看现有的代码演示，更多复杂情况演示详见 [开发介绍](.doc/README.md) 。

```java

@ConfigPath(root = true)
@HeaderComments("Configurations for sample")
public interface SampleConfig extends Configuration {

    @InlineComment("Enabled?") // 行后注释
    ConfiguredValue<Boolean> ENABLED = ConfiguredValue.of(true);

    @HeaderComments("Server configurations") // 头部注释
    ConfiguredValue<Integer> PORT = ConfiguredValue.of(Integer.class);

    @HeaderComments({"[ UUID >-----------------------------------", "A lot of UUIDs"})
    @FooterComments("[ UUID >-----------------------------------")
    ConfiguredList<UUID> UUIDS = ConfiguredList.builderOf(UUID.class).fromString()
            .parse(UUID::fromString).serialize(UUID::toString)
            .defaults(
                    UUID.fromString("00000000-0000-0000-0000-000000000000"),
                    UUID.fromString("00000000-0000-0000-0000-000000000001")
            ).build();

    interface INFO extends Configuration {

        @HeaderComments("Configure your name!") // Header comment
        ConfiguredValue<String> NAME = ConfiguredValue.of("Joker");

        @ConfigPath("how-old-are-you") // 自定义路径
        ConfiguredValue<Integer> AGE = ConfiguredValue.of(24);

    }

}

```

```java
public class Sample {
    public static void main(String[] args) {
        // 1. 生成一个 “holder” 用于给配置类提供源配置的文件。
        ConfigurationHolder<?> holder = YAMLConfigFactory.from("target/config.yml")
                .resourcePath("configs/sample.yml")
                .indent(4) // Optional: Set the indentation of the configuration file.
                .build();

        // 2. 通过 “holder” 初始化配置类或配置实例。
        holder.initialize(SampleConfig.class);
        // 3. 现在可以享受快捷方便的配置文件使用方式了~
        System.out.println("Enabled? -> " + SampleConfig.ENABLED.resolve());
        SampleConfig.ENABLED.set(false);
        System.out.println("And now? -> " + SampleConfig.ENABLED.resolve());
        // p.s. 在本示例里的更改未保存，因此启用值在下次运行中仍将为 true。
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

### 依赖方式

#### Maven 依赖

<details>
<summary>远程库配置</summary>

```xml

<project>
    <repositories>

        <repository>
            <!--采用Maven中心库，安全稳定，但版本更新需要等待同步-->
            <id>maven</id>
            <name>Maven Central</name>
            <url>https://repo1.maven.org/maven2</url>
        </repository>

        <repository>
            <!--采用github依赖库，实时更新，但需要配置 (推荐) -->
            <id>configured</id>
            <name>GitHub Packages</name>
            <url>https://maven.pkg.github.com/CarmJos/configured</url>
        </repository>

        <repository>
            <!--采用我的私人依赖库，简单方便，但可能因为变故而无法使用-->
            <id>carm-repo</id>
            <name>Carm's Repo</name>
            <url>https://repo.carm.cc/repository/maven-public/</url>
        </repository>

    </repositories>
</project>
```

</details>

<details>
<summary>通用原生依赖</summary>

```xml

<project>
    <dependencies>
        <!--基础实现部分，需要自行实现“Provider”与“Wrapper”。-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-core</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--基于YAML文件的实现版本，可用于全部Java环境。-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-yaml</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <!--基于JSON文件的实现版本，可用于全部Java环境。-->
        <!--需要注意的是，JSON不支持文件注释。-->
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-gson</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-hocon</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-sql</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>

    </dependencies>
</project>
```

</details>

#### Gradle 依赖

<details>
<summary>远程库配置</summary>

```groovy
repositories {

    // 采用Maven中心库，安全稳定，但版本更新需要等待同步 
    mavenCentral()

    // 采用github依赖库，实时更新，但需要配置 (推荐)
    maven { url 'https://maven.pkg.github.com/CarmJos/configured' }

    // 采用我的私人依赖库，简单方便，但可能因为变故而无法使用
    maven { url 'https://repo.carm.cc/repository/maven-public/' }
}
```

</details>

<details>
<summary>通用原生依赖</summary>

```groovy

dependencies {

    //基础实现部分，需要自行实现“Provider”与“Wrapper”。
    api "cc.carm.lib:configured-core:[LATEST RELEASE]"

    //基于YAML文件的实现版本，可用于全部Java环境。
    api "cc.carm.lib:configured-yaml:[LATEST RELEASE]"

    //基于JSON文件的实现版本，可用于全部Java环境。
    //需要注意的是，JSON不支持文件注释。
    api "cc.carm.lib:configured-gson:[LATEST RELEASE]"

    api "cc.carm.lib:configured-hocon:[LATEST RELEASE]"

    api "cc.carm.lib:configured-sql:[LATEST RELEASE]"

}
```

</details>

## 衍生项目

### [
*
*MineConfiguration
**](https://github.com/CarmJos/MineConfiguration) (by @CarmJos )

configured
for
MineCraft!
开始在
MineCraft
相关服务器平台上轻松(
做)
配置吧！

目前支持
BungeeCord,
Bukkit(
Spigot)
服务端，后续将支持更多平台。

## 支持与捐赠

若您觉得本插件做的不错，您可以通过捐赠支持我！

感谢您对开源项目的支持！

万分感谢
Jetbrains
为我们提供了从事此项目和其他开源项目的许可！

[![](https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.svg)](https://www.jetbrains.com/?from=https://github.com/CarmJos/configured)

万分感谢来自 [ArtformGames](https://github.com/ArtformGames)
对本项目的大力支持与积极贡献！

<img src="https://raw.githubusercontent.com/ArtformGames/.github/master/logo/logo_full.svg" width="317px" height="117px" alt="ArtformGames">

## 开源协议

本项目源码采用 [GNU LESSER GENERAL PUBLIC LICENSE](https://www.gnu.org/licenses/lgpl-3.0.html)
开源协议。
