package com.encrpyt.whatsapp.whatsappencrypt;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    private static final String ALGORITHM = "AES/ECB/PKCS7Padding";
    private byte[] key;

    public Crypt(byte[] key) {
        this.key = key;
    }

    public String encrypt(byte[] plainText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return new String(Base64.encode(cipher.doFinal(plainText), Base64.DEFAULT));
    }

    public String decrypt(byte[] cipherText) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        return new String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)));
    }
}