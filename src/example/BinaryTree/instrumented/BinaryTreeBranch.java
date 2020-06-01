package example.BinaryTree.instrumented;

import java.util.*;

import it.itc.etoc.*;


public class BinaryTreeBranch {
    private BinaryTreeNode root;
    
    public BinaryTreeBranch() {
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


    static java.util.Set trace;


    public static void newTrace() 
    {
        trace = new java.util.HashSet();
    }


    public static java.util.Set getTrace() 
    {
        return trace;
    }

}
