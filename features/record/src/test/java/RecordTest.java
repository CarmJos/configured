import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.ConfigPath;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.temp.TempConfigFactory;
import cc.carm.lib.configuration.value.standard.ConfiguredList;
import cc.carm.lib.configuration.value.standard.ConfiguredMap;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;
import cc.carm.lib.configured.adapter.record.RecordAdapter;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecordTest {

    @ConfigPath(root = true)
    interface ConfigA extends Configuration {

        ConfiguredValue<Device> VAL = ConfiguredValue.of(new Device(
            "device1",
            "My Device",
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            new Chip("chip1", null),
            Arrays.asList(
                new User("Alice", 30),
                new User("Bob", 25)
            ),
            Map.of(
                "Cloud", new Connection("cloud.example.com", 443),
                "Local", new Connection("127.0.0.1", 8080)
            )
        ));

        ConfiguredMap<String, Device> DEVICES = ConfiguredMap.builderOf(Device.class)
            .asHashMap().fromObject()
            .defaults(m -> {
                Device d = randomDevice();
                m.put(d.id, d);
            })
            .build();

        ConfiguredList<Device> DEVICE_LIST = ConfiguredList.with(Device.class)
            .defaults(Arrays.asList(randomDevice(), randomDevice()))
            .build();

    }

    @ConfigPath(root = true)
    interface ConfigB extends Configuration {

        ConfiguredValue<Device> VAL = ConfiguredValue.of(Device.class);
    }

    @Test
    public void test() {

        ConfigurationHolder<?> holder = TempConfigFactory.create().build();
        RecordAdapter.register(holder);
        holder.initialize(ConfigA.class);

        System.out.println("Device ID: " + ConfigA.VAL.resolve().id());
        System.out.println("Device Name: " + ConfigA.VAL.resolve().name());
        System.out.println("Device Serial: " + ConfigA.VAL.resolve().serial());
        System.out.println("Chip ID: " + ConfigA.VAL.resolve().chip().id());
        System.out.println("Chip Serial: " + ConfigA.VAL.resolve().chip().serialNumber());
        for (User user : ConfigA.VAL.resolve().users()) {
            System.out.println("Another Users: " + user.name() + ", Age: " + user.age());
        }

        printMap(holder.config().asMap(), 0);

        ConfigA.DEVICES.forEach((k, v) -> {
            System.out.println("Device Key: " + k + ", ID: " + v.id + ", Name: " + v.name);
        });

        ConfigA.DEVICE_LIST.forEach((v) -> {
            System.out.println("Device ID: " + v.id + ", Name: " + v.name);
        });


//        try {
//            List<User> parsed = holder.deserialize(ValueType.ofList(User.class), holder.config().getList("val.users"));
//            System.out.println("Parsed Users: " + parsed);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        ConfigurationHolder<?> anotherHolder = TempConfigFactory.create().defaults(() -> holder.config().asMap()).build();
        RecordAdapter.register(anotherHolder);

        anotherHolder.initialize(ConfigB.class);

        System.out.println("Another Device ID: " + ConfigB.VAL.resolve().id());
        System.out.println("Another Device Name: " + ConfigB.VAL.resolve().name());
        System.out.println("Another Device Serial: " + ConfigB.VAL.resolve().serial());
        System.out.println("Another Chip ID: " + ConfigB.VAL.resolve().chip().id());
        System.out.println("Another Chip Serial: " + ConfigB.VAL.resolve().chip().serialNumber());
        System.out.println("users: " + ConfigB.VAL.resolve().users().size());
        for (User user : ConfigB.VAL.resolve().users()) {
            System.out.println("Another Users: " + user.name() + ", Age: " + user.age());
        }
        ConfigB.VAL.resolve().connections.forEach((k, v) -> {
            System.out.println("Connection " + k + ": " + v.address() + ":" + v.port());
        });


    }

    record User(String name, int age) {
    }

    record Connection(String address, int port) {
    }

    record Device(String id, String name, UUID serial, Chip chip,
                  List<User> users,
                  Map<String, Connection> connections) {
    }

    record Chip(String id, @Nullable String serialNumber) {
    }

    public static Device randomDevice() {
        return new Device(
            "device-" + UUID.randomUUID(),
            "Device " + (int) (Math.random() * 100),
            UUID.randomUUID(),
            new Chip("chip-" + UUID.randomUUID(), "SN" + (int) (Math.random() * 10000)),
            Arrays.asList(
                new User("User" + (int) (Math.random() * 100), (int) (Math.random() * 50 + 10)),
                new User("User" + (int) (Math.random() * 100), (int) (Math.random() * 50 + 10))
            ),
            Map.of(
                "Cloud", new Connection("cloud.example.com", 443),
                "Local", new Connection("192.168.1." + (int) (Math.random() * 255), 8080)
            )
        );
    }


    static void printMap(Map<String, Object> map, int indent) {
        String indentStr = " ".repeat(indent);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map<?, ?> subMap) {
                System.out.println(indentStr + entry.getKey() + ":");
                printMap((Map<String, Object>) subMap, indent + 2);
            } else if (entry.getValue() instanceof List<?> subList) {
                System.out.println(indentStr + entry.getKey() + ":");
                for (Object item : subList) {
                    if (item instanceof Map<?, ?> itemMap) {
                        System.out.println(indentStr + "  - ");
                        printMap((Map<String, Object>) itemMap, indent + 2);
                    } else {
                        System.out.println(indentStr + "  - " + item);
                    }
                }
            } else {
                System.out.println(indentStr + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

}
