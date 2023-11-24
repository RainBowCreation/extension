package net.rainbowcreation.spongelobby.utils;

public class time {
    public static long[] secondToMinute(long second) {
        long[] lst = new long[2];

        lst[0] = second/60;
        lst[1] = second%60;
        return (lst);
    }
}
