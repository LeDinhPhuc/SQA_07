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

import it.itc.etoc.Chromosome;

abstract class Target {
  /**
   * For targets with sub-targets (e.g., methods having branches as
   * sub-targets). By default, it returns the current target.
   */
  public List<Target> getSubTargets() {
    List<Target> subTargets = new LinkedList<Target>();
    subTargets.add(this);
    return subTargets;
  }

  /**
   * ít nhất 1 cá thể nào đó bao phủ target
   * At least one individual in population covers target.
   */
  public abstract boolean coveredBy(Chromosome id);

}
