/**
 * @(#) TestCaseExecutor.java	v. 1.0 - March 12, 2003
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;

import java.util.*;
import java.util.regex.*;

import it.itc.etoc.BranchTarget;
import it.itc.etoc.Chromosome;
import it.itc.etoc.ChromosomeFormer;
import it.itc.etoc.TestCaseExecutor;
import it.itc.etoc.TestGenerator;

import java.io.*;
import java.lang.reflect.*;

/**
 * Executes a test case encoded in a chromosome.
 *
 * <p>
 * <img src=TestCaseExecutor.png width=500>
 *
 */
public class TestCaseExecutor {
	/**
	 * Description and values of a test case.
	 *
	 * @see ChromosomeFormer
	 */
	private String chromosome = "";

	/*
	 * Counts number of executions for logging purposes only.
	 */
	public static int testCaseExecutions = 0;

	/**
	 * Array of objects created for test case execution.
	 *
	 * Each object variable $xN in the chromosome is associated to the entry
	 * objects[N].
	 */
	private Object[] objects;

	/**
	 * Array of classes of the objects created for test case execution.
	 *
	 * Each object variable $xN in the chromosome is associated to the entry
	 * classes[N].
	 */
	private Class[] classes;

	/**
	 * Returns n-th object allocated during test case execution.
	 *
	 * @return objects[n]
	 */
	public Object objectAt(int n) {
		return objects[n];
	}

	/**
	 * Wraps primitive types and maps object vars into object types.
	 *
	 * @param type Either a primitive type or an object var.
	 *
	 * @return Wrapper type/class.
	 */
	public Class mapTypeToClass(String type) {
		System.out.println(type);
		try {
			if (type.indexOf("[") != -1)
				type = type.substring(0, type.indexOf("["));
			if (ChromosomeFormer.isPrimitiveType(type)) {
				if (type.equals("String"))
					return Class.forName("java.lang.String");
				else if (type.equals("boolean"))
					return Boolean.TYPE;
				else if (type.equals("double"))
					return Double.TYPE;
				else if (type.equals("float"))
					return Float.TYPE;
				else
					return Integer.TYPE;
			}
			int k = Integer.parseInt(type.substring(2));
			return classes[k];
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found. " + e);
			System.exit(1);
		}
		return null;
	}

	/**
	 * Wraps values with objects and returns previously allocated objects.
	 *
	 * If called with String $xN in input, it returns the object previously assigned
	 * to $xN, i.e., objects[N]. Otherwise, it returns the int value wrapped by an
	 * Integer object.
	 *
	 * @param val either $xN or an integer value
	 *
	 * @return Either objects[N] for $xN, or new Integer(val).
	 */
	public Object mapValueToObject(String val) {
		if (val.equals("null"))
			return null;
		Object obj = null;
		Pattern p = Pattern.compile("\\$x(\\d+)");
		Matcher m = p.matcher(val);
		if (m.find())
			obj = objects[Integer.parseInt(m.group(1))];
		else if (val.startsWith("\"") && val.endsWith("\""))
			obj = val.substring(1, val.length() - 1);
		else if (val.equals("true") || val.equals("false"))
			obj = new Boolean(val);
		else if (val.indexOf(".") != -1)
			obj = new Double(Double.parseDouble(val));
		else
			obj = new Integer(Integer.parseInt(val));
		return obj;
	}

	/**
	 * Executes a single action passed as a parameter.
	 *
	 * The action to execute can be either the construction of an object or the
	 * invocation of a method.
	 *
	 * @param action The action to execute.
	 * @param values Actual parameters.
	 */
	private void execute(String action, String[] values) {
		if (action.indexOf("=") != -1) {
			executeObjectConstruction(action, values);
		} else {
			executeMethodInvocation(action, values);
		}
	}

