
package com.byteworld.item;

import java.util.ArrayList;
import java.util.List;

public class CraftingManager {
    private static final CraftingManager instance = new CraftingManager();
    private final List<CraftingRecipe> recipes = new ArrayList<>();

    private CraftingManager() {
        initRecipes();
    }

    private void initRecipes() {
        // Wooden Planks from Wood
        addRecipe(
            new ItemStack[][]{
                {new ItemStack(ItemType.WOOD), null, null},
                {null, null, null},
                {null, null, null}
            },
            new ItemStack(ItemType.WOODEN_PLANK, 4)
        );

        // Wooden Pickaxe
        addRecipe(
            new ItemStack[][]{
                {new ItemStack(ItemType.WOODEN_PLANK), new ItemStack(ItemType.WOODEN_PLANK), new ItemStack(ItemType.WOODEN_PLANK)},
                {null, new ItemStack(ItemType.WOOD), null},
                {null, new ItemStack(ItemType.WOOD), null}
            },
            new ItemStack(ItemType.WOODEN_PICKAXE)
        );

        // Stone Pickaxe
        addRecipe(
            new ItemStack[][]{
                {new ItemStack(ItemType.STONE), new ItemStack(ItemType.STONE), new ItemStack(ItemType.STONE)},
                {null, new ItemStack(ItemType.WOOD), null},
                {null, new ItemStack(ItemType.WOOD), null}
            },
            new ItemStack(ItemType.STONE_PICKAXE)
        );

        // Iron Pickaxe
        addRecipe(
            new ItemStack[][]{
                {new ItemStack(ItemType.IRON_INGOT), new ItemStack(ItemType.IRON_INGOT), new ItemStack(ItemType.IRON_INGOT)},
                {null, new ItemStack(ItemType.WOOD), null},
                {null, new ItemStack(ItemType.WOOD), null}
            },
            new ItemStack(ItemType.IRON_PICKAXE)
        );

        // Wooden Sword
        addRecipe(
            new ItemStack[][]{
                {null, new ItemStack(ItemType.WOODEN_PLANK), null},
                {null, new ItemStack(ItemType.WOODEN_PLANK), null},
                {null, new ItemStack(ItemType.WOOD), null}
            },
            new ItemStack(ItemType.WOODEN_SWORD)
        );

        // Stone Sword
        addRecipe(
            new ItemStack[][]{
                {null, new ItemStack(ItemType.STONE), null},
                {null, new ItemStack(ItemType.STONE), null},
                {null, new ItemStack(ItemType.WOOD), null}
            },
            new ItemStack(ItemType.STONE_SWORD)
        );

        // Iron Sword
        addRecipe(
            new ItemStack[][]{
                {null, new ItemStack(ItemType.IRON_INGOT), null},
                {null, new ItemStack(ItemType.IRON_INGOT), null},
                {null, new ItemStack(ItemType.WOOD), null}
            },
            new ItemStack(ItemType.IRON_SWORD)
        );
    }

    private void addRecipe(ItemStack[][] pattern, ItemStack result) {
        recipes.add(new CraftingRecipe(pattern, result));
    }

    public CraftingRecipe findMatchingRecipe(Inventory craftingGrid) {
        for (CraftingRecipe recipe : recipes) {
            if (recipe.matches(craftingGrid)) {
                return recipe;
            }
        }
        return null;
    }

    public static CraftingManager getInstance() {
        return instance;
    }
}
