package cc.carm.lib.configuration.demo.tests.model;

import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ItemStack {

    protected String material;
    protected int amount;
    protected @Nullable String name;
    protected @Nullable List<String> lore;

    public ItemStack(String material, int amount) {
        this(material, amount, null, null);
    }

    public ItemStack(String material, int amount, @Nullable String name, @Nullable List<String> lore) {
        this.material = material;
        this.amount = amount;
        this.name = name;
        this.lore = lore;
    }

    public String getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public @Nullable String getName() {
        return name;
    }

    public @Nullable List<String> getLore() {
        return lore;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public void setLore(@Nullable List<String> lore) {
        this.lore = lore;
    }


    public Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("material", material);
        if (amount != 1) map.put("amount", amount);
        if (name != null) map.put("name", name);
        if (lore != null) map.put("lore", lore);
        return map;
    }

    public static ItemStack deserialize(ConfigureSection section) {
        return new ItemStack(
            section.getString("material"),
            section.getInt("amount", 1),
            section.getString("name"),
            section.getStringList("lore")
        );
    }
}
