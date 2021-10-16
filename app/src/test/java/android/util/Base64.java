package android.util;
// copied from https://stackoverflow.com/questions/49109709/how-to-mock-base64-in-android

public class Base64 {
  public static String encodeToString(byte[] input, int flags) {
    return java.util.Base64.getEncoder().encodeToString(input);
  }

  public static byte[] decode(byte[] bytestr, int flags) {
    return java.util.Base64.getDecoder().decode(bytestr);
  }
}