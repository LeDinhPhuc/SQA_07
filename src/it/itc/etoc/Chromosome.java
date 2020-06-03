/**
 * @(#) Chromosome.java	v. 1.0 - March 5, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;

import java.util.*;

import it.itc.etoc.Action;
import it.itc.etoc.BranchTarget;
import it.itc.etoc.Chromosome;
import it.itc.etoc.ChromosomeFormer;
import it.itc.etoc.ConstructorInvocation;
import it.itc.etoc.DataFlowTarget;
import it.itc.etoc.DataFlowTestGenerator;
import it.itc.etoc.MethodInvocation;
import it.itc.etoc.TestGenerator;

/**
 * Chromosome representation for evolutionary testing of classes.
 *
 * A chromosome encodes the information for the execution of one test case.
 * <p>
 * <img src=Chromosome.png width=550>
 * <p>
 * Examples:
 * 
 * <pre>
 * $x0=A(int[0;1]):$x1=B#null:$x0.m(int,$x1)@1,88
 * $x0=A():$x1=B(int[-2;2]):$x1.g1():$x0.m(int,$x1)@-1,42
 * </pre>
 */
public class Chromosome implements Comparable, Cloneable {
	/**
	 * A test case descriptor is either a constructor "$xN=A(...)" or a method call
	 * "$xN.m(...)": List&lt;Action&gt;.
	 */
	private List actions = new LinkedList();

	/**
	 * Branch targets covered when current chromosome is turned into a test case and
	 * executed.
	 */
	Collection coveredBranchTargets;

	/**
	 * Data flow targets covered when current chromosome is turned into a test case
	 * and executed.
	 */
	Set coveredDataFlowTargets = new HashSet();

	/**
	 * Number of branches to current target covered by Chromosome. Chỉ số đánh giá
	 * hàm fitness của Chromosome này
	 */
	int fitness = 0;

	/**
	 * Implements chromosome duplication.
	 */
	public Object clone() {
		List acts = new LinkedList();
		Iterator it = actions.iterator();
		while (it.hasNext()) {
			Action act = (Action) it.next();
			acts.add(act.clone());
		}
		return new Chromosome(acts);
	}

	/**
	 * Thứ tự nhiễm sắc thể dựa trên giá trị hàm F giảm dần Đem cái NST so sánh với
	 * cái NST này ****************************************************************
	 * Ordering of chromosomes is based on decreasing fitness.
	 */
	public int compareTo(Object o) {
		Chromosome id = (Chromosome) o;
		return id.fitness - fitness;
	}

	/**
	 * So sánh 2 NST có cùng giá trị hàm đánh giá F ko?***********************
	 * Equality of chromosomes is based on fitness.
	 */
	public boolean equals(Object o) {
		Chromosome id = (Chromosome) o;
		return fitness == id.fitness;
	}

	/**
	 * get fitness ************************************************************
	 * Accessor to fitness.
	 */
	public int getFitness() {
		return fitness;
	}
	// Xóa
	// /**
	// * Đường dẫn bao phủ được trả về từ TestCaseExecutor và tập quần thể truyền
	// cho
	// * từng NST thông số này *************************************************
	// *
	// * Covered path points are returned by TestCaseExecutor. Population transmits
	// * this data to each Chromosome.
	// */
	// public void setCoveredBranches(Set pathPoints) {
	// coveredBranchTargets = pathPoints;
	// }

	/**
	 * Target path của data flow được lấy từ TestCaseExecutor. Tập quần thể truyền
	 * thông tin này cho từng NST
	 * 
	 * Covered path points are returned by TestCaseExecutor. Population transmits
	 * this data to each Chromosome.
	 */
	public void setCoveredDataFlows(List pathPoints) {
		coveredBranchTargets = pathPoints;
		coveredDataFlowTargets = new HashSet();
		Iterator i = TestGenerator.getAllTargets().iterator();
		while (i.hasNext()) {
			DataFlowTarget tgt = (DataFlowTarget) i.next();
			BranchTarget source = tgt.getSourceBranch();
			BranchTarget dest = tgt.getDestinationBranch();
			boolean inDefClearPath = false;
			Iterator j = coveredBranchTargets.iterator();
			while (j.hasNext()) {
				BranchTarget x = (BranchTarget) j.next();
				if (inDefClearPath && dest.equals(x))
					coveredDataFlowTargets.add(tgt);
				if (DataFlowTestGenerator.isDef(x, tgt.getVariable()))
					inDefClearPath = false;
				if (source.equals(x))
					inDefClearPath = true;
			}
		}
	}

