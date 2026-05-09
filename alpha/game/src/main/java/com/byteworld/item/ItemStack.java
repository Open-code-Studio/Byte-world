
package com.byteworld.item;

public class ItemStack {
    private ItemType type;
    private int count;

    public ItemStack(ItemType type) {
        this(type, 1);
    }

    public ItemStack(ItemType type, int count) {
        this.type = type;
        this.count = Math.min(count, type.getMaxStackSize());
    }

    public ItemType getType() {
        return type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = Math.max(0, Math.min(count, type.getMaxStackSize()));
    }

    public void increment(int amount) {
        count = Math.min(count + amount, type.getMaxStackSize());
    }

    public void decrement(int amount) {
        count = Math.max(0, count - amount);
    }

    public boolean isEmpty() {
        return count <= 0 || type == ItemType.AIR;
    }

    public boolean canStackWith(ItemStack other) {
        return other != null && type == other.type && count < type.getMaxStackSize();
    }

    public int stackWith(ItemStack other) {
        if (!canStackWith(other)) {
            return 0;
        }
        int space = type.getMaxStackSize() - count;
        int transferred = Math.min(space, other.count);
        count += transferred;
        other.count -= transferred;
        return transferred;
    }

    public ItemStack copy() {
        return new ItemStack(type, count);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemStack other = (ItemStack) obj;
        return type == other.type && count == other.count;
    }

    @Override
    public int hashCode() {
        return type.getId() * 100 + count;
    }
}
