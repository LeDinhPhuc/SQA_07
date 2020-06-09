public class BinaryTreeNode {
	private BinaryTreeNode left;
	private BinaryTreeNode right;
	private Comparable obj;

	public BinaryTreeNode(Comparable x) {
		obj = x;
		left = null;
		right = null;
	}

	public void insert(BinaryTreeNode z) {
		if (z.obj.compareTo(obj) == 0)
			return;
		if (z.obj.compareTo(obj) < 0)
			if (left == null)
				left = z;
			else
				left.insert(z);
		else if (right == null)
			right = z;
		else
			right.insert(z);
	}

	public BinaryTreeNode search(Comparable x) {
		if (x.compareTo(obj) == 0) {
			return this;
		}
		if (x.compareTo(obj) < 0)
			if (left == null)
				return null;
			else
				return left.search(x);
		else if (right == null)
			return null;
		else
			return right.search(x);
	}
}
