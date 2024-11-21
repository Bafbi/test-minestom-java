package fr.bafbi.crafting;

import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.DeclareRecipesPacket;
import net.minestom.server.recipe.RecipeCategory;
import net.minestom.server.recipe.ShapedRecipe;
import net.minestom.server.recipe.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TestRecipe extends ShapedRecipe {

    public TestRecipe() {
        super("test_recipe", 1, 1, "a",
                RecipeCategory.Crafting.MISC,
                List.of(
                        new DeclareRecipesPacket.Ingredient(List.of(ItemStack.of(Material.STONE)))
                ),
                ItemStack.of(Material.STONE),
                false);
    }

    @Override
    public boolean shouldShow(@NotNull Player player) {
        return true;
    }
}
