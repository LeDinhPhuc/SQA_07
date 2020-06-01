/**
 * @(#) MethodSignature.java	v. 1.0 - March 5, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;

/**
 * Manages method/constructor signatures.
 */
public class MethodSignature {
  /**
   * Method name.
   *
   * <p>
   * Example: "m" for method with full name A.m(int,B).
   */
  private String name;

  /**
   * Method parameters.
   *
   * <p>
   * Example: ("int", "B") for method with full name A.m(int,B).
   */
  private List parameters;

  /**
   * Returns method name
   *
   * @return name method name: String
   */
  public String getName() {
    return name;
  }

  /**
   * Returns method parameters
   *
   * @return method parameters: List&lt;String&gt;
   */
  public List getParameters() {
    return parameters;
  }

  /**
   * Builds a method signature out of name and parameters.
   *
   * @param name        method name
   * @param parameters  List&lt;String&gt;, 
   * where each String is a parameter type.
   */
  public MethodSignature(String name, List parameters) {
    this.name = name;
    this.parameters = parameters;
  }
}

