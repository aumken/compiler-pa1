package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;

import miniJava.ErrorReporter;

public class Scanner {
	private InputStream _in;
	private ErrorReporter _errors;
	private StringBuilder _currentText;
	private char _currentChar;
	
	public Scanner( InputStream in, ErrorReporter errors ) {
		this._in = in;
		this._errors = errors;
		this._currentText = new StringBuilder();
		
		nextChar();
	}
	
	public Token scan() {
		// DONE: This function should check the current char to determine what the token could be.
		
		// DONE: Consider what happens if the current char is whitespace

		while (Character.isWhitespace(_currentChar)) {
			skipIt();
		}

		// DONE: Consider what happens if there is a comment (// or /* */)

		if (_currentChar == '/') {
			// single line
			if (lookUp() == '/') {
				skipIt();
				skipIt();
				while (_currentChar != '\n' && _currentChar != (char)-1) {
					skipIt();
				}
				skipIt();
			}
			// multi line
			else if(lookUp() == '*') {
				skipIt();
				skipIt();
				while(_currentChar != (char)-1) {
					if(_currentChar == '*' && lookUp() == '/') {
						skipIt();
						skipIt();
						break;
					}
					skipIt();
				}
			}
			else {
				takeIt();
				return makeToken(TokenType.BINOP);
			}
		}	
		
		// DONE: What happens if there are no more tokens?

		if (_currentChar == (char)-1) {
			return makeToken(TokenType.EOT);
		}
		
		// DONE: Determine what the token is. For example, if it is a number
		//  keep calling takeIt() until _currentChar is not a number. Then
		//  create the token via makeToken(TokenType.IntegerLiteral) and return it.

		if(Character.isLetter(_currentChar)) {
			takeIt();
			while(Character.isLetterOrDigit(_currentChar) || _currentChar == '_') {
				takeIt();
			}
			
			String tokenText = _currentText.toString();
			
			switch(tokenText) {
			case "class": return makeToken(TokenType.CLASS);
			case "public": return makeToken(TokenType.PUBLIC);
			case "private": return makeToken(TokenType.PRIVATE);
			case "static": return makeToken(TokenType.STATIC);
			case "void": return makeToken(TokenType.VOID);
			case "int": return makeToken(TokenType.INT);
			case "boolean": return makeToken(TokenType.BOOLEAN);
			case "this": return makeToken(TokenType.THIS);
			case "if": return makeToken(TokenType.IF);
			case "else": return makeToken(TokenType.ELSE);
			case "while": return makeToken(TokenType.WHILE);
			case "return": return makeToken(TokenType.RETURN);
			case "true": return makeToken(TokenType.TRUE);
			case "false": return makeToken(TokenType.FALSE);
			case "new": return makeToken(TokenType.NEW);
			default: return makeToken(TokenType.IDENTIFIER);
			}
		}
		else if (Character.isDigit(_currentChar)) {
			takeIt();
			while(Character.isDigit(_currentChar)) {
				takeIt();
			}
			return makeToken(TokenType.NUM);
		}
		else if (_currentChar == '=') {
			takeIt();
			if (_currentChar == '=') {
				takeIt();
				return makeToken(TokenType.BINOP);
			} else {
				return makeToken(TokenType.EQUALS);
			}
		}
		else if(_currentChar == '!') {
			takeIt();
			if (_currentChar == '=') {
				takeIt();
				return makeToken(TokenType.BINOP);
			}
			else {
				return makeToken(TokenType.UNOP);
			}
		}
		else if (_currentChar == '>') {
			takeIt();
			if(_currentChar == '=') {
				takeIt();
			}
			return makeToken(TokenType.BINOP);
		}
		else if (_currentChar == '<') {
			takeIt();
			if(_currentChar == '=') {
				takeIt();
			}
			return makeToken(TokenType.BINOP);
		}
		else if (_currentChar == '&') {
			takeIt();
			if(_currentChar == '&') {
				takeIt();
				return makeToken(TokenType.BINOP);
			}
			else {
				_errors.reportError(_currentChar + " is an illegal character");
				return makeToken(TokenType.ERROR);
			}
		}
		else if (_currentChar == '|') {
			takeIt();
			if(_currentChar == '|') {
				takeIt();
				return makeToken(TokenType.BINOP);
			}
			else {
				_errors.reportError(_currentChar + " is an illegal character");
				return makeToken(TokenType.ERROR);
			}
		}
		else if (_currentChar == '+') {
			takeIt();
			return makeToken(TokenType.BINOP);
		}
		else if (_currentChar == '-') {
			takeIt();
			return makeToken(TokenType.UNOP);
		}
		else if (_currentChar == '*') {
			takeIt();
			return makeToken(TokenType.BINOP);
		}
		else if (_currentChar == '/') {
			takeIt();
			return makeToken(TokenType.BINOP);
		}
		else if(_currentChar == ';') {
			takeIt();
			return makeToken(TokenType.SEMICOLON);
		}
		else if (_currentChar == '(') {
			takeIt();
			return makeToken(TokenType.LPAREN);
		}
		else if (_currentChar == ')') {
			takeIt();
			return makeToken(TokenType.RPAREN);
		}
		else if (_currentChar == '{') {
			takeIt();
			return makeToken(TokenType.LCURLY);
		}
		else if (_currentChar == '}') {
			takeIt();
			return makeToken(TokenType.RCURLY);
		}
		else if (_currentChar == '[') {
			takeIt();
			return makeToken(TokenType.LBRACKET);
		}
		else if (_currentChar == ']') {
			takeIt();
			return makeToken(TokenType.RBRACKET);
		}
		else if(_currentChar == ',') {
			takeIt();
			return makeToken(TokenType.COMMA);
		}
		else if(_currentChar == '.') {
			takeIt();
			return makeToken(TokenType.PERIOD);
		}
		else {
			_errors.reportError(_currentChar + " is an illegal character");
			nextChar();
			return makeToken(TokenType.ERROR);
		}
	}
	// look up next character without changing the _currentChar
	private char lookUp() {
		try {
			int peek = _in.read();
			_in.close();
			return (char) peek;
		}
		catch(IOException e) {
			_errors.reportError("io error");
		}
		return (char) -1;
	}
	
	private void takeIt() {
		_currentText.append(_currentChar);
		nextChar();
	}
	
	private void skipIt() {
		nextChar();
	}
	
	private void nextChar() {
		try {
			int c = _in.read();
			_currentChar = (char)c;
			
			// DONE: What happens if c == -1?

			if(c == -1) {
				_currentChar = (char) -1;
			}
			
			// DONE: What happens if c is not a regular ASCII character?

			if (c > 127) {
				_errors.reportError("non ascii character detected.");
			}
			
		} catch( IOException e ) {
			// DONE: Report an error here
			_errors.reportError("io error");
		}
	}
	
	private Token makeToken( TokenType toktype ) {
		// DONE: return a new Token with the appropriate type and text
		//  contained in 
		Token newToken = new Token(toktype, _currentText.toString());
		_currentText.setLength(0);
		return newToken;
	}
}
