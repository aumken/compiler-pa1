package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;

public class Compiler {
	public static void main(String[] args) {
		ErrorReporter errorReporter = new ErrorReporter();

		if (args.length == 0) {
			System.out.println("no input file specified");
			return;
		}

		String filePath = args[0];
		FileInputStream inputStream;

		try {
			inputStream = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			System.out.println("file not found" + filePath);
			return;
		}

		Scanner scanner = new Scanner(inputStream, errorReporter);
		Parser parser = new Parser(scanner, errorReporter);

		parser.parse();

		if (errorReporter.hasErrors()) {
			System.out.println("Error");
			errorReporter.outputErrors();
		} else {
			System.out.println("Success");
		}
	}
}