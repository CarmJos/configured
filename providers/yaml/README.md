# configured-YAML

YAML
file-based
implementation,
compatible
with
all
Java
environments.

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
            <artifactId>configured-yaml</artifactId>
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
    api "cc.carm.lib:configured-yaml:[LATEST RELEASE]"
}
```