	/**
	 * Covered path points are returned by TestCaseExecutor. Population transmits
	 * this data to each Chromosome.
	 */
	public Set getCoveredTargets() {
		// Xóa
		// if (TestGenerator.dataFlowCoverage)
		// System.out.println(coveredDataFlowTargets + "
		// <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<=");
		return coveredDataFlowTargets;
		// else
		// return (Set) coveredBranchTargets;
	}

	/**
	 * Kiểm tra xem target path đã được bao phủ chưa
	 * 
	 * Check if target is among the covered branches.
	 *
	 * Prerequisite: coveredBranchTargets has been set previously by calling
	 * setCoveredBranches or setCoveredDataFlows.
	 */
	// Xóa
	// public boolean coversBranch(BranchTarget target) {
	// if (coveredBranchTargets == null)
	// return false;
	// // nếu coveredBranchTargets chứa target thì trả về true
	// if (coveredBranchTargets.contains(target))
	// return true;
	// return false;
	// }

	/**
	 * Kiểm tra xem target path đã được bao phủ chưa
	 * 
	 * Check if target is among the covered data flows.
	 *
	 * Prerequisite: coveredPathPoints has been set previously by calling
	 * setCoveredPathPoints.
	 */
	public boolean coversDataFlow(DataFlowTarget target) {
		return coveredDataFlowTargets.contains(target);
	}

	/**
	 * Xác định số nhánh trong path đã được bao phủ
	 * 
	 * Determines number of branches in path already covered.
	 */
	// Xóa
	// public int computeBranchFitness(Set path) {
	// fitness = 0;
	// Iterator i = path.iterator();
	// while (i.hasNext()) {
	// BranchTarget x = (BranchTarget) i.next();
	// if (coveredBranchTargets.contains(x))
	// fitness++;
	// }
	// return fitness;
	// }

	/**
	 * Xác định ( số nhánh trong path1 + số nhánh trong path2 ) ĐƯỢC BAO PHỦ bởi các
	 * nhánh con từ tgt source đến def(tgt.variable)
	 * 
	 * Determines number of branches in path1 already covered + number of branches
	 * in path2 <-> covered by the subtrace(s) from tgt.source to def(tgt.variable).
	 */
	public int computeDataFlowFitness(DataFlowTarget tgt, Set path1, Set path2) {
		fitness = 0;
		Set coveredBranches = new HashSet();
		coveredBranches.addAll(coveredBranchTargets);
		Iterator i = path1.iterator();
		while (i.hasNext()) {
			BranchTarget x = (BranchTarget) i.next();
			if (coveredBranches.contains(x))
				fitness++;
		}
		BranchTarget source = tgt.getSourceBranch();
		boolean inDefClearPath = false;
		i = coveredBranchTargets.iterator();
		while (i.hasNext()) {
			BranchTarget x = (BranchTarget) i.next();
			if (DataFlowTestGenerator.isDef(x, tgt.getVariable()))
				inDefClearPath = false;
			if (source.equals(x))
				inDefClearPath = true;
			if (inDefClearPath && path2.contains(x))
				fitness++;
		}
		return fitness;
	}

	/**
	 * Accessor to test case descriptors.
	 */
	public List getActions() {
		return actions;
	}

	/**
	 * Number of actions in chromosome.
	 */
	public int size() {
		return actions.size();
	}

