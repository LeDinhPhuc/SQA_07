/**
 * @(#) FaultSeeder.java	v. 1.0 - July 18, 2004
 *
 * This software was written by Paolo Tonella (tonella@itc.it) at ITC-irst,
 * Centro per la Ricerca Scientifica e Tecnlogica.
 *
 * Distributed under the Gnu GPL (General Public License). See GPL.TXT
 */

package it.itc.etoc;
import java.io.*;
import java.util.regex.*;

import it.itc.etoc.FaultSeeder;

public class FaultSeeder {
  int faultLine = 0;
  String fault = "";

  public FaultSeeder(int line) {
    faultLine = line;
  }

  public void readFault(String faultFile) {
    try {
      String s;
      Matcher m;
      Pattern p = Pattern.compile("^(\\d+):(.*)");
      BufferedReader reader = new BufferedReader(new FileReader(faultFile));
      while ((s = reader.readLine()) != null) {
      	m = p.matcher(s);
	if (m.find()) {
	  int curFaultLine = Integer.parseInt(m.group(1));
	  if (curFaultLine == faultLine) 
	    fault = m.group(2);
	}
      }
    } catch(FileNotFoundException e) {
      System.err.println("File not found: " + faultFile);
      System.exit(1);
    } catch(IOException e) {
      System.err.println("I/O error.");
      System.exit(1);
    }
  }

  public void seedFault(String javaFile) {
    try {
      String s;
      int curLine = 0;
      BufferedReader reader = new BufferedReader(new FileReader(javaFile));
      while ((s = reader.readLine()) != null) {
	curLine++;
	if (faultLine == curLine)
	  System.out.println(fault);      
	else
	  System.out.println(s); 
      }    
    } catch(FileNotFoundException e) {
      System.err.println("File not found: " + javaFile);
      System.exit(1);
    } catch(IOException e) {
      System.err.println("I/O error.");
      System.exit(1);
    }
  }

  public static void main(String args[]) {
    if (args.length != 3) {
      System.err.println("Usage: java FaultSeeder file.orig.java " +
			 "file.faults line-num");
      System.exit(1);
    }
    FaultSeeder seeder = new FaultSeeder(Integer.parseInt(args[2]));
    seeder.readFault(args[1]);
    seeder.seedFault(args[0]);
  }
}
