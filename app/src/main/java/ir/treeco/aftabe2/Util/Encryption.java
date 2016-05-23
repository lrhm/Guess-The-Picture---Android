package ir.treeco.aftabe2.Util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    private static String charset = "UTF-8";

    public static String encryptAES(String plainText, byte[] key) {
        byte[] encryptedTextBytes = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            encryptedTextBytes = cipher.doFinal(plainText.getBytes(charset));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(encryptedTextBytes, Base64.DEFAULT);

    }

    public static String decryptAES(String encryptedText, byte[] key) {
        byte[] decryptedTextBytes = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");

            byte[] encryptedTextBytes = Base64.decode(encryptedText, Base64.DEFAULT);

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new String(decryptedTextBytes);
    }
}