REQUIREMENTS:

eToc requires that the following tools are installed:

Java SDK (http://java.sun.com/)
Junit (http://junit.org/)
OpenJava (http://openjava.sourceforge.net/)


INSTALLATION:

Set the CLASSPATH variable:

setenv CLASSPATH <OPENJAVA-DIR>/classes:<JUNIT-DIR>/junit.jar:<INSTALL-DIR>/etoc.jar:.:${CLASSPATH}



EXECUTION:

Branch instrumentation:
cd examples/BinaryTree/instrumented
cp ../BinaryTree.java BinaryTree.oj
edit BinaryTree.oj:
     add "import it.itc.etoc.*;"
     insert "class BinaryTree instantiates BranchInstrumentor {...}"
     or "class BinaryTree instantiates DataFlowInstrumentor {...}" for data
     flow testing
java openjava.ojc.Main BinaryTree.oj
Note: only ONE class must be instrumented (unit test only supported)
Note: "instantiates" precedes "extends" and "implements" clauses.

edit BinaryTree.sign and insert ../BinaryTree.head at the beginning and
../BinaryTree.tail at the end. 

For data flow testing:
fix data dependences in tgt file (see examples/BinaryTree/BinaryTree.tgt).

Generation of mutated chromosomes (prerequisite: branch instrumentation):
cd examples/BinaryTree/instrumented
java it.itc.etoc.ChromosomeFormer BinaryTree.sign

Execution of test cases (prerequisite: branch instrumentation):
cd examples/BinaryTree/instrumented
java it.itc.etoc.TestCaseExecutor BinaryTree.sign

Test case generation (prerequisite: branch instrumentation):
cd examples/BinaryTree/instrumented
java it.itc.etoc.TestGenerator BinaryTree.sign BinaryTree.tgt BinaryTree.path -junit BinaryTreeTest.java
cp BinaryTreeTest.java ..
cd ..
edit BinaryTreeTest.java and add Junit assertions
javac BinaryTreeTest.java
java junit.swingui.TestRunner BinaryTreeTest &
(or java BinaryTreeTest)

Test case generation for data flow testing (prerequisite: data flow
instrumentation): 
cd examples/BinaryTree/instrumented
java it.itc.etoc.TestGenerator BinaryTree.sign BinaryTree.tgt BinaryTree.path -junit BinaryTreeTest.java -dataflow
cp BinaryTreeTest.java ..
cd ..
edit BinaryTreeTest.java and add Junit assertions
javac BinaryTreeTest.java
java junit.swingui.TestRunner BinaryTreeTest &
(or java BinaryTreeTest)


Note: the parameters of the algorithm can be changed by editing params.txt
(optional argument in the execution of TestGenerator):
java it.itc.etoc.TestGenerator BinaryTree.sign BinaryTree.tgt BinaryTree.path -params params.txt -junit BinaryTreeTest.java



COMPILATION:

Set the CLASSPATH variable:

setenv CLASSPATH <OPENJAVA-DIR>/classes:<INSTALL-DIR>:${CLASSPATH}

Run make
Run make jar



KNOWN LIMITATIONS:

1. In signature file, constructors for parameters must appear before the
   method with such parameters.
2. Call dependences must be added manually to control dependences
3. Data dependences must be fixed manually in tgt file

