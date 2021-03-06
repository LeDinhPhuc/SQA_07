package examples;
public class BinaryTree {
	private BinaryTreeNode root;
	String test = null;

	public BinaryTree() {
		root = null;
		test = null;
	}

	public void insert(BinaryTreeNode z) {
		if (root == null) {
			root = z;
			if (test == null) {
				test = "Hello world";
			}
		} else
			root.insert(z);
	}

	public boolean search(Comparable x) {
		if (root == null)
			return false;
		BinaryTreeNode n = root.search(x);
		if (n != null)
			return true;
		return false;
	}
}
