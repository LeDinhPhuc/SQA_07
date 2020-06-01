/**
 * @(#) MethodInvocation.java	v. 1.0 - March 5, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;
import java.util.regex.*;

import it.itc.etoc.Action;
import it.itc.etoc.MethodInvocation;


/**
 * Method invocation in a chromosome test case descriptor.
 *
 */
public class MethodInvocation extends Action {
  /**
   * Builds a MethodInvocation action.
   *
   * @param objVar        Target of method invocation ($xN in $xN.m());
   * @param methodName    Method name (m in $xN.m()).
   * @param formalParams  Parameter types.
   * @param vals          Input values (e.g., "$x0", "23")
   */
  MethodInvocation(String objVar, String methodName, 
			List formalParams, List vals) {
    targetObject = objVar;
    name = methodName;
    parameterTypes = formalParams;
    parameterValues = vals;
  }

  /**
   * Used when cloning chromosomes.
   */
  public Object clone() {
    return new MethodInvocation(targetObject, name,
				parameterTypes, 
				parameterValues);
  }

  /*
   * Method prefix consists of an attribute access on the target object.
   *
   * Example: "$x0.m", where the method invocation is $x0.m(int)
   */
  String actionPrefix() {
    return targetObject + "." + name;
  }

  /*
   * Java code for method call.
   *
   * Example: "x0.m(4);", where the chromosome action is $x0.m(int)@4
   */
  String toCode() {
    String s = "    ";
    s += targetObject.substring(1) + "." + name;
    s += "(";
    Iterator j = parameterValues.iterator();
    while (j.hasNext()) {
      String param = (String)j.next();
      if (param.startsWith("$"))
	param = param.substring(1);
      if (s.endsWith("(")) s += param;
      else s += ", " + param;
    }
    s += ");";
    return s;
  }

  /**
   * Variables used by this action (e.g., {$x1, $x2} for $x1.A($x2)).
   */
  Set getUse() {
    Set use = new HashSet();
    use.add(targetObject);
    Iterator i = getParameterObjects().iterator();
    while (i.hasNext())
      use.add(i.next());
    return use;
  }

}

