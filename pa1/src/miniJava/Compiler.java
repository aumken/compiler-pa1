package miniJava;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import miniJava.SyntacticAnalyzer.Parser;
import miniJava.SyntacticAnalyzer.Scanner;
public class Compiler {
	// Main function, the file to compile will be an argument.
	public static void main(String[] args) {
		// DONE: Instantiate the ErrorReporter object
		ErrorReporter reporter = new ErrorReporter();
		
		// DONE: Check to make sure a file path is given in args
		if (args.length == 0) {
			System.out.println("no input file given");
			return;
		}

		String inputFile = args[0];

		// DONE: Create the inputStream using new FileInputStream
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(inputFile);
		} catch (FileNotFoundException e) {
			reporter.reportError("no input file found");
			return;
		}
		
		// DONE: Instantiate the scanner with the input stream and error object
		Scanner scanner = new Scanner(inputStream, reporter);

		// DONE: Instantiate the parser with the scanner and error object
		Parser parser = new Parser(scanner, reporter);
		
		// DONE: Call the parser's parse function
		parser.parse();
		
		// DONE: Check if any errors exist, if so, println("Error")
		//  then output the errors
		if (reporter.hasErrors()) {
			System.out.println("Error");
			reporter.outputErrors();
		}
		
		// DONE: If there are no errors, println("Success")
		else {
			System.out.println("Success");
		}
	}
}
