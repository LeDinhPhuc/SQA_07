/**
 * @(#) Action.java	v. 1.0 - March 18, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;

import it.itc.etoc.Action;
import it.itc.etoc.ChromosomeFormer;


/**
 * Action in a chromosome test case descriptor.
 *
 */
public class Action implements Cloneable {
  /**
   * Stores the target object.
   */
  String targetObject;

  /**
   * Action name.
   */
  String name;

  /**
   * Formal parameter types: List&lt;String&gt;
   *
   * Example: ("int", "B", "A", "int"). 
   */
  List parameterTypes = new LinkedList();

  
  /**
   * Actual parameter values: List&lt;String&gt;
   *
   * Example: ("12", "$x2", "$x0", "23")
   */
  List parameterValues = new LinkedList();

  /**
   * Used when cloning chromosomes.
   */
  public Object clone() {
    Action act = new Action();
    act.targetObject = targetObject;
    act.name = name;
    act.parameterTypes = parameterTypes;
    act.parameterValues = parameterValues;
    return act;
  }

  /**
   * Accessor to actual parameter values: List&lt;String&gt;
   *
   * Example: ("12", "$x2", "$x0", "23")
   */
  public List getParameterValues() {
    return parameterValues;
  }

  /**
   * Assigns actual parameter values: List&lt;String&gt;
   */
  public void setParameterValues(List newParameterValues) {
    parameterValues = newParameterValues;
  }

  /**
   * Accessor to parameter objects only: List&lt;String&gt;
   *
   * Example: ("$x2", "$x0") from ("12", "$x2", "$x0", "23")
   */
  public List getParameterObjects() {
    List paramObjects = new LinkedList();
    if (parameterTypes == null || parameterValues == null) 
      return paramObjects;
    Iterator i = parameterTypes.iterator();
    Iterator j = parameterValues.iterator();
    while (i.hasNext() && j.hasNext()) {
      String paramType = (String)i.next();
      String param = (String)j.next();
      if (!ChromosomeFormer.isPrimitiveType(paramType)) 
	paramObjects.add(param);
    }
    return paramObjects;
  }

  /**
   * Accessor to target object.
   */
  String getObject() {
    return targetObject;
  }

  /**
   * Assigns target object.
   */
  void setObject(String newTargetObject) {
    targetObject = newTargetObject;
  }

  /**
   * Accessor to action name.
   */
  String getName() {
    return name;
  }

  /**
   * Variables defined at this action (e.g., {$x1} for $x1=A()).
   */
  Set getDef() {
    return new HashSet();
  }

  /**
   * Variables used by this action (e.g., {$x1, $x2} for $x1.A($x2)).
   */
  Set getUse() {
    return new HashSet();
  }

  /**
   * Code representation of an action.
   *
   * Delegated to subclasses.
   */
  String toCode() {
    return "";
  }

  /**
   * String representation of an action.
   */
  String actionDescription() {
    return actionPrefix() + parameterDescription();
  }

  /**
   * Action prefix is delegated to subclasses.
   */
  String actionPrefix() {
    return "";
  }

  /**
   * String representation of parameter types.
   */
  String parameterDescription() {
    if (parameterTypes == null || parameterValues == null) return "";
    String s = "(";
    Iterator i = parameterTypes.iterator();
    Iterator j = parameterValues.iterator();
    while (i.hasNext() && j.hasNext()) {
      String param = (String)i.next();
      String paramId = (String)j.next();
      if (!ChromosomeFormer.isPrimitiveType(param))
	param = paramId;
      if (s.equals("(")) s += param;
      else s += "," + param;
    }
    s += ")";
    System.out.printf("%s\n" , s);
    return s;
  }

  /**
   * String representation of parameter values.
   */
  String actualValues() {
    if (parameterValues == null || parameterTypes == null) return "";
    String s = "";
    Iterator i = parameterValues.iterator();
    Iterator j = parameterTypes.iterator();
    while (i.hasNext() && j.hasNext()) {
      String paramVal = (String)i.next();
      String paramType = (String)j.next();
      if (ChromosomeFormer.isPrimitiveType(paramType)) {
	if (s.equals("")) s += paramVal;
	else s += "," + paramVal;
      }
    }
    return s;
  }

  /**
   * Number of primitive types.
   */
  public int countPrimitiveTypes() {
    int n = 0;
    if (parameterValues == null || parameterTypes == null) return n;
    Iterator i = parameterTypes.iterator();
    while (i.hasNext()) {
      String paramType = (String)i.next();
      if (ChromosomeFormer.isPrimitiveType(paramType))
	n++;
    }
    return n;
  }

  /**
   * Randomly changes a value of this action.
   *
   * @param valIndex  Index of the primitive type value to change.
   */
  public void changeInputValue(int valIndex) {
    if (parameterValues == null || parameterTypes == null) return;
    List newParamVals = new LinkedList();
    int k = 0;
    Iterator i = parameterValues.iterator();
    Iterator j = parameterTypes.iterator();
    while (i.hasNext() && j.hasNext()) {
      String paramVal = (String)i.next();
      String paramType = (String)j.next();
      if (ChromosomeFormer.isPrimitiveType(paramType) && k == valIndex) {
	String newVal = ChromosomeFormer.buildValue(paramType);
	newParamVals.add(newVal);
      } else {
	newParamVals.add(paramVal);
      }
      if (ChromosomeFormer.isPrimitiveType(paramType))
	k++;
    }
    parameterValues = newParamVals;
  }

}