	/**
	 * Gets ConstructorInvocation with given target as left hand side.
	 *
	 * @param objId Target object of constructor.
	 * @return ConstructorInvocation object with objId as target.
	 */
	private Action getConstructor(String objId) {
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (objId.equals(act.getObject()))
				return act;
		}
		return null;
	}

	/**
	 * Renames variables used for object identification.
	 *
	 * Example: $x2=B():$x1=A($x2) becomes: $x8=B():$x1=A($x8) if oldId=$x2 and
	 * newId=$x8.
	 */
	void renameObject(String oldId, String newId) {
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			String objId = act.getObject();
			if (objId.equals(oldId))
				act.setObject(newId);
			List newParameterValues = new LinkedList();
			List oldParameterValues = act.getParameterValues();
			if (oldParameterValues != null) {
				Iterator j = oldParameterValues.iterator();
				while (j.hasNext()) {
					String val = (String) j.next();
					if (val.equals(oldId))
						newParameterValues.add(newId);
					else
						newParameterValues.add(val);
				}
			} else {
				newParameterValues = null;
			}
			act.setParameterValues(newParameterValues);
		}
	}

	/**
	 * Renames variables used for object identification.
	 *
	 * Example: $x2=B():$x1=A($x2) becomes: $x8=B():$x7=A($x8)
	 */
	int renameObjects(int idCounter) {
		Map oldIdToNewId = new HashMap();
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			String objId = act.getObject();
			if (!oldIdToNewId.containsKey(objId))
				oldIdToNewId.put(objId, "$x" + (idCounter++));
			act.setObject((String) oldIdToNewId.get(objId));
			List newParameterValues = new LinkedList();
			List oldParameterValues = act.getParameterValues();
			if (oldParameterValues != null) {
				Iterator j = oldParameterValues.iterator();
				while (j.hasNext()) {
					String val = (String) j.next();
					if (val != null && val.startsWith("$")) {
						if (!oldIdToNewId.containsKey(val))
							oldIdToNewId.put(val, "$x" + (idCounter++));
						newParameterValues.add(oldIdToNewId.get(val));
					} else {
						newParameterValues.add(val);
					}
				}
			} else {
				newParameterValues = null;
			}
			act.setParameterValues(newParameterValues);
		}
		return idCounter;
	}

	/**
	 * sửa các điểm định nghĩa def và sử dụng used trong đoạn NST Fixes variable
	 * definitions and uses in chromosome fragment.
	 *
	 * Actions containing variables used (resp. defined) but not defined (resp.
	 * used) are removed.
	 */
	public void fixDefUse() {
		boolean change = true;
		while (change) {
			change = false;
			Set def = new HashSet();
			Set use = new HashSet();
			Set objectsToRemove = new HashSet();
			List newActions = new LinkedList();
			Iterator i = actions.iterator();
			while (i.hasNext()) {
				Action act = (Action) i.next();
				def.addAll(act.getDef());
				use.addAll(act.getUse());
			}
			i = use.iterator();
			while (i.hasNext()) {
				String objId = (String) i.next();
				if (!def.contains(objId))
					objectsToRemove.add(objId);
			}
			i = def.iterator();
			while (i.hasNext()) {
				String objId = (String) i.next();
				if (!use.contains(objId))
					objectsToRemove.add(objId);
			}
			i = actions.iterator();
			while (i.hasNext()) {
				Action act = (Action) i.next();
				if (!act.getDef().removeAll(objectsToRemove) && !act.getUse().removeAll(objectsToRemove))
					newActions.add(act);
				else
					change = true;
			}
			actions = newActions;
		}
	}

	/**
	 * Fixes the chromosome head produced by a mutation operator.
	 */
	public void fixHead() {
		fixDefUse();
	}

	/**
	 * Fixes the chromosome tail produced by a mutation operator.
	 */
	public void fixTail() {
		fixDefUse();
	}

	/**
	 * Randomly splits chromosome into two.
	 *
	 * Head and tail always contain at least one action (for the tail, the final
	 * method invocation is always included)
	 */
	public Chromosome[] randomSplitIntoTwo() {
		Chromosome[] parts = new Chromosome[2];
		LinkedList head = new LinkedList();
		LinkedList tail = new LinkedList();
		int splitIndex = 0;
		if (actions.size() > 1)
			splitIndex = ChromosomeFormer.randomGenerator.nextInt(actions.size());
		int k = 0;
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (k <= splitIndex)
				head.add(act);
			else
				tail.add(act);
			k++;
		}
		parts[0] = new Chromosome(head);
		parts[1] = new Chromosome(tail);
		// if (tail.size() == 0)
		// parts[1] = new Chromosome(head);
		return parts;
	}

	/**
	 * Builds chromosome from list of test case descriptors.
	 */
	public Chromosome(List acts) {
		actions = acts;
	}

	/**
	 * Builds chromosome.
	 */
	public Chromosome() {
	}

	/**
	 * String representation of Chromosome.
	 *
	 * Example:
	 * 
	 * <pre>
	 * $x0=A():$x1=B(int):$x1.c():$x0.m(int, $x1) @ 1, 4
	 * </pre>
	 */
	public String toString() {
		String s = "";
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (s.equals(""))
				s = act.actionDescription();
			else
				s += ":" + act.actionDescription();
		}
		s += "@";
		i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			String actVals = act.actualValues();
			if (!actVals.equals("")) {
				if (s.endsWith("@"))
					s += actVals;
				else
					s += "," + actVals;
			}
		}
		return s;
	}

	/**
	 * java code representation of Chromosome.
	 *
	 * Example:
	 * 
	 * <pre>
	 * $x0=A():$x1=B(int):$x1.c():$x0.m(int, $x1) @ 1, 4
	 * </pre>
	 * 
	 * becomes:
	 * 
	 * <pre>
	 * A x0 = new A();
	 * B x1 = B(1);
	 * x1.c();
	 * x0.m(4, x1) @ 1, 4
	 * </pre>
	 */
	public String toCode() {
		String s = "";
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			s += act.toCode() + "\n";
		}
		return s;
	}

	/**
	 * Determines the variable $xN assigned to an object of a given class.
	 *
	 * Scans the chromosome until the allocation of an object of a given class is
	 * encountered. The left hand side variable is returned.
	 *
	 * @param className class of the searched object
	 * @return String representation of searched object variable (or null)
	 */
	public String getObjectId(String className) {
		if (className.indexOf("[") != -1)
			className = className.substring(0, className.indexOf("["));
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action a = (Action) i.next();
			if (className.equals(a.getName()))
				return a.getObject();
		}
		return null;
	}

	/**
	 * Determines the variable $xN assigned to an object of a class from a given
	 * class list.
	 *
	 * @param classes List of classes the object may belong to
	 * @return String representation of searched object variable (or null)
	 */
	public String getObjectId(List classes) {
		Iterator i = classes.iterator();
		while (i.hasNext()) {
			String cl = (String) i.next();
			String objId = getObjectId(cl);
			if (objId != null)
				return objId;
		}
		return null;
	}

	/**
	 * Determines the variable $xN on which the final method call is issued.
	 */
	public String getObjectIdOfFinalMethodCall() {
		Action finalCall = (Action) actions.get(actions.size() - 1);
		return finalCall.getObject();
	}

	/**
	 * Determines if all object allocations necessary for the final method call are
	 * available.
	 */
	public boolean finalMethodCallParametersExist() {
		Action finalCall = (Action) actions.get(actions.size() - 1);
		List params = finalCall.getParameterObjects();
		Map availableObjects = new HashMap();
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (act instanceof ConstructorInvocation) {
				availableObjects.put(act.getObject(), act);
			}
		}
		List requiredObjects = new LinkedList();
		requiredObjects.addAll(params);
		while (requiredObjects.size() > 0) {
			String objId = (String) requiredObjects.remove(0);
			if (!availableObjects.containsKey(objId))
				return false;
			Action act = (Action) availableObjects.get(objId);
			requiredObjects.addAll(act.getParameterObjects());
		}
		return true;
	}

	/**
	 * Adds action to input descriptors.
	 *
	 * @param act Action to be added.
	 */
	public void addAction(Action act) {
		actions.add(act);
	}

	/*
	 * Fuses two chromosomes together.
	 *
	 * Example: <pre> $x0=A(int)@10 $x1.m($x0,int)@21
	 *
	 * $x0=A(int):$x1.m($x0,int)@10,21 </pre>
	 *
	 * @param chrom Chromosome to be appended.
	 */
	public void append(Chromosome chrom) {
		actions.addAll(chrom.actions);
	}

	/**
	 * Mutation operator: randomly changes one of the input values.
	 */
	public void changeInputValue() {
		int valNum = 0;
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			valNum += act.countPrimitiveTypes();
		}
		if (valNum == 0)
			return;
		int inputIndex = ChromosomeFormer.randomGenerator.nextInt(valNum);
		int k = 0;
		i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			int actValNum = act.countPrimitiveTypes();
			if (k <= inputIndex && k + actValNum > inputIndex) {
				act.changeInputValue(inputIndex - k);
				break;
			}
			k += actValNum;
		}
	}

	/**
	 * Mutation operator: randomly replaces a constructor.
	 *
	 */
	public void replaceConstructor(ChromosomeFormer chromosomeFormer) {
		List newActions = new LinkedList();
		int constrNum = 0;
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (act instanceof ConstructorInvocation)
				constrNum++;
		}
		if (constrNum == 0)
			return;
		int constrIndex = ChromosomeFormer.randomGenerator.nextInt(constrNum);
		int k = 0;
		i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (k == constrIndex && act instanceof ConstructorInvocation) {
				String className = act.getName();
				String objId = act.getObject();
				Chromosome chrom = chromosomeFormer.buildConstructor(className, objId);
				newActions.addAll(chrom.getActions());
			} else {
				newActions.add(act);
			}
			if (act instanceof ConstructorInvocation)
				k++;
		}
		actions = newActions;
		fixDefUse();
	}

	/**
	 * Mutation operator: randomly removes a method invocation.
	 */
	public void removeMethodCall() {
		List newActions = new LinkedList();
		int methodCallNum = 0;
		Iterator i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (act instanceof MethodInvocation)
				methodCallNum++;
		}
		if (methodCallNum <= 1)
			return;
		methodCallNum--; // Last method call must be excluded.
		int methodCallIndex = ChromosomeFormer.randomGenerator.nextInt(methodCallNum);
		int k = 0;
		i = actions.iterator();
		while (i.hasNext()) {
			Action act = (Action) i.next();
			if (!(k == methodCallIndex && act instanceof MethodInvocation))
				newActions.add(act);
			if (act instanceof MethodInvocation)
				k++;
		}
		actions = newActions;
		fixDefUse();
	}

}
