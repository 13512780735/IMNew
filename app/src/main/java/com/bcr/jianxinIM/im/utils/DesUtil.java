package com.bcr.jianxinIM.im.utils;


import java.nio.charset.Charset;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
public class DesUtil {
    private static final String DES = "DES";
    private static final Charset CHARSET = Charset.forName("UTF8");

    /**
     * 加密
     *
     * @param src 明文
     * @param key 公匙
     * @return 密文
     */
    public static String encrypt(String src, String key) {
        byte[] s = src.getBytes(CHARSET);
        byte[] buf = des(s, key, Cipher.ENCRYPT_MODE);
        return parseByte2HexStr(buf);
    }

    /**
     * 解密
     *
     * @param data 密文
     * @param key  公钥
     * @return 明文
     */
    public static String decrypt(String data, String key) {
        byte[] d = parseHexStr2Byte(data);
        byte[] buf = des(d, key, Cipher.DECRYPT_MODE);
        return new String(buf, CHARSET);
    }

    /**
     * 将二进制转换成16进制
     */
    private static String parseByte2HexStr(byte[] buf) {
        StringBuilder sb = new StringBuilder();
        for (byte b : buf) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     */
    private static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1) {
            return null;
        }
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    /**
     * DES 加/解密 核心
     */
    private static byte[] des(byte[] src, String key, int mode) {
        try {
            byte[] k = key.getBytes();
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(k);
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey secureKey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance(DES);
            // 用密匙初始化Cipher对象
            cipher.init(mode, secureKey, random);
            // 真正开始解密操作
            return cipher.doFinal(src);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
