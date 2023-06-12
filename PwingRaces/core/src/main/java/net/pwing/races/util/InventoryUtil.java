package net.pwing.races.util;

public class InventoryUtil {

    public static int getPagedSlot(int i) {
        return 9 + (((int) ((double) i / 7)) * 9) + 1 + (i % 7);
    }

    public static int getRows(int count) {
        return (int) Math.ceil((double) count / 7);
    }
}
