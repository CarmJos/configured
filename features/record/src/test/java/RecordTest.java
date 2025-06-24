import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.temp.TempConfigFactory;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;
import cc.carm.lib.configured.adapter.record.RecordAdapter;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RecordTest {

    interface Config extends Configuration {

        ConfiguredValue<Device> VAL = ConfiguredValue.of(new Device(
            "device1",
            "My Device",
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
            new Chip("chip1", "SN123456")
        ));

    }

    @Test
    public void test() {

        ConfigurationHolder<?> holder = TempConfigFactory.create().build();
        RecordAdapter.register(holder);
        holder.initialize(Config.class);

        System.out.println("Device ID: " + Config.VAL.resolve().id());
        System.out.println("Device Name: " + Config.VAL.resolve().name());
        System.out.println("Device Serial: " + Config.VAL.resolve().serial());
        System.out.println("Chip ID: " + Config.VAL.resolve().chip().id());
        System.out.println("Chip Serial: " + Config.VAL.resolve().chip().serialNumber());

        try {
            holder.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        printMap(holder.config().asMap(), 0);

    }


    public record Device(String id, String name, UUID serial, Chip chip) {
    }

    public record Chip(String id, String serialNumber) {
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