	/**
	 * Determines the Constructor compatible with any possible parameter supertype.
	 *
	 * @param cl         The class containing the method.
	 * @param constrName Name of Constructor.
	 * @param params     Actual parameter types.
	 * @return compatible Constructor (null if no one exists).
	 */
	private Constructor getConstructor(Class cl, Class[] params) {
		Constructor constr = null;
		Constructor[] classConstructors = cl.getConstructors();
		for (int i = 0; i < classConstructors.length; i++) {
			constr = classConstructors[i];
			Class[] formalParams = constr.getParameterTypes();
			if (formalParams.length != params.length)
				continue;
			boolean paramsAreCompatible = true;
			for (int j = 0; j < formalParams.length; j++)
				if (params[j] == null || !formalParams[j].isAssignableFrom(params[j]))
					paramsAreCompatible = false;
			if (paramsAreCompatible)
				return constr;
		}
		return null;
	}

	/**
	 * Builds an object according to the requested action.
	 *
	 * @param action The object construction action to execute.
	 * @param values Actual parameters for the construction.
	 */
	private void executeObjectConstruction(String action, String[] values) {
		String className = "";
		try {
			String lhs = action.substring(action.indexOf("$x") + 2, action.indexOf("="));
			int i = Integer.parseInt(lhs);
			if (action.indexOf("#") != -1) {
				className = action.substring(action.indexOf("=") + 1, action.indexOf("#"));
				objects[i] = null;
				classes[i] = Class.forName(className);
				return;
			}
			className = action.substring(action.indexOf("=") + 1, action.indexOf("("));
			String[] paramNames = action.substring(action.indexOf("(") + 1, action.indexOf(")")).split(",");
			if (paramNames.length == 1 && paramNames[0].equals(""))
				paramNames = new String[0];
			Class[] params = new Class[paramNames.length];
			for (int j = 0; j < paramNames.length; j++)
				params[j] = mapTypeToClass(paramNames[j]);
			Class cl = Class.forName(className);
			Constructor constr = getConstructor(cl, params);
			Object[] actualParams = new Object[params.length];
			for (int j = 0; j < actualParams.length; j++)
				actualParams[j] = mapValueToObject(values[j]);
			if (constr != null) {
				objects[i] = constr.newInstance(actualParams);
				classes[i] = objects[i].getClass();
			}
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found. " + e);
			System.exit(1);
		} catch (SecurityException e) {
			System.err.println("Class security violation: " + className + ".");
			System.exit(1);
		} catch (InstantiationException e) {
			System.err.println("Instantiation error: " + className + ".");
			System.exit(1);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal access error: " + className + ".");
			System.exit(1);
		} catch (InvocationTargetException e) {
			return;
			// continues normal execution in case of exception in called constructor.
			// System.err.println("Invocation target error.");
			// System.exit(1);
		}
	}

	/**
	 * Determines the Method compatible with any possible parameter supertype.
	 *
	 * @param cl         The class containing the method.
	 * @param methodName Name of method.
	 * @param params     Actual parameter types.
	 * @return compatible Method (null if no one exists).
	 */
	private Method getMethod(Class cl, String methodName, Class[] params) {
		Method method = null;
		Method[] classMethods = cl.getMethods();
		for (int i = 0; i < classMethods.length; i++) {
			method = classMethods[i];
			if (!method.getName().equals(methodName))
				continue;
			Class[] formalParams = method.getParameterTypes();
			if (formalParams.length != params.length)
				continue;
			boolean paramsAreCompatible = true;
			for (int j = 0; j < formalParams.length; j++) {
				if (params[j] == null || !formalParams[j].isAssignableFrom(params[j]))
					paramsAreCompatible = false;
			}
			if (paramsAreCompatible)
				return method;
		}
		return null;
	}

	/**
	 * Invokes a method according to the requested action.
	 *
	 * @param action The method invocation action to execute.
	 * @param values Actual parameters for the invocation.
	 */
	private void executeMethodInvocation(String action, String[] values) {
		try {
			String targetName = action.substring(action.indexOf("$x") + 2, action.indexOf("."));
			String methodName = action.substring(action.indexOf(".") + 1, action.indexOf("("));
			String[] paramNames = action.substring(action.indexOf("(") + 1, action.indexOf(")")).split(",");
			if (paramNames.length == 1 && paramNames[0].equals(""))
				paramNames = new String[0];
			Class[] params = new Class[paramNames.length];
			Object obj = objects[Integer.parseInt(targetName)];
			if (obj == null)
				return;
			Class cl = obj.getClass();
			for (int i = 0; i < paramNames.length; i++)
				params[i] = mapTypeToClass(paramNames[i]);
			Method method = getMethod(cl, methodName, params);
			Object[] actualParams = new Object[params.length];
			for (int j = 0; j < actualParams.length; j++)
				actualParams[j] = mapValueToObject(values[j]);
			if (method != null)
				method.invoke(obj, actualParams);
		} catch (SecurityException e) {
			System.err.println("Class security violation.");
			System.exit(1);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal access error.");
			System.exit(1);
		} catch (InvocationTargetException e) {
			return; // continues normal execution in case of exception in called method.
			// System.err.println("Invocation target error.");
			// System.exit(1);
		}
	}

