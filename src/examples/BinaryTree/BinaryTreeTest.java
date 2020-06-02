package examples.BinaryTree;

import junit.framework.*;

public class BinaryTreeTest extends TestCase {
	public void testCase1() {
		BinaryTree x19 = new BinaryTree();
		java.lang.Integer x34 = new java.lang.Integer(96);
		BinaryTreeNode x33 = new BinaryTreeNode(x34);
		java.lang.Integer x35 = new java.lang.Integer(65);
		x19.search(x35);
		java.lang.Integer x37 = new java.lang.Integer(21);
		BinaryTreeNode x36 = new BinaryTreeNode(x37);
		x19.insert(x36);
		java.lang.Integer x20 = new java.lang.Integer(91);
		assertFalse(x19.search(x20));
	}

	public void testCase2() {
		BinaryTree x1 = new BinaryTree();
		java.lang.Integer x2 = new java.lang.Integer(91);
		assertFalse(x1.search(x2));
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(BinaryTreeTest.class);
	}
}
