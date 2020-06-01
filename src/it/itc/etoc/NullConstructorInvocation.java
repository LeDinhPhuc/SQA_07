/**
 * @(#) NullConstructorInvocation.java	v. 1.0 - August 13, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;
import java.util.regex.*;

import it.itc.etoc.ConstructorInvocation;
import it.itc.etoc.NullConstructorInvocation;


/**
 * Constructor invocation returning null.
 *
 */
public class  NullConstructorInvocation extends ConstructorInvocation {
  /**
   * Builds a NullConstructorInvocation action.
   *
   * @param objVar        Left hand side of constructor invocation $xN=A();
   * @param constrName    Constructor name.
   */
  NullConstructorInvocation(String objVar, String constrName) {
    super(objVar, constrName, null, null);
  }

  /**
   * Used when cloning chromosomes.
   */
  public Object clone() {
    return new NullConstructorInvocation(targetObject, name);
  }

  /*
   * Constructor prefix consists of an object assignment.
   *
   * Example: "$x0=null"
   */
  String actionPrefix() {
    return targetObject + "=" + name + "#null";
  }

  /*
   * Java code for constructor call.
   *
   * Example: "A x0 = null;", where the chromosome action is $x0=A[null]@
   */
  String toCode() {
    String s = "    ";
    s += name + " " + targetObject.substring(1) + " = null;";
    return s;
  }

  
  
}
