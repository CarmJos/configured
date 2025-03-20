package cc.carm.lib.configuration.value.text.function.inserter;

import cc.carm.lib.configuration.value.text.function.ContentHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public interface Insertable<RECEIVER, SELF> {

    List<ContentInserter<RECEIVER>> inserters();

    SELF inserter(@NotNull ContentInserter<RECEIVER> inserter);

    boolean noneInsertion();

    boolean inserting(@NotNull String id);

    @Nullable List<String> getInsertion(@NotNull String id, @Nullable RECEIVER receiver);

    /**
     * Insert the specific contents by the id.
     *
     * @param id       the id of the insertion text
     * @param supplier to supply the lines to insert
     * @return the current {@link ContentHandler} instance
     */
    SELF insert(@NotNull String id, @Nullable Function<RECEIVER, List<String>> supplier);

    /**
     * Insert the specific contents by the id.
     *
     * @param id the id of the insertion text
     * @return the current {@link ContentHandler} instance
     */
    default SELF insert(@NotNull String id) {
        return insert(id, (Function<RECEIVER, List<String>>) null);
    }

    /**
     * Insert the specific contents by the id.
     *
     * @param id       the id of the insertion text
     * @param contents the lines to insert
     * @return the current {@link ContentHandler} instance
     */
    default SELF insert(@NotNull String id, @NotNull List<String> contents) {
        return insert(id, receiver -> contents);
    }

    /**
     * Insert the specific contents by the id.
     *
     * @param id       the id of the insertion text
     * @param contents the lines to insert
     * @return the current {@link ContentHandler} instance
     */
    default SELF insert(@NotNull String id, @NotNull String... contents) {
        return insert(id, Arrays.asList(contents));
    }

    SELF removeInsert(@NotNull String id);

}
