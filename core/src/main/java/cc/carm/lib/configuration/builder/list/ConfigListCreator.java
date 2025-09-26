package cc.carm.lib.configuration.builder.list;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.collection.SimpleCollectionCreator;
import cc.carm.lib.configuration.value.standard.ConfiguredList;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConfigListCreator<V> extends SimpleCollectionCreator<V, List<V>, ConfiguredList<V>> {

    public ConfigListCreator(@NotNull ValueType<V> type) {
        super(type, ArrayList::new, ConfiguredList::new);
    }

}
