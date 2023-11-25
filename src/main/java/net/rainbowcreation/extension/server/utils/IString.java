package net.rainbowcreation.extension.server.utils;

public class IString {
    public static String genHeader(String name) {
        StringBuilder header = new StringBuilder();
        String c = "#";
        int len = name.length();
        int x = 40; // prefix
        int y = 6; // suffix
        int z = 86-x-y;
        int tmp = z - len; // pre body
        for (int i = 0; i < x; i++) // do prefix
            header.append(c);
        for (int i = 0; i < tmp; i++) // do pre body
            header.append(c);
        header.append(name); // do body
        for (int i = 0; i < y; i++) // do suffix
            header.append(c);
        return header.toString();
    }

    public static String capitalizeFirstLetter(String name) {
        if (name.contains(" ")) {
            String[] split = name.split(" ");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append(capitalizeFirstLetter(s));
                sb.append(" ");
            }
            return sb.toString();
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
