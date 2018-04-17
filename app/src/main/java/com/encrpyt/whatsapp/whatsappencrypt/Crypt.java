package com.encrpyt.whatsapp.whatsappencrypt;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    private static final String ALGORITHM = "AES";
    private String Number1;
    private String Number2;

    public Crypt(String num1, String num2) {
        this.Number1 = num1;
        this.Number2 = num2;
    }

    private byte[] getSHA256Key() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Log.e("Crypt Key",Number1 + " " + Number2);
        return digest.digest((Number1 + Number2).getBytes());
    }

    public byte[] fileCrypt(byte[] inputBytes,int mode) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {if(inputBytes != null)  return inputBytes;
        Key secretKey = new SecretKeySpec(getSHA256Key(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, secretKey);

        return cipher.doFinal(inputBytes);
    }

    public byte[] fileEncrypt(byte[] inputBytes) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return fileCrypt(inputBytes,Cipher.ENCRYPT_MODE);
    }

    public byte[] fileDecrypt(byte[] inputBytes) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        return fileCrypt(inputBytes,Cipher.DECRYPT_MODE);
    }

    public String encrypt(String plainText) throws Exception {

        String OTPCrypted = OTPEncrypt(plainText);
        String temp =  "#" + AESEncrypt(OTPCrypted) + "#";
        Log.e("Crypt encrypt final",temp);
        return temp;
    }

    public String decrypt(String cipherText) throws Exception {
        Log.e("Crypt non strip",cipherText);
        cipherText = cipherText.substring(1,cipherText.length()-1);
        Log.e("Crypt strip",cipherText);
        String AESDecrypted = AESDecrypt(cipherText);
        String temp =  OTPDecrypt(AESDecrypted);
        Log.e("Crypt Decrypted final",temp);
        return temp;
    }

    private String AESEncrypt(String OTPCrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKeySpec secretKey = new SecretKeySpec(getSHA256Key(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return new String(Base64.encode(cipher.doFinal(OTPCrypted.getBytes()), Base64.DEFAULT));
    }

    private String AESDecrypt(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKeySpec secretKey = new SecretKeySpec(getSHA256Key(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String x = new String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)));
        Log.e("Crypt AES Decrypt",x);
        return x;
    }

    private String OTPEncrypt(String plainText) throws NoSuchProviderException, NoSuchAlgorithmException {
        final byte[] plainTextByte = plainText.getBytes();
        final int textByteLen = plainTextByte.length;
        byte[] encoded = new byte[textByteLen];
        byte[] key = new byte[textByteLen];
        new SecureRandom().nextBytes(key);
        for (int i = 0; i < textByteLen; i++) {
            encoded[i] = (byte) (plainTextByte[i] ^ key[i]);
        }
        return new String(Base64.encode(encoded, Base64.DEFAULT)) + new String(Base64.encode(key, Base64.DEFAULT));
    }

    private String OTPDecrypt(String encryptedText) {
        final int length = encryptedText.length() / 2;
        final byte[] Base64key = encryptedText.substring(0, length).getBytes();
        final byte[] Base64text = encryptedText.substring(length).getBytes();
        final byte[] text = Base64.decode(Base64text, Base64.DEFAULT);
        final byte[] key = Base64.decode(Base64key, Base64.DEFAULT);
        final byte[] decoded = new byte[length];
        for (int i = 0; i < text.length; i++) {
            decoded[i] = (byte) (text[i] ^ key[i]);
        }
        return new String(decoded);
    }
}