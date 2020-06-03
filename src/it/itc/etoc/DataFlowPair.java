package it.itc.etoc;

import it.itc.etoc.DataFlowPair;
import openjava.ptree.*;
import openjava.ptree.util.*;

/**
 * Variable and branch involved in a def or in a use.
 *
 */
public class DataFlowPair {
	public int branch;
	public String var;

	public DataFlowPair(int br, String v) {
		this.branch = br;
		this.var = v;
	}

	public boolean equals(Object obj) {
		DataFlowPair pair = (DataFlowPair) obj;
		return (branch == pair.branch) && var.equals(pair.var);
	}

	public int hashCode() {
		return branch * 13451 + var.hashCode();
	}
}
