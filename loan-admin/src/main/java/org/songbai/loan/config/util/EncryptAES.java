package org.songbai.loan.config.util;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;

/**
 * Created by yhj on 17/6/7.
 */
public class EncryptAES {

    private final static String DEFAULT_KEY = "@]S8(xG+0_S8X(s$=";

    public static String encrypt(String s, String key) throws Exception {

        Cipher encryptCipher = Cipher.getInstance("AES");

        encryptCipher.init(Cipher.ENCRYPT_MODE, getKeyByte(key));

        byte[] r = encryptCipher.doFinal(s.getBytes("utf-8"));

        return byte2Hex(r);
    }

    public static String decrypt(String s, String key) throws Exception {

        Cipher decryptCipher = Cipher.getInstance("AES");
        decryptCipher.init(Cipher.DECRYPT_MODE, getKeyByte(key));

        byte[] r = decryptCipher.doFinal(hex2Byte(s));

        return new String(r, "utf-8");
    }


    private static SecretKey getKeyByte(String key) throws UnsupportedEncodingException {

        key = key + DEFAULT_KEY;

        key = key.substring(0, 16);

        byte[] kb = key.getBytes("utf-8");

        return new javax.crypto.spec.SecretKeySpec(kb, "AES");

    }


    public static String byte2Hex(byte[] data) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            String temp = Integer.toHexString(((int) data[i]) & 0xFF);
            for (int t = temp.length(); t < 2; t++) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }

    public static byte[] hex2Byte(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

}
