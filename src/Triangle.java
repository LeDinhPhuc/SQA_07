/*
 * This code was generated by ojc.
 */
import java.util.*;
import it.itc.etoc.*;


public class Triangle
{

    public Triangle()
    {
        super();
        trace.add( new java.lang.Integer( 1 ) );
    }

    public java.lang.String checkTriangle( int side1, int side2, int side3 )
    {
        trace.add( new java.lang.Integer( 2 ) );
        java.lang.String type = "";
        if (side1 + side2 > side3 && side3 + side2 > side1 && side1 + side3 > side2) {
            trace.add( new java.lang.Integer( 3 ) );
            if (side1 != side2 && side1 != side3 && side2 != side3) {
                trace.add( new java.lang.Integer( 4 ) );
                type = "Is triangle scalene";
            } else {
                trace.add( new java.lang.Integer( 5 ) );
                if (side1 == side2 && side1 != side3 || side1 == side3 && side1 != side2 || side2 == side3 && side2 != side1) {
                    trace.add( new java.lang.Integer( 6 ) );
                    type = "Is triangle isosceles ";
                } else {
                    trace.add( new java.lang.Integer( 7 ) );
                    type = "Is triangle equilateral ";
                }
            }
        } else {
            trace.add( new java.lang.Integer( 8 ) );
            type = "Not a triangle";
        }
        return type;
    }

    
    static java.util.List trace = new java.util.LinkedList();

    
    public static void newTrace()
    {
        trace = new java.util.LinkedList();
    }

    
    public static java.util.List getTrace()
    {
        return trace;
    }

}
