package fr.bafbi;

import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import net.minestom.server.tag.TagReadable;
import net.minestom.server.tag.TagSerializer;
import net.minestom.server.tag.TagWritable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class LootTable {

    public final Map<ItemStack, Integer> loots;
    private final Random random;

    public LootTable(Map<ItemStack, Integer> lootTable, Random random) {
        this.loots = lootTable;
        this.random = random;
    }

    public LootTable(Map<ItemStack, Integer> lootTable) {
        this(lootTable, new Random());
    }

    public Optional< ItemStack> getRandomItemStack() {
        var total = loots.values().stream().reduce(Integer::sum).orElse(0);

        float random = (float) this.random.nextFloat() * total;
        for (Map.Entry<ItemStack, Integer> entry : loots.entrySet()) {
            random -= entry.getValue();
            if (random <= 0) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public static LootTable.Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private final Map<ItemStack, Integer> lootTable = new java.util.HashMap<>();

        public LootTable.Builder addItemStack(ItemStack itemStack, Integer weight) {
            lootTable.put(itemStack, weight);
            return this;
        }

        public LootTable build() {
            return new LootTable(lootTable);
        }
    }

    public static final Tag<LootTable> LOOT_TABLE_TAG = Tag.Structure("loottable", new TagSerializer<LootTable>() {


        @Override
        public @Nullable LootTable read(@NotNull TagReadable reader) {
            ItemStack loot = reader.getTag(Tag.ItemStack("loot"));
            int weight = reader.getTag(Tag.Integer("weight"));
            return LootTable.builder().addItemStack(loot, weight).build();
        }

        @Override
        public void write(@NotNull TagWritable writer, @NotNull LootTable value) {
            value.loots.forEach((itemStack, weight) -> {
                writer.setTag(Tag.ItemStack("loot"), itemStack);
                writer.setTag(Tag.Integer("weight"), weight);
            });
        }
    });

}
