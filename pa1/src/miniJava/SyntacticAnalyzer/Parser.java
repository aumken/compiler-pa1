package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;

public class Parser {
	private Scanner _scanner;
	private ErrorReporter _errors;
	private Token _currentToken;
	
	public Parser( Scanner scanner, ErrorReporter errors ) {
		this._scanner = scanner;
		this._errors = errors;
		this._currentToken = this._scanner.scan();
	}
	
	class SyntaxError extends Error {
		private static final long serialVersionUID = -6461942006097999362L;
	}
	
	public void parse() {
		try {
			// The first thing we need to parse is the Program symbol
			parseProgram();
		} catch( SyntaxError e ) { }
	}
	
	// Program ::= (ClassDeclaration)* eot
	private void parseProgram() throws SyntaxError {
		// DONE: Keep parsing class declarations until eot
		while (_currentToken.getTokenType() != TokenType.EOT) {
			parseClassDeclaration();
		}
		accept(TokenType.EOT);
	}
	
	// ClassDeclaration ::= class identifier { (FieldDeclaration|MethodDeclaration)* }
	private void parseClassDeclaration() throws SyntaxError {
		// DONE: Take in a "class" token (check by the TokenType)
		//  What should be done if the first token isn't "class"?
		accept(TokenType.CLASS);
		
		// DONE: Take in an identifier token
		accept(TokenType.IDENTIFIER);
		
		// DONE: Take in a {
		accept(TokenType.LCURLY);
		
		// IN PROGRESS: Parse either a FieldDeclaration or MethodDeclaration
		while (_currentToken.getTokenType() != TokenType.RCURLY) {
			if (_currentToken.getTokenType() == TokenType.PUBLIC || _currentToken.getTokenType() == TokenType.PRIVATE) {
				accept(_currentToken.getTokenType());
			}

			if (_currentToken.getTokenType() == TokenType.STATIC) {
				accept(TokenType.STATIC);
			}

			if (_currentToken.getTokenType() == TokenType.INT
					|| _currentToken.getTokenType() == TokenType.BOOLEAN
					|| _currentToken.getTokenType() == TokenType.IDENTIFIER) {
				/* Add back: parseFieldDeclaration();*/
			} else if (_currentToken.getTokenType() == TokenType.VOID) {
				/* Add back: parseMethodDeclaration();*/
			} else {
				throw new SyntaxError();
			}
		}

		// DONE: Take in a }
		accept(TokenType.RCURLY);
	}
	
	// This method will accept the token and retrieve the next token.
	//  Can be useful if you want to error check and accept all-in-one.
	private void accept(TokenType expectedType) throws SyntaxError {
		if( _currentToken.getTokenType() == expectedType ) {
			_currentToken = _scanner.scan();
			return;
		}
		
		// DONE: Report an error here.
		//  "Expected token X, but got Y"
		_errors.reportError("Expected token " + expectedType.toString() + ", but got "
				+ _currentToken.getTokenType().toString());
		throw new SyntaxError();
	}
}
