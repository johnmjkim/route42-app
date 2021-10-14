package com.comp6442.route42.utils;

import org.junit.Assert;
import org.junit.Test;

public class CryptoTest {
  @Test
  public void testEncryptDecrypt1() throws Exception {
    String plainTextPassword = "test1234";
    Assert.assertEquals(plainTextPassword, Crypto.decodeAndDecrypt(Crypto.encryptAndEncode(plainTextPassword)));
  }

  @Test
  public void testEncryptDecrypt2() throws Exception {
    String plainTextPassword = "RU2bEYZgkUsr";
    Assert.assertEquals(plainTextPassword, Crypto.decodeAndDecrypt(Crypto.encryptAndEncode(plainTextPassword)));
  }

  @Test
  public void testEncryptDecrypt3() throws Exception {
    String plainTextPassword = "w4Uzb.j*cT6*";
    Assert.assertEquals(plainTextPassword, Crypto.decodeAndDecrypt(Crypto.encryptAndEncode(plainTextPassword)));
  }
}