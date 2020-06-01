/**
 * @(#) MethodTarget.java	v. 1.0 - March 20, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.*;

import it.itc.etoc.BranchTarget;
import it.itc.etoc.Chromosome;
import it.itc.etoc.Target;

class MethodTarget extends Target {
  /**
   * Method to be covered.
   */
  String method;

  /**
   * Branches to be covered inside method.
   */
  List branches; // List<BranchTarget>

  /**
   * Branches to be covered are added by successive method invocations.
   */
  public MethodTarget(String meth) {
    method = meth;
    branches = new LinkedList();
  }

  /**
   * Adds a branch belonging to method to those to be covered.
   */
  public void addBranch(int n) {
    branches.add(new BranchTarget(n));
  }

  /**
   * Gives target method.
   */
  public String getMethod() {
    return method;
  }

  /**
   * Overrides default and returns branches in given method.
   */
  public List getSubTargets() {
    return branches;
  }

  /**
   * Must never be called on composite targets.
   */
  public boolean coveredBy(Chromosome id) {
    System.err.println("Error: coveredBy called on a composite target.");
    System.exit(1);
    return false;
  }
}
