package miniJava;

import java.util.ArrayList;
import java.util.List;

// DONE: Note this class lacks a lot of things.
//  First of all, errors are simple strings,
//  perhaps it may be worthwhile to augment this reporter
//  with requiring line numbers.
public class ErrorReporter {
	private List<String> _errorQueue;
	
	public ErrorReporter() {
		this._errorQueue = new ArrayList<String>();
	}
	
	public boolean hasErrors() {
		// DONE: Check if errorQueue is non-empty
		return !_errorQueue.isEmpty();
	}
	
	public void outputErrors() {
		// DONE: output all errors in the errorQueue
		for (String error : _errorQueue) {
			System.out.println(error);
		}
	}
	
	public void reportError(String ...error) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : error)
			sb.append(s);
		
		_errorQueue.add(sb.toString());
	}
}
