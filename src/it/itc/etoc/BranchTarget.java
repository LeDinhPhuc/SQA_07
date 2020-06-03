package it.itc.etoc;
import it.itc.etoc.BranchTarget;
import it.itc.etoc.Chromosome;
import it.itc.etoc.Target;

class BranchTarget extends Target {
  /**
   * Branch to be covered.
   */
  int branch;

  /**
   * Branch to be covered is the only parameter.
   */
  public BranchTarget(int br) {
    branch = br;
  }

  /**
   * Used in Map's.
   */
  public int hashCode() {
    return branch;
  }

  /**
   * Used in Map's.
   */
  public boolean equals(Object obj) {
    BranchTarget tgt = (BranchTarget)obj;
    return branch == tgt.branch;
  }

  /**
   * Used in print's.
   */
  public String toString() {
    return Integer.toString(branch);
  }

  /**
   * At least one individual in population covers branch.
   */
  // Xóa
  public boolean coveredBy(Chromosome id) {
	return false;
//    return id.coversBranch(this);
  }
}