	/**
	 * Remaps variable indexes to the range 0...N.
	 *
	 * Example: "$x21=A():$x22.m($x21)" becomes "$x0=A():$x1.m($x0)".
	 */
	private String renameChromsomeVariables(String chrom) {
		String inputDescription = chrom.substring(0, chrom.indexOf("@"));
		String[] actions = inputDescription.split(":");
		int n = 0;
		Map mapIndex = new HashMap();
		for (int i = 0; i < actions.length; i++)
			if (actions[i].indexOf("=") != -1) {
				String targetObj = actions[i].substring(2, actions[i].indexOf("="));
				int k = Integer.parseInt(targetObj);
				mapIndex.put(new Integer(k), new Integer(n++));
			}
		Iterator i = mapIndex.keySet().iterator();
		while (i.hasNext()) {
			Integer x = (Integer) i.next();
			int k = x.intValue();
			Integer y = (Integer) mapIndex.get(x);
			int j = y.intValue();
			if (k == j)
				continue;
			Pattern p = Pattern.compile("(.*)\\$x" + k + "([\\.=,\\)].*)");
			Matcher m = p.matcher(chrom);
			while (m.find()) {
				chrom = m.group(1) + "$y" + j + m.group(2);
				m = p.matcher(chrom);
			}
		}
		chrom = chrom.replaceAll("\\$y", "\\$x");
		return chrom;
	}

	/**
	 * Executes the test case encoded in chromosome chrom.
	 *
	 * The chromosome is split into input description and values. Each action in the
	 * input description is then executed.
	 */
	public void execute(String classUnderTest, String chrom) {
		Method setUpExec = null;
		Method tearDownExec = null;
		try {
			Class<?> cl = Class.forName(classUnderTest);
			setUpExec = cl.getDeclaredMethod("setUpExec", new Class[0]);
			if (setUpExec != null)
				setUpExec.invoke(null, new Object[0]);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		testCaseExecutions++;
		chromosome = renameChromsomeVariables(chrom);
		String inputDescription = chromosome.substring(0, chromosome.indexOf("@"));
		String inputValues = chromosome.substring(chromosome.indexOf("@") + 1);
		String[] actions = inputDescription.split(":");
		String[] values = inputValues.split(",");
		int n = -1;
		for (int i = 0; i < actions.length; i++)
			if (actions[i].indexOf("=") != -1) {
				String targetObj = actions[i].substring(0, actions[i].indexOf("="));
				int k = Integer.parseInt(targetObj.substring(2));
				if (k > n)
					n = k;
			}
		objects = new Object[n + 1];
		classes = new Class[n + 1];

		resetExecutionTrace(classUnderTest);
		int k = 0;
		for (int i = 0; i < actions.length; i++) {
			String action = actions[i];
			String[] params = new String[0];
			if (action.indexOf("(") != -1)
				params = action.substring(action.indexOf("(") + 1, action.indexOf(")")).split(",");
			if (params.length == 1 && params[0].equals(""))
				params = new String[0];
			String[] actionValues = new String[params.length];
			for (int j = 0; j < params.length; j++) {
				if (ChromosomeFormer.isPrimitiveType(params[j]))
					actionValues[j] = values[k++];
				else
					actionValues[j] = params[j];
			}
			execute(action, actionValues);
		}
		try {
			Class cl = Class.forName(classUnderTest);
			tearDownExec = cl.getDeclaredMethod("tearDownExec", new Class[0]);
			if (tearDownExec != null)
				tearDownExec.invoke(null, new Object[0]);
		} catch (Exception e) {

		}
	}

	/**
	 * Requests the execution trace to the class under test.
	 *
	 * @param classUnderTest The class being tested.
	 * @return trace: Set<Integer>
	 */
	public Collection getExecutionTrace(String classUnderTest) {
		try {
			Class cl = Class.forName(classUnderTest);
			Method getTrace = cl.getDeclaredMethod("getTrace", new Class[0]);
			Collection trace = (Collection) getTrace.invoke(null, new Object[0]);
			Collection coveredBranches;
			if (TestGenerator.dataFlowCoverage)
				coveredBranches = new LinkedList();
			else
				coveredBranches = new HashSet();
			Iterator j = trace.iterator();
			while (j.hasNext()) {
				BranchTarget branch = new BranchTarget(((Integer) j.next()).intValue());
				coveredBranches.add(branch);
			}
			return coveredBranches;
		} catch (NoSuchMethodException e) {
			System.err.println("Method not found. " + e);
			System.exit(1);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal access error.");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found. " + e);
			System.exit(1);
		} catch (InvocationTargetException e) {
			System.err.println("Invocation target error: " + classUnderTest + ".");
			System.exit(1);
		}
		return null;
	}

