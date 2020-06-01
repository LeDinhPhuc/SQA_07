/**
 * @(#) Target.java	v. 1.0 - March 20, 2004
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
import it.itc.etoc.DataFlowTarget;
import it.itc.etoc.Target;

class DataFlowTarget extends Target {

  int source;
  int destination;
  String variable;
  static int HASH_CODE_MAGIC_NUMBER = 1341;

  /**
   * Each target is a data dependence.
   */
  public DataFlowTarget(int src, int dst, String var) {
    source = src;
    destination = dst;
    variable = var;
  }

  /**
   * Used in Map's.
   */
  public int hashCode() {
    return source * HASH_CODE_MAGIC_NUMBER * HASH_CODE_MAGIC_NUMBER +
      destination * HASH_CODE_MAGIC_NUMBER + variable.hashCode();
  }

  /**
   * Used in Map's.
   */
  public boolean equals(Object obj) {
    DataFlowTarget tgt = (DataFlowTarget)obj;
    return source == tgt.source && destination == tgt.destination &&
      variable.equals(tgt.variable);
  }

  /**
   * Used in print's.
   */
  public String toString() {
    return "(" + source + ", " + destination + ", " + variable + ")";
  }

  /**
   * At least one individual in population covers data flow.
   */
  public boolean coveredBy(Chromosome id) {
    return id.coversDataFlow(this);
  }

  /**
   * Source of data dependence.
   */
  public BranchTarget getSourceBranch() {
    return new BranchTarget(source);
  }

  /**
   * Source of data dependence.
   */
  public BranchTarget getDestinationBranch() {
    return new BranchTarget(destination);
  }

  /**
   * Variable defined in data dependence.
   */
  public String getVariable() {
		return variable;
	}
}
