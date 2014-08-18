package ir.treeco.aftabe.utils;

import android.util.Base64;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.UnsupportedEncodingException;

/**
 * Created by hossein on 7/31/14.
 */
public class Encryption {
    private static String charset = "UTF-8";

    public static String encrypt(String target) {
        return Base64Coder.encodeString(target);
    }

    public static String decrypt(String target) {
        return Base64Coder.decodeString(target);
    }
}