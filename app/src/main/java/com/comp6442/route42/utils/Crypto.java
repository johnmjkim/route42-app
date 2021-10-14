package com.comp6442.route42.utils;
// copied from https://stackoverflow.com/questions/41223937/how-can-i-encrypte-my-password-android-studio with some modifications

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {
  private static String IV = "IV_VALUE_16_BYTE";
  private static String PASSWORD = "iwx8u4D7kbiT";
  private static String SALT = "RU2bEYZgkUsr";

  public static String encryptAndEncode(String raw) {
    try {
      Cipher c = getCipher(Cipher.ENCRYPT_MODE);
      byte[] encryptedVal = c.doFinal(getBytes(raw));
      String s = Base64.encodeToString(encryptedVal, Base64.DEFAULT);
      return s;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  public static String decodeAndDecrypt(String encrypted) throws Exception {
    byte[] decodedValue = Base64.decode(getBytes(encrypted), Base64.DEFAULT);
    Cipher c = getCipher(Cipher.DECRYPT_MODE);
    byte[] decValue = c.doFinal(decodedValue);
    return new String(decValue);
  }

  private static byte[] getBytes(String str) {
    return str.getBytes(StandardCharsets.UTF_8);
  }

  private static Cipher getCipher(int mode) throws Exception {
    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
    byte[] iv = getBytes(IV);
    c.init(mode, generateKey(), new IvParameterSpec(iv));
    return c;
  }

  private static Key generateKey() throws Exception {
    SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    char[] password = PASSWORD.toCharArray();
    byte[] salt = getBytes(SALT);

    KeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
    SecretKey tmp = factory.generateSecret(spec);
    byte[] encoded = tmp.getEncoded();
    return new SecretKeySpec(encoded, "AES");
  }
}