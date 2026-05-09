
package com.byteworld.item;

import java.util.ArrayList;
import java.util.List;

public class CraftingRecipe {
    private final ItemStack[][] pattern;
    private final ItemStack result;

    public CraftingRecipe(ItemStack[][] pattern, ItemStack result) {
        this.pattern = pattern;
        this.result = result;
    }

    public boolean matches(Inventory craftingGrid) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int slot = y * 3 + x;
                ItemStack gridItem = craftingGrid.getItem(slot);
                ItemStack patternItem = pattern[y][x];
                
                if (patternItem == null || patternItem.isEmpty()) {
                    if (!gridItem.isEmpty()) {
                        return false;
                    }
                } else {
                    if (gridItem.isEmpty() || gridItem.getType() != patternItem.getType()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ItemStack getResult() {
        return result.copy();
    }

    public void consumeIngredients(Inventory craftingGrid) {
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                int slot = y * 3 + x;
                ItemStack patternItem = pattern[y][x];
                if (patternItem != null && !patternItem.isEmpty()) {
                    craftingGrid.getItem(slot).decrement(1);
                }
            }
        }
    }
}
