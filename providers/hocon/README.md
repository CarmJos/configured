# configured-HOCON

HOCON
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
            <artifactId>configured-hocon</artifactId>
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
    api "cc.carm.lib:configured-hocon:[LATEST RELEASE]"
}
```

## Example file format

```hocon
class-value=1.0
# Inner Test
inner {
    inner-value=72.0043567836829
}
sub {
    that {
        operators=[]
    }
}
test {
    # Section类型数据测试
    user {
        info {
            uuid="8aba6166-1dc3-476d-8eb6-8957434c05ba"
        }
        name=Carm
    }
}
test-enum=DAYS
test-number=5555780951875134464
# Section类型数据测试
user {
    info {
        uuid="9ed3a8f3-ad2a-4a62-a720-5530f5d19b33"
    }
    name="9038c"
}
# [ID - UUID]对照表
# 
# 用于测试Map类型的解析与序列化保存
users {
    "1"="4bfe382e-7b9e-4dad-9314-d16ddeb99f34"
    "2"="6e587a1e-361e-43da-99ba-9de44db198dc"
    "3"=ce582c1c-d696-43d4-ab58-af40d000d656
    "4"="37b7eb1f-86b9-41c7-afa3-9ac9c75fef2c"
    "5"="2659c33a-3393-404d-960e-850fef3b23fd"
}
uuid-value="035e89e8-3fe8-45ed-a25d-eef0bbe8f73d"
version=1.0

```
