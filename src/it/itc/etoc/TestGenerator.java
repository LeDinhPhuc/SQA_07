/**
 * @(#) TestGenerator.java	v. 1.0 - July 20, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;

import openjava.ojc.*;
import java.util.regex.*;

import javax.xml.transform.Templates;

import it.itc.etoc.BranchTarget;
import it.itc.etoc.Chromosome;
import it.itc.etoc.DataFlowTarget;
import it.itc.etoc.DataFlowTestGenerator;
import it.itc.etoc.MethodTarget;
import it.itc.etoc.Population;
import it.itc.etoc.Target;
import it.itc.etoc.TestCaseExecutor;

import java.util.*;
import java.io.*;

class TestGenerator {
	/**
	 * Data flow coverage instead of branch coverage.
	 *
	 */
	public static boolean dataFlowCoverage = true;

	/**
	 * Junit class to be generated.
	 *
	 */
	static String junitFile = null;

	/**
	 * Branches to be covered, given as method + statements.
	 *
	 * This attribute contains a list of Target objects:
	 * 
	 * <pre>
	 * targets: List&lt;Target&gt;
	 * </pre>
	 */
	static List targets = new LinkedList();

	/**
	 * Output of test case generation.
	 *
	 * This list of individuals is filled in by the test case minimization
	 * procedure.
	 * 
	 * <pre>
	 * testCases: List&lt;Chromosomet&gt;
	 * </pre>
	 */
	List testCases = null;

	/**
	 * Control dependences associated to each target.
	 *
	 * target: BranchTarget -> Set<nodes: BranchTarget>
	 */
	Map paths = new HashMap();

	/**
	 * Associates each branch to the individual that allows covering it.
	 * 
	 * branch: Target -> individual: Chromosome
	 */
	Map targetCoveredByIndividual = new HashMap();

	/**
	 * Maximum iterations before changing target.
	 */
	int maxAttemptsPerTarget = 10;

	/**
	 * Execution timeout.
	 */
	int maxTime = 1; // seconds

	/**
	 * Text file with list of targets to be covered.
	 */
	static String targetFile = null;

	/**
	 * Text file with control dependences to each target.
	 */
	static String pathsFile = null;

	/**
	 * Text file with parameters of genetic algorithm.
	 */
	static String paramsFile = null;

	/**
	 * Info displayed at run-time.
	 *
	 * Changed while algorithm is running.
	 */
	static String displayedInfo = "";

	/**
	 * Reads text files with execution configuration.
	 */
	public TestGenerator() {
		readParameters();
		printParameters();
		readTarget();
		readPaths();
	}

	/**
	 * Prints current configuration.
	 */
	public void printParameters() {
		System.out.println("maxAttemptsPerTarget: " + maxAttemptsPerTarget);
		System.out.println("maxTime: " + maxTime + " s");
		System.out.println("populationSize: " + Population.populationSize);
	}

	/**
	 * Reads algorithm parameters.
	 *
	 * @param paramFile is formatted as follows:
	 *
	 *                  <pre>
	 * maxAttemptsPerTarget = 1000
	 * maxTime = 60
	 * populationSize = 50
	 *                  </pre>
	 */
	public void readParameters() {
		if (paramsFile == null)
			return;
		try {
			String s, r;
			int x;
			BufferedReader in = new BufferedReader(new FileReader(paramsFile));
			while ((s = in.readLine()) != null) {
				x = -1;
				if (s.trim().length() > 0) {
					StringTokenizer tok = new StringTokenizer(s);
					tok.nextToken();
					tok.nextToken();
					r = tok.nextToken();
					try {
						x = Integer.parseInt(r);
					} catch (NumberFormatException e) {
					}
					if (s.indexOf("maxAttemptsPerTarget") != -1 && x != -1)
						maxAttemptsPerTarget = x;
					if (s.indexOf("maxTime") != -1 && x != -1)
						maxTime = x;

					if (s.indexOf("populationSize") != -1 && x != -1)
						Population.populationSize = x;
				}
			}
		} catch (NumberFormatException e) {
			System.err.println("Wrong format file: " + paramsFile);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("IO error: " + paramsFile);
			System.exit(1);
		}
	}

	/**
	 * Reads selected targets from text file (target.txt).
	 * 
	 * Format of target.txt:
	 *
	 * <pre>
	 * BinaryTree.search(Comparable): 5, 6, 7, 8, 9
	 * </pre>
	 */
	public void readTarget() {
		try {
			String s;
			Pattern p = Pattern.compile("([^\\s]+)\\s*:\\s*(.*)");
			BufferedReader in = new BufferedReader(new FileReader(targetFile));
			while ((s = in.readLine()) != null) {
				Matcher m = p.matcher(s);

				if (!m.find())
					continue;
				String method = m.group(1);
				MethodTarget tgt = new MethodTarget(method);
				String[] branches = m.group(2).split(",");
				for (int i = 0; i < branches.length; i++) {
					int n = Integer.parseInt(branches[i].trim());
					tgt.addBranch(n);
				}
				targets.add(tgt);
			}
		} catch (NumberFormatException e) {
			System.err.println("Wrong format file: " + targetFile);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("IO error: " + targetFile);
			System.exit(1);
		}
	}

	/**
	 * Reads control dependences (paths.txt).
	 * 
	 * Format of paths.txt:
	 *
	 * <pre>
	 * 10: 4 5 6
	 * 11: 4 5 6 10
	 * 12: 4 5 6 10
	 * </pre>
	 */
	public void readPaths() {
		try {
			String s;
			BufferedReader in = new BufferedReader(new FileReader(pathsFile));
			while ((s = in.readLine()) != null) {
				String r = s.substring(0, s.indexOf(":"));
				int tgt = Integer.parseInt(r);
				// danh sách node cha của nó
				r = s.substring(s.indexOf(":") + 1);
				StringTokenizer tok = new StringTokenizer(r);
				Set pathPoints = new HashSet();
				while (tok.hasMoreTokens()) {
					int n = Integer.parseInt(tok.nextToken());
					pathPoints.add(new BranchTarget(n));
				}
				// hash map từ một nhánh target đến các nhánh cha của nó
				paths.put(new BranchTarget(tgt), pathPoints);
			}
		} catch (NumberFormatException e) {
			System.err.println("Wrong format file: " + pathsFile);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("IO error: " + pathsFile);
			System.exit(1);
		}
	}

	/**
	 * Collects target branches from all methods.
	 */
	public static List getAllTargets() {
		List targetsToCover = new LinkedList();
		Iterator i = targets.iterator();
		while (i.hasNext()) {
			Target tgt = (Target) i.next();
			// Xóa
			targetsToCover.addAll(tgt.getSubTargets());
		}
		return targetsToCover;
	}

	/**
	 * Updates the mapping between targets and covering test cases.
	 */
	public void updateCoveredTargets(Population pop) {
		List allTargets = getAllTargets();
		Iterator i = allTargets.iterator();
		while (i.hasNext()) {
			Target tgt = (Target) i.next();
			if (pop.covers(tgt) && !targetCoveredByIndividual.containsKey(tgt)) {
				Chromosome id = pop.getCoveringIndividual(tgt);
				targetCoveredByIndividual.put(tgt, id);
			}
		}
	}

	/**
	 * Xác định target path chưa được bao phủ Determines targets yet to be covered.
	 *
	 * @return Lst<targetsToCover: BranchTarget>
	 */
	public List computeNotYetCoveredTargets() {
		List newTargetToCover = new LinkedList();
		List allTargets = getAllTargets();
		Iterator i = allTargets.iterator();
		while (i.hasNext()) {
			Target tgt = (Target) i.next();
			if (!targetCoveredByIndividual.containsKey(tgt))
				newTargetToCover.add(tgt);

		}
		return newTargetToCover;
	}

	/**
	 * Displays execution info at run time.
	 */
	public void displayInfo(long t, int cov) {
		// for (int i = 0; i < displayedInfo.length(); i++)
		// System.out.print("\b");
		System.out.flush();
		displayedInfo = "Time: " + t + " s, targets to cover: " + cov + "      ";
		System.out.println(displayedInfo);
		System.out.flush();
	}

	/**
	 * Fitness for branch coverage.
	 */
	// Xóa
	// public void computeBranchFitness(Population pop, Target tgt) {
	// Set tgtPathPoints = (Set) paths.get(tgt);
	// pop.computeBranchFitness(tgtPathPoints);
	// }

	/**
	 * compute Fitness for data flow coverage.
	 */
	public void computeDataFlowFitness(Population pop, DataFlowTarget tgt) {
		Set tgtPathPoints1 = (Set) paths.get(tgt.getSourceBranch());
		Set tgtPathPoints2 = (Set) paths.get(tgt.getDestinationBranch());

		pop.computeDataFlowFitness((DataFlowTarget) tgt, tgtPathPoints1, tgtPathPoints2);
	}

	/**
	 * Sinh testcase Test case generation and execution.
	 */
	public void generateTestCases(String signFile) {
		Population.setChromosomeFormer(signFile);
		// Tập target path còn phải sinh test case
		List targetsToCover = getAllTargets();

		long startTime = System.currentTimeMillis() / 1000;
		long time = 0;
		int attempts = 0;

		displayInfo(time, targetsToCover.size());

		while (targetsToCover.size() > 0 && time < maxTime) {
			// iterator để duyệt phần tử trong list cho dạng con trỏ
			Iterator i = targets.iterator();
			while (i.hasNext()) {
				Target target = (Target) i.next();
				Population curPopulation = Population.generateRandomPopulation();

				Iterator j = target.getSubTargets().iterator();
				while (j.hasNext()) {
					// target trong file sign
					Target tgt = (Target) j.next();
					// nếu tập quần thể hiện tại đã bao phủ tgt rồi thì bỏ qua code sau
					if (curPopulation.covers(tgt))
						continue;
					// Số lần chạy tối đa
					attempts = 0;
					while (attempts < maxAttemptsPerTarget) {
						// thực thi test case
						curPopulation.executeTestCases();
						// cập nhật lại tập target path
						updateCoveredTargets(curPopulation);
						// nếu tiêu chí bao phủ target path thỏa mãn thì break
						if (curPopulation.covers(tgt))
							break;
						// nếu chưa thì sẽ đánh giá hàm fitness
						// if (dataFlowCoverage)
						computeDataFlowFitness(curPopulation, (DataFlowTarget) tgt);
						// tái tạo lại tập quần thể
						curPopulation = curPopulation.generateNewPopulation();
						attempts++;
					}
				}
			}
			// Cập nhật những target ko được bao phủ
			targetsToCover = computeNotYetCoveredTargets();
			time = System.currentTimeMillis() / 1000 - startTime;
			displayInfo(time, targetsToCover.size());
			System.out.println("targetsToCover " + targetsToCover.toString());
		}
		System.out.println("\n");
	}

	/**
	 * Generates Junit test class.
	 */
	public void printJunitFile() {
		try {
			if (testCases == null)
				minimizeTestCases();
			if (junitFile == null)
				return;
			String junitClass = junitFile.substring(junitFile.lastIndexOf("/") + 1, junitFile.indexOf("."));
			PrintStream out = new PrintStream(new FileOutputStream(junitFile));
			out.println("import junit.framework.*;");
			out.println();
			out.println("public class " + junitClass + " extends TestCase {");
			Iterator i = testCases.iterator();
			int n = 0;
			while (i.hasNext()) {
				Chromosome id = (Chromosome) i.next();
				n++;
				out.println("  public void testCase" + n + "() {");
				out.print(id.toCode());
				out.println("  }");
				out.println();
			}
			out.println("  public static void main (String[] args) {");
			out.println("    junit.textui.TestRunner.run(" + junitClass + ".class);");
			out.println("  }");
			out.println("}");
			out.close();
		} catch (FileNotFoundException e) {
			System.err.println("cannot create file: " + junitFile);
			System.exit(1);
		}
	}

	/**
	 * Minimize number of test cases.
	 *
	 * Greedy algorithm: selects test case with highest coverage increase.
	 */
	public void minimizeTestCases() {
		testCases = new LinkedList();
		Set covered = new HashSet();
		Set individuals = new HashSet();
		individuals.addAll(targetCoveredByIndividual.values());
		Set coveredTargets = targetCoveredByIndividual.keySet();
		while (coveredTargets.size() != covered.size()) {
			int max = 0;
			Chromosome selectedId = null;
			Iterator i = individuals.iterator();
			while (i.hasNext()) {
				Chromosome id = (Chromosome) i.next();
				Set idTrace = id.getCoveredTargets();
				int coveredNum = 0;
				Iterator j = idTrace.iterator();
				while (j.hasNext()) {
					Target target = (Target) j.next();
					if (!covered.contains(target) && coveredTargets.contains(target))
						coveredNum++;
				}
				if (coveredNum > max) {
					max = coveredNum;
					selectedId = id;
				}
			}
			testCases.add(selectedId);
			Set idTrace = selectedId.getCoveredTargets();
			Iterator j = idTrace.iterator();
			while (j.hasNext()) {
				Target target = (Target) j.next();
				if (coveredTargets.contains(target))
					covered.add(target);
			}
		}
	}

	/**
	 * Prints test cases covering input targets.
	 */
	public void printTestCases() {
		if (testCases == null)
			minimizeTestCases();
		Iterator i = testCases.iterator();
		int n = 0;
		while (i.hasNext()) {
			Chromosome id = (Chromosome) i.next();
			n++;
			System.out.print("TC #" + n + ": ");
			System.out.print(id.toString());
			System.out.print(" % covers: ");
			Set coveredTargets = id.getCoveredTargets();
			Iterator j = coveredTargets.iterator();
			boolean first = true;
			while (j.hasNext()) {
				Target target = (Target) j.next();
				if (first)
					first = false;
				else
					System.out.print(", ");
				System.out.print(target);
			}
			System.out.println();
		}

		Set coveredTargets = targetCoveredByIndividual.keySet();
		List allTargets = getAllTargets();
		if (coveredTargets.size() != allTargets.size()) {
			System.out.print("Not yet covered:");
			i = allTargets.iterator();
			while (i.hasNext()) {
				Target x = (Target) i.next();
				if (targetCoveredByIndividual.get(x) == null)
					System.out.print(" " + x);
			}
			System.out.println();
		}

		int covTgt = coveredTargets.size();
		int toCovTgt = allTargets.size();
		Double coverage = new Double(100.0 * covTgt / toCovTgt);
		String cov = coverage.toString();
		if (cov.indexOf(".") != -1 && cov.substring(cov.indexOf(".")).length() > 2)
			cov = cov.substring(0, cov.indexOf(".") + 3);
		System.out.println("Coverage: " + covTgt + "/" + toCovTgt + " = " + cov + "%");
		System.out.println("Test case executions: " + TestCaseExecutor.testCaseExecutions);
	}

	public static void main(String args[]) throws Exception {
		String classNameString = "BinaryTree";
		String relativePathString = "src/";
		args = new String[] { relativePathString + classNameString + ".oj", relativePathString + classNameString + ".sign",
				relativePathString + classNameString + ".tgt", relativePathString + classNameString + ".path",
				relativePathString + "params.txt", relativePathString + classNameString + ".junit" };

		String[] srcfiles = { args[0] };
		String signFile = args[1];
		targetFile = args[2];
		pathsFile = args[3];
		paramsFile = args[4];
		junitFile = args[5];
		openjava.ojc.Main.main(srcfiles);
		Thread.sleep(5000);
		TestGenerator tg = new DataFlowTestGenerator();

		tg.generateTestCases(signFile);

		tg.minimizeTestCases();
		tg.printTestCases();
		tg.printJunitFile();
	}
}
