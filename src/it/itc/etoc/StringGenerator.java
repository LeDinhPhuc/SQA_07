/**
 * @(#) StringGenerator.java	v. 1.0 - August 13, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;

import it.itc.etoc.StringGenerator;

public class StringGenerator {
  public static Random randomGenerator = new Random();


  /** 
   * Randomly generates new String.
   *
   * Null String is generated with probability nullProb.
   */
  public String newString(double nullProb) {
    int n = randomGenerator.nextInt(100);
    if (n <= nullProb * 100) return null;
    return newString();
  }

  /** 
   * Randomly generates new String.
   *
   * Uniform selection of character to insert.
   * Probability of length n is (1/2)^(n+1).
   * Only alphanumeric strings are generated.
   */
  public String newString() {
    int n = ('z' - 'a') + 1 + ('Z' - 'A') + 1 + ('9' - '0') + 1;
    char chars[] = new char[n];
    int i = 0;
    for (char c = 'a' ; c <= 'z' ; c++) 
      chars[i++] = c;
    for (char c = 'A' ; c <= 'Z' ; c++) 
      chars[i++] = c;
    for (char c = '0' ; c <= '9' ; c++) 
      chars[i++] = c;
    String s = "";
    int addMoreChars = randomGenerator.nextInt(100);
    while (addMoreChars < 50) {
      n = randomGenerator.nextInt(chars.length);
      s += chars[n];
      addMoreChars = randomGenerator.nextInt(100);
    }
    return s;
  }

  /** 
   * Randomly generates new String which respects given regular expression.
   *
   * @param regexp regular expression describing the String to be
   *               generated. 
   */
  public String newString(String regexp) {
    return "to be implemented";    
  }

  public static void main(String args[]) {
    StringGenerator gen = new StringGenerator();
    while(true)
      System.out.println(gen.newString());
  }
}