	/**
	 * Resets the execution trace of the class under test.
	 *
	 * @param classUnderTest The class being tested.
	 */
	public void resetExecutionTrace(String classUnderTest) {
		try {
			Class cl = Class.forName(classUnderTest);
			Method newTrace = cl.getDeclaredMethod("newTrace", new Class[0]);
			newTrace.invoke(null, new Object[0]);
		} catch (NoSuchMethodException e) {
			System.err.println("Method not found. " + e);
			System.exit(1);
		} catch (IllegalAccessException e) {
			System.err.println("Illegal access error.");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			System.err.println("Class not found. " + e);
			System.exit(1);
		} catch (InvocationTargetException e) {
			System.err.println("Invocation target error: " + classUnderTest + ".");
			System.exit(1);
		}
	}

	/**
	 * Prints the execution trace.
	 *
	 * Note that the execution trace is the <i> set </i> (not the <i> sequence </i>)
	 * of statements that have been executed. Repeated execution is counted once.
	 *
	 * @param classUnderTest The class being tested.
	 */
	public void printBranchExecutionTrace(String classUnderTest) {
		if (TestGenerator.dataFlowCoverage)
			return;
		System.out.print("Trace:");
		Set trace = (Set) getExecutionTrace(classUnderTest);
		Iterator i = trace.iterator();
		while (i.hasNext()) {
			BranchTarget n = (BranchTarget) i.next();
			System.out.print(" " + n);
		}
		System.out.println();
	}

	/**
	 * Generates a chromosome and executes the related test case.
	 *
	 * The last method in signature file is the method under test.
	 *
	 * @param args[0] Text file with signatures.
	 */
	public static void main(String[] args) {
		try {
			TestCaseExecutor exec = new TestCaseExecutor();
			if (args.length == 0) {
				System.err.println("Usage: java TestCaseExecutor signature-file");
				System.exit(1);
			}
			ChromosomeFormer chromFormer = new ChromosomeFormer();
			chromFormer.readSignatures(args[0]);
			chromFormer.buildNewChromosome();
			String classUnderTest = chromFormer.getClassUnderTest();
			int c = 0;
			while (c != -1) {
				System.out.println(chromFormer.getChromosome());
				exec.execute(classUnderTest, chromFormer.getChromosome().toString());
				System.out.println(exec.chromosome);
				exec.printBranchExecutionTrace(classUnderTest);
				c = System.in.read();
				Chromosome curChrom = chromFormer.getChromosome();
				chromFormer.buildNewChromosome();
				chromFormer.insertRandomMethodCall();
				Chromosome newChrom = chromFormer.getChromosome();
				chromFormer.setCurrentChromosome(curChrom);
				chromFormer.mutateChromosome(newChrom);
			}
		} catch (IOException e) {
			System.err.println("IO error: " + args[0]);
			System.exit(1);
		}
	}

}
