

import it.itc.etoc.*;

public class BinaryTreeDataFlow {

	private BinaryTreeNode root;

	public BinaryTreeDataFlow() {
		trace.add(new Integer(1));
		root = null;
	}

	public void insert(BinaryTreeNode z) {
		trace.add(new Integer(2));
		if (root == null) {
			trace.add(new Integer(3));
			root = z;
		} else {
			trace.add(new Integer(4));
			root.insert(z);
		}
	}

	public boolean search(Comparable x) {
		trace.add(new Integer(5));
		if (root == null) {
			trace.add(new Integer(6));
			return false;
		} else {
			trace.add(new Integer(7));
		}

		BinaryTreeNode n = root.search(x);
		if (n != null) {
			trace.add(new Integer(8));
			return true;
		} else {
			trace.add(new Integer(9));
		}

		return false;
	}

	static java.util.List trace;

	public static void newTrace() {

		trace = new java.util.LinkedList();
	}

	public static java.util.List getTrace() {
		return trace;
	}

}
