package miniJava;

import java.util.ArrayList;
import java.util.List;

public class ErrorReporter {
	private List<String> _errorQueue;

	public ErrorReporter() {
		this._errorQueue = new ArrayList<String>();
	}

	public boolean hasErrors() {
		return !_errorQueue.isEmpty();
	}

	public void outputErrors() {
		for (String error : _errorQueue) {
			System.out.println(error);
		}
	}

	public void reportError(String... error) {
		StringBuilder sb = new StringBuilder();

		for (String s : error)
			sb.append(s);

		_errorQueue.add(sb.toString());
	}
}