/**
 * @(#) ConstructorInvocation.java	v. 1.0 - March 5, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distrubuted under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;
import java.util.regex.*;

import it.itc.etoc.Action;
import it.itc.etoc.ConstructorInvocation;


/**
 * Constructor invocation in a chromosome test case descriptor.
 *
 */
public class ConstructorInvocation extends Action {
  /**
   * Builds a ConstructorInvocation action.
   *
   * @param objVar        Left hand side of constructor invocation $xN=A();
   * @param constrName    Constructor name.
   * @param formalParams  Parameter types.
   * @param vals          Input values (e.g., "$x0", "23")
   */
  ConstructorInvocation(String objVar, String constrName, 
			List formalParams, List vals) {
    targetObject = objVar;
    name = constrName;
    parameterTypes = formalParams;
    parameterValues = vals;
  }

  /**
   * Used when cloning chromosomes.
   */
  public Object clone() {
    return new ConstructorInvocation(targetObject, name,
				     parameterTypes, 
				     parameterValues);
  }

  /*
   * Constructor prefix consists of an object assignment.
   *
   * Example: "$x0=A", where the constructor invocation is $x0=A(int)
   */
  String actionPrefix() {
    return targetObject + "=" + name;
  }

  /*
   * Java code for constructor call.
   *
   * Example: "A x0 = new A(4);", where the chromosome action is $x0=A(int)@4
   */
  String toCode() {
    String s = "    ";
    s += name + " " + targetObject.substring(1) + " = new " + name;
    s += "(";
    Iterator j = parameterValues.iterator();
    while (j.hasNext()) {
      String param = (String)j.next();
      if (param == null) param = "null";
      if (param.startsWith("$"))
	param = param.substring(1);
      if (s.endsWith("(")) s += param;
      else s += ", " + param;
    }
    s += ");";
    return s;
  }

  /**
   * Variables used by this action (e.g., {$x2} for $x1.A($x2)).
   */
  Set getUse() {
    Set use = new HashSet();
    Iterator i = getParameterObjects().iterator();
    while (i.hasNext())
      use.add(i.next());
    return use;
  }

  /**
   * Variables defined at this action (e.g., {$x1} for $x1=A()).
   */
  Set getDef() {
    Set def = new HashSet();
    def.add(targetObject);
    return def;
  }

}

