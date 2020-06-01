/*
 * This code was generated by ojc.
 */
/**
 * @(#) DataFlowInstrumentor.o	v. 1.0 - October 15, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */
package it.itc.etoc;

import openjava.mop.*;
import openjava.ptree.*;
import java.io.*;

/**
 * Adds trace instructions to an input Java program.
 *
 * <p>
 * This class exploits the OpenJava compiler to instrument the branches of a
 * Java program.
 *
 * <p>
 * The CLASSPATH must point to the location of the OpenJava classes:
 * 
 * <pre>
 * setenv CLASSPATH <OPENJAVA-DIR>/classes:${CLASSPATH}
 * </pre>
 */
public class DataFlowInstrumentor extends it.itc.etoc.BranchInstrumentor {

	java.util.Set fields = new java.util.HashSet();

	java.util.Set def = new java.util.HashSet();

	java.util.Set use = new java.util.HashSet();

	public void printDataDependences() {
		targetFile.println();
		java.util.Iterator d = def.iterator();
		while (d.hasNext()) {
			it.itc.etoc.DataFlowPair dp = (it.itc.etoc.DataFlowPair) d.next();
			int defBranch = dp.branch;
			java.lang.String defVar = dp.var;
			java.util.Iterator u = use.iterator();
			while (u.hasNext()) {
				it.itc.etoc.DataFlowPair up = (it.itc.etoc.DataFlowPair) u.next();
				int useBranch = up.branch;
				java.lang.String useVar = up.var;
				if (defVar.equals(useVar)) {
					targetFile.println("(" + defBranch + ", " + useBranch + ", " + getClassName() + "." + defVar + ")");
				}
			}
		}
	}

	public void addDef(int branch, java.lang.String var) {
		def.add(new it.itc.etoc.DataFlowPair(branch, var));
	}

	public void addUse(int branch, java.lang.String var) {
		use.add(new it.itc.etoc.DataFlowPair(branch, var));
	}

	/**
	 * true if var is a class field
	 */
	public boolean isField(java.lang.String var) {
		return fields.contains(var);
	}

	/**
	 * Overrides default type for trace attribute (from Set to List).
	 */
	public void translateDefinition() throws openjava.mop.MOPException {
		traceInterfaceType = "java.util.List";
		traceConcreteType = "java.util.LinkedList";
		openjava.mop.OJField[] flds = getAllFields();
		for (int i = 0; i < flds.length; i++) {
			fields.add(flds[i].getName());
		}
		super.translateDefinition();
		printDataDependences();
	}

	/**
	 * Initiates the visit of the parse tree from the whole method body.
	 */
	public void insertBranchTraces(openjava.ptree.StatementList block) {
		try {
			block.accept(new it.itc.etoc.DataFlowVisitor(this));
			super.insertBranchTraces(block);
		} catch (openjava.ptree.ParseTreeException e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public DataFlowInstrumentor(openjava.mop.Environment oj_param0, openjava.mop.OJClass oj_param1,
			openjava.ptree.ClassDeclaration oj_param2) {
		super(oj_param0, oj_param1, oj_param2);
	}

	public DataFlowInstrumentor(java.lang.Class oj_param0, openjava.mop.MetaInfo oj_param1) {
		super(oj_param0, oj_param1);
	}

}