package ir.treeco.aftabe.New.Util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

public class Tools {

    public String decodeBase64(String string) {
        byte[] data = Base64.decode(string, Base64.DEFAULT);
        String solution = "";

        try {
            solution = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return solution;

    }
}
