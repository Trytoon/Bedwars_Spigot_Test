package fr.trytoon.bedwars.items;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class NbtItemsUtils {

    private static boolean matchesNBT(ReadableNBT comparedTo, ReadableNBT filterNBT) {
        
        for (String key : filterNBT.getKeys()) {
            Object filterValue = getValue(filterNBT, key);

            if (filterValue == null) continue;

            if (!hasNBTWithValue(comparedTo, key, filterValue)) {
                return false;
            }
        }

        return true;
    }

    public static Object getValue(ReadableNBT nbt, String key) {
        return switch (nbt.getType(key)) {
            case NBTTagString -> nbt.getString(key);
            case NBTTagInt -> nbt.getInteger(key);
            case NBTTagByte -> nbt.getByte(key);
            case NBTTagShort -> nbt.getShort(key);
            case NBTTagLong -> nbt.getLong(key);
            case NBTTagFloat -> nbt.getFloat(key);
            case NBTTagDouble -> nbt.getDouble(key);
            case NBTTagByteArray -> nbt.getByteArray(key);
            case NBTTagIntArray -> nbt.getIntArray(key);
            case NBTTagLongArray -> nbt.getLongArray(key);
            default -> null;
        };
    }

    public static int simulateRemove(Inventory inventory, ItemStack templateItem, int amountToRemove) {
        ReadableNBT itemNbt = NBT.readNbt(templateItem);

        int count = 0;

        for (ItemStack item : inventory.getContents()) {
            if (item == null) continue;

            ReadableNBT inventoryItemNBT = NBT.readNbt(item);

            if (matchesNBT(inventoryItemNBT, itemNbt)) {
                count += item.getAmount();
                
                if (count >= amountToRemove) {
                    return amountToRemove;
                }
            }
        }
        
        return count;
    }

    public static boolean remove(Inventory inventory, ItemStack itemStack, int amountToRemove) {
        ReadableNBT itemNbt = NBT.readNbt(itemStack);

        int toRemove = amountToRemove;
        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null) continue;

            ReadableNBT inventoryItemNBT = NBT.readNbt(item);

            if (matchesNBT(inventoryItemNBT, itemNbt)) {
                int stackAmount = item.getAmount();

                if (stackAmount <= toRemove) {
                    inventory.setItem(i, null);
                    toRemove -= stackAmount;
                } else {

                    //always clone item before removing
                    ItemStack newItem = item.clone();
                    newItem.setAmount(stackAmount - toRemove);
                    inventory.setItem(i, newItem);
                    toRemove = 0;
                }

                if (toRemove <= 0) {
                    break;
                }
            }
        }

        return toRemove <= 0;
    }


    public static boolean hasNBTWithValue(ReadableNBT nbt, String tagName, Object expectedValue) {
        if (!nbt.hasTag(tagName)) {
            return false;
        }

        NBTType nbtType = nbt.getType(tagName);

        boolean match = switch (nbtType) {
            case NBTTagString -> expectedValue instanceof String && nbt.getString(tagName).equals(expectedValue);
            case NBTTagInt -> expectedValue instanceof Integer && nbt.getInteger(tagName) == (int) expectedValue;
            case NBTTagByte -> expectedValue instanceof Byte && nbt.getByte(tagName) == (byte) expectedValue;
            case NBTTagShort -> expectedValue instanceof Short && nbt.getShort(tagName) == (short) expectedValue;
            case NBTTagLong -> expectedValue instanceof Long && nbt.getLong(tagName) == (long) expectedValue;
            case NBTTagFloat -> expectedValue instanceof Float && Math.abs(nbt.getFloat(tagName) - (float) expectedValue) < 0.0001f;
            case NBTTagDouble -> expectedValue instanceof Double && Math.abs(nbt.getDouble(tagName) - (double) expectedValue) < 0.0001;
            case NBTTagByteArray -> expectedValue instanceof byte[] && java.util.Arrays.equals(nbt.getByteArray(tagName), (byte[]) expectedValue);
            case NBTTagIntArray -> expectedValue instanceof int[] && java.util.Arrays.equals(nbt.getIntArray(tagName), (int[]) expectedValue);
            case NBTTagLongArray -> expectedValue instanceof long[] && java.util.Arrays.equals(nbt.getLongArray(tagName), (long[]) expectedValue);
            default -> false;
        };

        return match;
    }
}
