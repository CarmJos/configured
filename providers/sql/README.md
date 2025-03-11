# configured-SQL

SQL database implementation, support for MySQL or MariaDB.

## Table schema
```mysql
CREATE TABLE IF NOT EXISTS conf
(
    `namespace`      VARCHAR(32)        NOT NULL,                           # 命名空间 (代表其属于谁，类似于单个配置文件地址的概念)
    `path`           VARCHAR(96)        NOT NULL,                           # 配置路径 (ConfigPath)
    `type`           TINYINT UNSIGNED   NOT NULL DEFAULT 0,                 # 数据类型 (Integer/Byte/List/Map/...)
    `value`          MEDIUMTEXT,                                            # 配置项的值 (可能为JSON格式)
    `inline_comment` TEXT comment 'usage',                                  # 配置项的用法，本质是行内注释
    `header_comment` MEDIUMTEXT comment 'description',                      # 配置项的描述，本质是顶部注释
    `footer_comment` MEDIUMTEXT comment 'example',                          # 配置项的参考，本质是底部注释
    `version`        MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,                 # 配置项的版本
    `create_time`    DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP, # 创建时间
    `update_time`    DATETIME           NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`namespace`, `path`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
```

## Dependencies

### Maven Dependency

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

```xml

<project>
    <dependencies>
        <dependency>
            <groupId>cc.carm.lib</groupId>
            <artifactId>configured-sql</artifactId>
            <version>[LATEST RELEASE]</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
</project>
```

### Gradle Dependency

```groovy
repositories {

    // Using Maven Central Repository for secure and stable updates, though synchronization might be needed.
    mavenCentral()

    // Using GitHub dependencies for real-time updates, configuration required (recommended).
    maven { url 'https://maven.pkg.github.com/CarmJos/configured' }

}
```

```groovy
dependencies {
    api "cc.carm.lib:configured-sql:[LATEST RELEASE]"
}
```