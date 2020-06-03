package it.itc.etoc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GenerationTargetPath {
	String classUnderTest;
	public GenerationTargetPath(String classUnderTest) {
		this.classUnderTest = classUnderTest;
	}

	
	public static void main(String[] args) {	
		String relativePathString = "src/";
		String tgtFile = relativePathString + "BinaryTree.target";
		Path path = Paths.get(tgtFile);

		try (BufferedWriter writer = Files.newBufferedWriter(path)) {
			writer.write("Hello World !!!");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new FileReader(tgtFile));
			String tgtPath;
			while ((tgtPath = reader.readLine()) != null) {
				System.out.println(tgtPath);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
