package ir.treeco.aftabe.utils;

import android.content.Context;
import android.util.Base64;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by hossein on 7/31/14.
 */
public class Encryption {
    private static String charset = "UTF-8";

    public static String encryptBase64(String target) {
        return Base64Coder.encodeString(target);
    }

    public static String decryptBase64(String target) {
        return Base64Coder.decodeString(target);
    }

    public static String encryptAES(String plainText, Context context) {
        byte[] encryptedTextBytes = null;
        try {
            byte[] keyBytes = Utils.getAESkey(context).getBytes(charset);
//            byte[] tmp = new byte[16];
//            for(int i=0;i<16;++i) tmp[i] = keyBytes[i];
//            keyBytes = tmp;
//            keyBytes = Arrays.copyOf(keyBytes, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Base64Coder.encodeLines(encryptedTextBytes);
    }

    public static String decryptAES(String encryptedText, Context context) {
        byte[] decryptedTextBytes = null;
        try {
            byte[] keyBytes = Utils.getAESkey(context).getBytes(charset);
//            keyBytes = Arrays.copyOf(keyBytes, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
            byte[] encryptedTextBytes = Base64Coder.decode(encryptedText);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(decryptedTextBytes);
    }
}