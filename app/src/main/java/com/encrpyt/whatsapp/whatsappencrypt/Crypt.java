package com.encrpyt.whatsapp.whatsappencrypt;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * This is Crypt Class. It encrypts and decrypts texts using using multiple encryption algorithm
 */
public class Crypt {
    private static final String ALGORITHM = "AES";
    private String Number1;
    private String Number2;

    /**
     * @param num1 Either Sender or receiver number depending whether the text is being sent or received
     * @param num2 Either Sender or receiver number depending whether the text is being sent or received
     */
    public Crypt(String num1, String num2) {
        this.Number1 = num1;
        this.Number2 = num2;
    }

    /**
     * @return Generates a key for AES Algorithm by hashing two numbers using SHA-256 algorithm
     * @throws NoSuchAlgorithmException
     */
    private byte[] getSHA256Key() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Log.e("Key",Number1 + " " + Number2);
        return digest.digest((Number1 + Number2).getBytes());
    }


    /**
     * @param plainText The text which is to be encrypted
     * @return Return Encrypted string using multiple algorithms
     * @throws IllegalBlockSizeException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public String encrypt(String plainText) throws Exception {

        String OTPCrypted = OTPEncrypt(plainText);
        return "#" + AESEncrypt(OTPCrypted) + "#";
    }

    /**
     * @param cipherText The text which is to be decrypted
     * @return Return Decrypted string using multiple algorithms
     * @throws Exception
     */
    public String decrypt(String cipherText) throws Exception {
        cipherText = cipherText.substring(1,cipherText.length()-1);
        Log.e("Servoce",cipherText);
        String AESDecrypted = AESDecrypt(cipherText);
        return OTPDecrypt(AESDecrypted);
    }

    /**
     * @param OTPCrypted Takes the OTP Encrypted text for AES Encryption
     * @return Returns AES Encrypted text
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private String AESEncrypt(String OTPCrypted) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKeySpec secretKey = new SecretKeySpec(getSHA256Key(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        return new String(Base64.encode(cipher.doFinal(OTPCrypted.getBytes()), Base64.DEFAULT));
    }

    /**
     * @param cipherText The text to be decrypted by AES Algorithm
     * @return Return AES Decrypted text
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    private String AESDecrypt(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        SecretKeySpec secretKey = new SecretKeySpec(getSHA256Key(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        String x = new String(cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT)));
        Log.e("Crypt AES Decrypt",x);
        return x;
    }

    /**
     * @param plainText The text to be encrypted
     * @return Return string which contains OTP and Base64 encrypted text along with Base64 encrypted keys
     */
    private String OTPEncrypt(String plainText) throws NoSuchProviderException, NoSuchAlgorithmException {
        final byte[] plainTextByte = plainText.getBytes();
        final int textByteLen = plainTextByte.length;
        byte[] encoded = new byte[textByteLen];
        byte[] key = new byte[textByteLen];
        new SecureRandom().nextBytes(key);
//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG","Crypto");
//        sr.nextBytes(key);
        for (int i = 0; i < textByteLen; i++) {
            encoded[i] = (byte) (plainTextByte[i] ^ key[i]);
        }
        return new String(Base64.encode(encoded, Base64.DEFAULT)) + new String(Base64.encode(key, Base64.DEFAULT));
    }

    /**
     * @param encryptedText Takes AES Decrypted text for Decryption using OTP Algortihm
     * @return Return the original text by OTP Decryption
     */
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
//        https://ideone.com/otkxeL
    }
}