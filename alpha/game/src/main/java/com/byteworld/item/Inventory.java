
package com.byteworld.item;

import java.util.Arrays;

public class Inventory {
    private static final int HOTBAR_SIZE = 9;
    private static final int INVENTORY_SIZE = 36;
    
    private final ItemStack[] items;
    private int selectedSlot = 0;

    public Inventory() {
        this.items = new ItemStack[INVENTORY_SIZE];
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            items[i] = new ItemStack(ItemType.AIR);
        }
    }

    public ItemStack getItem(int slot) {
        if (slot >= 0 && slot < items.length) {
            return items[slot];
        }
        return new ItemStack(ItemType.AIR);
    }

    public void setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < items.length) {
            items[slot] = item != null ? item : new ItemStack(ItemType.AIR);
        }
    }

    public int addItem(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return item != null ? item.getCount() : 0;
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i].canStackWith(item)) {
                items[i].stackWith(item);
                if (item.isEmpty()) {
                    return 0;
                }
            }
        }

        for (int i = 0; i < items.length; i++) {
            if (items[i].isEmpty()) {
                items[i] = item.copy();
                return 0;
            }
        }

        return item.getCount();
    }

    public boolean removeItem(int slot, int count) {
        if (slot < 0 || slot >= items.length) {
            return false;
        }
        ItemStack item = items[slot];
        if (item.isEmpty()) {
            return false;
        }
        if (item.getCount() <= count) {
            items[slot] = new ItemStack(ItemType.AIR);
        } else {
            item.decrement(count);
        }
        return true;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        if (slot >= 0 && slot < HOTBAR_SIZE) {
            selectedSlot = slot;
        }
    }

    public ItemStack getSelectedItem() {
        return items[selectedSlot];
    }

    public int getSize() {
        return items.length;
    }

    public int getHotbarSize() {
        return HOTBAR_SIZE;
    }

    public void clear() {
        Arrays.fill(items, new ItemStack(ItemType.AIR));
    }
}
