package it.itc.etoc;

import it.itc.etoc.TestGenerator;

public class Main {
	TestGenerator tesGenerator = new TestGenerator();

	public static void main(String[] args) throws Exception {
		args = new String[] { "src\\example\\BinaryTree\\BinaryTree.sign",
				"src\\example\\BinaryTree\\BinaryTree.tgt",
				"src\\example\\BinaryTree\\BinaryTree.path", "-params",
				"src\\example\\BinaryTree\\instrumented\\params.txt", "-junit",
				"src\\example\\BinaryTree\\BinaryTree.java", "-dataflow" };
		TestGenerator.main(args);
	}
}
