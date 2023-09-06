package top.rgb39.shop.tools;

public class ArrayUtils {
    public static String join(String[] strings, String seperator) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            res.append(strings[i]);
            if (i < strings.length - 1) {
                res.append(seperator);
            }
        }

        return res.toString();
    }
}
