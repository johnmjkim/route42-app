package com.comp6442.route42.utils;
// copied from https://stackoverflow.com/questions/41223937/how-can-i-encrypte-my-password-android-studio with some modifications

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt {
  private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
  private static final String KEY = "iwx8u4D7kbiT";

  public static String encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
    Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, generateKey());
    byte[] encryptedByteValue = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
  }

  public static String decrypt(String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
    Cipher cipher = Cipher.getInstance(AESCrypt.ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, generateKey());
    byte[] decryptedByteValue = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
    return new String(decryptedByteValue, StandardCharsets.UTF_8);
  }

  private static Key generateKey() {
    return new SecretKeySpec(AESCrypt.KEY.getBytes(), AESCrypt.ALGORITHM);
  }
}
