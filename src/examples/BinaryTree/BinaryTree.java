package examples.BinaryTree;

public class BinaryTree {
  private BinaryTreeNode root;

  public BinaryTree() {
    root = null;
  }
   
  public void insert(BinaryTreeNode z) {
    if (root == null)
      root = z;
    else
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
