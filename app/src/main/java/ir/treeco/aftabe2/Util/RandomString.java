package ir.treeco.aftabe2.Util;

import java.util.Random;

/**
 * Created by al on 3/6/16.
 */
public class RandomString {

    private static final char[] symbols;

    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ++ch)
            tmp.append(ch);
        for (char ch = 'a'; ch <= 'z'; ++ch)
            tmp.append(ch);
        symbols = tmp.toString().toCharArray();
    }


    public static String nextString() {

        char[] buf = new char[8];
        for (int idx = 0; idx < buf.length; ++idx) {
            Random random = new Random(System.currentTimeMillis());
            buf[idx] = symbols[random.nextInt(symbols.length)];
            try {
                Thread.sleep(random.nextInt(85));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new String(buf);
    }
}