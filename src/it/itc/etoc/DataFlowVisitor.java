/**
 * @(#) DataFlowVisitor.java	v. 1.0 - October 14, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;

import it.itc.etoc.DataFlowInstrumentor;
import openjava.ptree.*;
import openjava.ptree.util.*;

/**
 * Visitor design pattern, used to extract data dependences.
 *
 */
public class DataFlowVisitor extends ParseTreeVisitor {
	static int branchCounter = 0;
	static java.util.Stack lastBranchCounter;
	DataFlowInstrumentor instrumentor;

	boolean isDef = false;
	boolean isUse = true;

	/**
	 * The backward link to the DataFlowInstrumentor class is used to check if a
	 * Variable is a field.
	 */
	public DataFlowVisitor(DataFlowInstrumentor instr) {
		instrumentor = instr;
		lastBranchCounter = new java.util.Stack();
	}

	public void visit(AllocationExpression p) throws ParseTreeException {
		ExpressionList args = p.getArguments();
		args.accept(this);
	}

	public void visit(ArrayAccess p) throws ParseTreeException {
		Expression index = p.getIndexExpr();
		index.accept(this);
		Expression ref = p.getReferenceExpr();
		ref.accept(this);
	}

	public void visit(ArrayAllocationExpression p) throws ParseTreeException {
		ExpressionList dim = p.getDimExprList();
		dim.accept(this);
		ArrayInitializer init = p.getInitializer();
		if (init != null)
			init.accept(this);
	}

	public void visit(ArrayInitializer p) throws ParseTreeException {
		java.util.Enumeration it = p.elements();
		while (it.hasMoreElements()) {
			ParseTree elem = (ParseTree) it.nextElement();
			elem.accept(this);
		}
	}

	/**
	 * Determines defined fields.
	 */
	public void visit(AssignmentExpression p) throws ParseTreeException {
		Expression left = p.getLeft();
		isDef = true;
		isUse = false;
		left.accept(this);
		isDef = false;
		isUse = true;
		Expression right = p.getRight();
		right.accept(this);
	}

	public void visit(BinaryExpression p) throws ParseTreeException {
		Expression left = p.getLeft();
		left.accept(this);
		Expression right = p.getRight();
		right.accept(this);
	}

	/**
	 * Resorts to visit(StatementList) in order to visit a Block.
	 */
	public void visit(Block p) throws ParseTreeException {
		StatementList stmts = p.getStatements();
		stmts.accept(this);
	}

	public void visit(BreakStatement p) {
	}

	/**
	 * Resorts to visit(StatementList) in order to visit a Case statement.
	 */
	public void visit(CaseGroup p) throws ParseTreeException {
		StatementList stmts = p.getStatements();
		stmts.accept(this);
	}

	/**
	 * Iterates over cases in switch statements.
	 */
	public void visit(CaseGroupList p) throws ParseTreeException {
		java.util.Enumeration it = p.elements();
		while (it.hasMoreElements()) {
			ParseTree elem = (ParseTree) it.nextElement();
			elem.accept(this);
		}
	}

	public void visit(CaseLabel p) {
	}

	public void visit(CaseLabelList p) {
	}

	public void visit(CastExpression p) throws ParseTreeException {
		Expression expr = p.getExpression();
		expr.accept(this);
	}

	/**
	 * Resorts to visit(StatementList) in order to visit each catch block.
	 */
	public void visit(CatchBlock p) throws ParseTreeException {
		StatementList stmts = p.getBody();
		stmts.accept(this);
	}

	/**
	 * Iterates over catch blocks.
	 */
	public void visit(CatchList p) throws ParseTreeException {
		java.util.Enumeration it = p.elements();
		while (it.hasMoreElements()) {
			ParseTree elem = (ParseTree) it.nextElement();
			elem.accept(this);
		}
	}

	public void visit(ClassDeclaration p) {
	}

	public void visit(ClassDeclarationList p) {
	}

	public void visit(ClassLiteral p) {
	}

	public void visit(CompilationUnit p) {
	}

	public void visit(ConditionalExpression p) throws ParseTreeException {
		Expression cond = p.getCondition();
		cond.accept(this);
		Expression trueCase = p.getTrueCase();
		trueCase.accept(this);
		Expression falseCase = p.getFalseCase();
		falseCase.accept(this);
	}

	public void visit(ConstructorDeclaration p) {
	}

	public void visit(openjava.ptree.ConstructorInvocation p) throws ParseTreeException {
		ExpressionList args = p.getArguments();
		args.accept(this);
	}

	public void visit(ContinueStatement p) {
	}

	/**
	 * Resorts to visit(StatementList) in order to visit a do loop.
	 */
	public void visit(DoWhileStatement p) throws ParseTreeException {
		Expression cond = p.getExpression();
		cond.accept(this);
		StatementList stmts = p.getStatements();
		stmts.accept(this);
	}

	public void visit(EmptyStatement p) {
	}

	public void visit(ExpressionList p) throws ParseTreeException {
		java.util.Enumeration it = p.elements();
		while (it.hasMoreElements()) {
			ParseTree elem = (ParseTree) it.nextElement();
			elem.accept(this);
		}
	}

	/**
	 * Resorts to visit(Expression) in order to visit an expression statement.
	 */
	public void visit(ExpressionStatement p) throws ParseTreeException {
		Expression expr = p.getExpression();
		expr.accept(this);
	}

