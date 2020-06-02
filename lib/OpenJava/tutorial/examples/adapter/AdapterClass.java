/*
 * This code was generated by ojc.
 */
/*
 * AdapterClass.oj
 *
 * An OpenJava example to support programming with the Adapter pattern.
 *
 * @author   Michiaki Tatsubori
 * @version  %VERSION% %DATE%
 * @see      java.lang.Object
 *
 * COPYRIGHT 1999 by Michiaki Tatsubori, ALL RIGHTS RESERVED.
 */
package examples.adapter;


import java.lang.Object;
import openjava.mop.*;
import openjava.ptree.*;
import openjava.syntax.*;


/**
 * The metaclass <code>AdapterClass</code> supports classes
 * implementing an adapter role of the Adapter pattern.
 * The target's methods with same signatures as the adaptee's are
 * automatically implemented into the adapter class.
 * <p>
 * For example, the class <code>VectorStack</code>:
 * <pre>
 * public class VectorStack instantiates AdapterClass
 *    adapts Vector in v to Stack
 * {
 *    Vector v;
 *    public VectorStack( Vector v ) {
 *        this.v = v;
 *    }
 *    public void push( Object o ) {
 *        v.addElement( o );
 *    }
 *    public Object pop() {
 *        return v.removeElementAt( v.size() - 1 );
 *    }
 * }
 * </pre>
 * would be automatically implemented with the forwarding methods
 * size(), isEmpty(), hashCode(), etc, which are found in both
 * the class Vector(adaptee) and the class Stack(target).
 * <p>
 *
 * @author   Michiaki Tatsubori
 * @version  1.0
 * @since    $Id: AdapterClass.java,v 1.2 2003/02/19 02:55:02 tatsubori Exp $
 * @see java.lang.Object
 */
public class AdapterClass extends openjava.mop.OJClass
{

    public static final java.lang.String KEY_ADAPTS = "adapts";

    /* overrides for translation */
    public void translateDefinition()
        throws openjava.mop.MOPException
    {
        openjava.mop.OJClass target = getTarget();
        openjava.mop.OJClass adaptee = getAdaptee();
        if (target == null || adaptee == null) {
            return;
        }
        openjava.mop.OJMethod[] adapteds = adaptee.getMethods( this );
        for (int i = 0; i < adapteds.length; ++i) {
            openjava.mop.OJMethod m;
            try {
                m = getTarget().getMethod( adapteds[i].getName(), adapteds[i].getParameterTypes(), this );
            } catch ( openjava.mop.NoSuchMemberException e ) {
                continue;
            }
            addMethod( makeForwardingMethod( m.getName(), m ) );
        }
        addInterface( getTarget() );
    }

    /**
     * Generates a forwarding method with specified name.
     *
     * @param  name  generating method's name.
     * @param forwarded  method to which the generated method forwards.
     * @return  a generated forwarding method.
     */
    private openjava.mop.OJMethod makeForwardingMethod( java.lang.String name, openjava.mop.OJMethod forwarded )
        throws openjava.mop.MOPException
    {
        openjava.mop.OJMethod result = new openjava.mop.OJMethod( this, forwarded.getModifiers().remove( OJModifier.ABSTRACT ), forwarded.getReturnType(), name, forwarded.getParameterTypes(), forwarded.getExceptionTypes(), null );
        openjava.ptree.ExpressionList params = result.getParameterVariables();
        openjava.ptree.Expression expr = new openjava.ptree.MethodCall( getContainer(), name, params );
        openjava.ptree.StatementList body = new openjava.ptree.StatementList();
        if (forwarded.getReturnType() == OJSystem.VOID) {
            body.add( new openjava.ptree.ExpressionStatement( expr ) );
            body.add( new openjava.ptree.ReturnStatement() );
        } else {
            body.add( new openjava.ptree.ReturnStatement( expr ) );
        }
        result.setBody( body );
        return result;
    }

    /* extended information */
    private openjava.mop.OJClass getAdaptee()
        throws openjava.mop.MOPException
    {
        openjava.ptree.ObjectList suffix = (openjava.ptree.ObjectList) getSuffix( KEY_ADAPTS );
        return OJClass.forName( suffix.get( 0 ).toString() );
    }

    private openjava.ptree.Variable getContainer()
        throws openjava.mop.MOPException
    {
        openjava.ptree.ObjectList suffix = (openjava.ptree.ObjectList) getSuffix( KEY_ADAPTS );
        return new openjava.ptree.Variable( suffix.get( 1 ).toString() );
    }

    private openjava.mop.OJClass getTarget()
        throws openjava.mop.MOPException
    {
        openjava.ptree.ObjectList suffix = (openjava.ptree.ObjectList) getSuffix( KEY_ADAPTS );
        return OJClass.forName( suffix.get( 2 ).toString() );
    }

    /* override to extend syntax */
    public static boolean isRegisteredKeyword( java.lang.String keyword )
    {
        return keyword.equals( KEY_ADAPTS );
    }

    /* override to extend syntax */
    public static openjava.syntax.SyntaxRule getDeclSuffixRule( java.lang.String keyword )
    {
        if (keyword.equals( KEY_ADAPTS )) {
            return new openjava.syntax.CompositeRule( new openjava.syntax.TypeNameRule(), new openjava.syntax.PrepPhraseRule( "in", new openjava.syntax.NameRule() ), new openjava.syntax.PrepPhraseRule( "to", new openjava.syntax.TypeNameRule() ) );
        }
        return null;
    }

    public AdapterClass( openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1, openjava.ptree.ClassDeclaration oj_param2 )
    {
        super( oj_param0, oj_param1, oj_param2 );
    }

    public AdapterClass( java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1 )
    {
        super( oj_param0, oj_param1 );
    }

}