package gnu.util;


/** 
 * Common math-related utility.
 * Clamp is a Math operation that allow us to verify if our value is between a
 * limit, if it's not we attribute the respective limit.
 * 
 * For example, suppose we can only have values between -2 and 3.
 * Clamp(1, -2, 3) = 1
 * Clamp(-8, -2, 3) = -2
 * Clamp(3.4, -2, 3) = 3.4
 */  
public class Math {
  public static double clamp (double i, double low, double high) {
    return java.lang.Math.max (java.lang.Math.min (i, high), low);
  }


  public static float clamp (float i, float low, float high) {
    return java.lang.Math.max (java.lang.Math.min (i, high), low);
  }


  public static int clamp (int i, int low, int high) {
    return java.lang.Math.max (java.lang.Math.min (i, high), low);
  }


  public static long clamp (long i, long low, long high) {
    return java.lang.Math.max (java.lang.Math.min (i, high), low);
  }
}
