# configured-JSON

JSON
file-based
implementation,
compatible
with
all
Java
environments.

*
*Remember
that
JSON
does
not
support
file
comments.
**

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
            <artifactId>configured-json</artifactId>
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
    api "cc.carm.lib:configured-json:[LATEST RELEASE]"
}
```

## Example file format

```json
{
  "version": 1.0,
  "test-number": 3185496340759645184,
  "test-enum": "DAYS",
  "user": {
    "name": "774b3",
    "info": {
      "uuid": "f890b050-d3c5-4a32-a8b0-8a421ec2d4cc"
    }
  },
  "sub": {
    "that": {
      "operators": []
    }
  },
  "uuid-value": "a20c2eb2-e36b-40d7-a1ba-57826e3588c2",
  "users": {
    "1": "561f5142-8d59-4e50-855d-18638f3cfca8",
    "2": "629fadab-c625-4678-85d2-cc73cb4aa3b7",
    "3": "e29d1fb8-d8bd-4c2a-8ac0-4aaee77196dc",
    "4": "8ff8ab49-7c34-44c0-9edd-203a9d44f309",
    "5": "3c09dbff-ca37-468a-8c47-e8e52f837a54"
  },
  "inner": {
    "inner-value": 49.831712577873375
  },
  "class-value": 1.0,
  "test": {
    "user": {
      "name": "Carm",
      "info": {
        "uuid": "c3881d54-3d77-46ca-b031-2962b8b89141"
      }
    }
  }
}
```
