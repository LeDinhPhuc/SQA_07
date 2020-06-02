package examples.BinaryTree.instrumented;

import it.itc.etoc.*;

public class BinaryTree{

    private BinaryTreeNode root;

    public BinaryTree()
    {
        trace.add( new java.lang.Integer( 1 ) );
        root = null;
    }

    public void insert( BinaryTreeNode z )
    {
        trace.add( new java.lang.Integer( 2 ) );
        if (root == null) {
            trace.add( new java.lang.Integer( 3 ) );
            root = z;
        } else {
            trace.add( new java.lang.Integer( 4 ) );
            root.insert( z );
        }
    }

    public boolean search( java.lang.Comparable x )
    {
        trace.add( new java.lang.Integer( 5 ) );
        if (root == null) {
            trace.add( new java.lang.Integer( 6 ) );
            return false;
        } else {
            trace.add( new java.lang.Integer( 7 ) );
        }
        BinaryTreeNode n = root.search( x );
        if (n != null) {
            trace.add( new java.lang.Integer( 8 ) );
            return true;
        } else {
            trace.add( new java.lang.Integer( 9 ) );
        }
        return false;
    }

    
    static java.util.List trace;

    
    public static void newTrace()
    {
        trace = new java.util.LinkedList();
    }

    
    public static java.util.List getTrace()
    {
        return trace;
    }

}
