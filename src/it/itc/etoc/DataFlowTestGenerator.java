/**
 * @(#) DataFlowTestGenerator.java	v. 1.0 - September 13, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.util.regex.*;

import it.itc.etoc.BranchTarget;
import it.itc.etoc.DataFlowTarget;
import it.itc.etoc.TestGenerator;

import java.util.*;
import java.io.*;

class DataFlowTestGenerator extends TestGenerator {
 
  static Map def = new HashMap(); // BranchTarget -> Variable

  /**
   * True if node defines var.
   *
   */
  public static boolean isDef(BranchTarget node, String var) {
    return def.containsKey(node) && def.get(node).equals(var);
  }

  /**
   * Reads selected targets from text file (target.txt).
   * 
   * Format of target.txt:
   *
   * <pre>
   * (1, 3, BinaryTree.root)
   * </pre>
   */
  public void readTarget() {
    try{
      String s;
      Pattern p = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*([^\\s]+)\\)");
      BufferedReader in
	= new BufferedReader(new FileReader(targetFile));
      while ((s = in.readLine()) != null) {
	Matcher m = p.matcher(s);
	if (!m.find()) continue;
	int src = Integer.parseInt(m.group(1));
	int dst = Integer.parseInt(m.group(2));
	String var = m.group(3);
	DataFlowTarget tgt = new DataFlowTarget(src, dst, var);
	targets.add(tgt);
	def.put(tgt.getSourceBranch(), var);
      }
    } catch (NumberFormatException e) {
      System.err.println("Wrong format file: " + targetFile);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("IO error: " + targetFile);
      System.exit(1);
    }
  }
  
}

