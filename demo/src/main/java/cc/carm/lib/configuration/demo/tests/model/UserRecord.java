package cc.carm.lib.configuration.demo.tests.model;

import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRecord extends AbstractRecord {

    public static final UserRecord CARM = new UserRecord("Carm", UUID.fromString("f7b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b"));

    protected UUID uuid;

    public UserRecord(String name, UUID uuid) {
        super(name);
        this.uuid = uuid;
    }

    public void uuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("uuid", uuid.toString());
        map.put("info", map2);
        return map;
    }

    public static UserRecord deserialize(ConfigureSection section) {
        System.out.println(">  Deserializing  -> " + section.fullPath());
        String name = section.getString("name");
        if (name == null) throw new NullPointerException("name is null");
        String uuidString = section.getString("info.uuid");
        if (uuidString == null) throw new NullPointerException("uuid is null");
        return new UserRecord(name, UUID.fromString(uuidString));
    }

    public static UserRecord random() {
        return new UserRecord(UUID.randomUUID().toString().substring(0, 5), UUID.randomUUID());
    }


    @Override
    public String toString() {
        return "TestUser{" +
            "name='" + name + '\'' +
            ", uuid=" + uuid +
            '}';
    }
}