	public void visit(FieldAccess p) throws ParseTreeException {
		Expression ref = p.getReferenceExpr();
		if (ref != null)
			ref.accept(this);
	}

	public void visit(FieldDeclaration p) {
	}

	/**
	 * Resorts to visit(StatementList) in order to visit a for loop.
	 */
	public void visit(ForStatement p) throws ParseTreeException {
		Expression cond = p.getCondition();
		if (cond != null)
			cond.accept(this);
		ExpressionList incr = p.getIncrement();
		if (incr != null)
			incr.accept(this);
		ExpressionList init = p.getInit();
		if (init != null)
			init.accept(this);
		StatementList stmts = p.getStatements();
		if (stmts != null)
			stmts.accept(this);
	}

	/**
	 * Resorts to visit(StatementList) in order to visit an if statement.
	 */
	public void visit(IfStatement p) throws ParseTreeException {
		Expression cond = p.getExpression();
		cond.accept(this);
		StatementList stmts = p.getStatements();
		stmts.accept(this);
		StatementList elsestmts = p.getElseStatements();
		elsestmts.accept(this);
	}

	public void visit(InstanceofExpression p) throws ParseTreeException {
		Expression expr = p.getExpression();
		expr.accept(this);
	}

	public void visit(LabeledStatement p) throws ParseTreeException {
		Statement stmt = p.getStatement();
		stmt.accept(this);
	}

	public void visit(Literal p) {
	}

	public void visit(MemberDeclarationList p) {
	}

	public void visit(MemberInitializer p) {
	}

	public void visit(MethodCall p) throws ParseTreeException {
		ExpressionList args = p.getArguments();
		args.accept(this);
		Expression ref = p.getReferenceExpr();
		if (ref != null)
			ref.accept(this);
	}

	public void visit(MethodDeclaration p) {
	}

	public void visit(ModifierList p) {
	}

	public void visit(Parameter p) {
	}

	public void visit(ParameterList p) {
	}

	public void visit(ReturnStatement p) throws ParseTreeException {
		Expression expr = p.getExpression();
		if (expr != null)
			expr.accept(this);
	}

	public void visit(SelfAccess p) {
	}

	/**
	 * Iterates over statements.
	 */
	public void visit(StatementList p) throws ParseTreeException {
		branchCounter++;
		lastBranchCounter.push(new Integer(branchCounter));
		java.util.Enumeration it = p.elements();
		while (it.hasMoreElements()) {
			ParseTree elem = (ParseTree) it.nextElement();
			elem.accept(this);
		}
		lastBranchCounter.pop();
	}

	/**
	 * Resorts to visit(CaseGroupList) to visit switch.
	 */
	public void visit(SwitchStatement p) throws ParseTreeException {
		Expression cond = p.getExpression();
		cond.accept(this);
		CaseGroupList cases = p.getCaseGroupList();
		cases.accept(this);
	}

	public void visit(SynchronizedStatement p) throws ParseTreeException {
		Expression expr = p.getExpression();
		expr.accept(this);
		StatementList stmts = p.getStatements();
		stmts.accept(this);
	}

	public void visit(ThrowStatement p) throws ParseTreeException {
		Expression expr = p.getExpression();
		expr.accept(this);
	}

	/**
	 * Resorts to visit(StatementList) to visit each try block.
	 */
	public void visit(TryStatement p) throws ParseTreeException {
		StatementList stmts = p.getBody();
		stmts.accept(this);
		CatchList catches = p.getCatchList();
		if (catches != null && !catches.isEmpty())
			catches.accept(this);
		stmts = p.getFinallyBody();
		if (stmts != null && !stmts.isEmpty())
			stmts.accept(this);
	}

	public void visit(TypeName p) {
	}

	public void visit(UnaryExpression p) throws ParseTreeException {
		Expression expr = p.getExpression();
		if (p.getOperator() == p.POST_DECREMENT || p.getOperator() == p.POST_INCREMENT
				|| p.getOperator() == p.PRE_DECREMENT || p.getOperator() == p.PRE_DECREMENT)
			isDef = true;
		isUse = true;
		expr.accept(this);
		isDef = false;
		isUse = true;
	}

	public void visit(Variable p) throws ParseTreeException {
//    if (instrumentor.isField(p.toString())) {
		if (isDef)
			instrumentor.addDef(((Integer) lastBranchCounter.peek()).intValue(), p.toString());
		if (isUse)
			instrumentor.addUse(((Integer) lastBranchCounter.peek()).intValue(), p.toString());
//    }
	}

	public void visit(VariableDeclaration p) throws ParseTreeException {
		VariableInitializer var = p.getInitializer();
		var.accept(this);
		instrumentor.print();
		instrumentor.addDef(((Integer) lastBranchCounter.peek()).intValue(), p.getVariable());
	}

	public void visit(VariableDeclarator p) throws ParseTreeException {
		VariableInitializer init = p.getInitializer();
		System.out.println(init + " init");
		p.accept(this);
	}

	/**
	 * Resorts to visit(StatementList) to visit a while loop.
	 */
	public void visit(WhileStatement p) throws ParseTreeException {
		Expression cond = p.getExpression();
		cond.accept(this);
		StatementList stmts = p.getStatements();
		stmts.accept(this);
	}

}
