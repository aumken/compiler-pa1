package miniJava.SyntacticAnalyzer;

import miniJava.ErrorReporter;

public class Parser {
	private Scanner _scanner;
	private ErrorReporter _errors;
	private Token _currentToken;

	public Parser(Scanner scanner, ErrorReporter errors) {
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
		} catch (SyntaxError e) {
		}
	}

	// Program ::= (ClassDeclaration)* eot
	private void parseProgram() throws SyntaxError {
		// DONE: Keep parsing class declarations until eot
		while (_currentToken.getTokenType() != TokenType.EOT) {
			parseClassDeclaration();
		}
		accept(TokenType.EOT);
	}

	// ClassDeclaration ::= class identifier { (FieldDeclaration|MethodDeclaration)*
	// }
	private void parseClassDeclaration() throws SyntaxError {
		// DONE: Take in a "class" token (check by the TokenType)
		// What should be done if the first token isn't "class"?
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
				parseFieldDeclaration();
			} else if (_currentToken.getTokenType() == TokenType.VOID) {
				parseMethodDeclaration();
			} else {
				throw new SyntaxError();
			}
		}

		// DONE: Take in a }
		accept(TokenType.RCURLY);
	}

	// EXTRA FUNCTIONS

	private void parseType() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.INT) {
			accept(TokenType.INT);
			// array ?
			if (_currentToken.getTokenType() == TokenType.LBRACKET) {
				accept(TokenType.LBRACKET);
				accept(TokenType.RBRACKET);
			}
		} else if (_currentToken.getTokenType() == TokenType.IDENTIFIER) {
			accept(TokenType.IDENTIFIER);
			// array ?
			if (_currentToken.getTokenType() == TokenType.LBRACKET) {
				accept(TokenType.LBRACKET);
				accept(TokenType.RBRACKET);
			}
		} else if (_currentToken.getTokenType() == TokenType.BOOLEAN) {
			accept(TokenType.BOOLEAN);
		} else {
			throw new SyntaxError();
		}
	}

	private void parseFieldDeclaration() throws SyntaxError {
		parseType();
		accept(TokenType.IDENTIFIER);
		accept(TokenType.SEMICOLON);
	}

	private void parseMethodDeclaration() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.VOID) {
			accept(TokenType.VOID);
		} else {
			parseType();
		}
		accept(TokenType.IDENTIFIER);
		accept(TokenType.LPAREN);
		if (_currentToken.getTokenType() != TokenType.RPAREN) {
			parseParameterList();
		}
		accept(TokenType.RPAREN);
		accept(TokenType.LCURLY);
		while (_currentToken.getTokenType() != TokenType.RCURLY) {
			parseStatement();
		}
		accept(TokenType.RCURLY);
	}

	private void parseParameterList() throws SyntaxError {
		parseType();
		accept(TokenType.IDENTIFIER);
		while (_currentToken.getTokenType() == TokenType.COMMA) {
			accept(TokenType.COMMA);
			parseType();
			accept(TokenType.IDENTIFIER);
		}
	}

	private void parseStatement() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.LCURLY) {
			accept(TokenType.LCURLY);
			while (_currentToken.getTokenType() != TokenType.RCURLY) {
				parseStatement();
			}
			accept(TokenType.RCURLY);
		} else if (_currentToken.getTokenType() == TokenType.INT || _currentToken.getTokenType() == TokenType.BOOLEAN
				|| _currentToken.getTokenType() == TokenType.IDENTIFIER) {
			parseType();
			accept(TokenType.IDENTIFIER);
			accept(TokenType.EQUALS);
			parseExpression();
			accept(TokenType.SEMICOLON);
		} else if (_currentToken.getTokenType() == TokenType.IF) {
			accept(TokenType.IF);
			accept(TokenType.LPAREN);
			parseExpression();
			accept(TokenType.RPAREN);
			parseStatement();
			if (_currentToken.getTokenType() == TokenType.ELSE) {
				accept(TokenType.ELSE);
				parseStatement();
			}
		} else if (_currentToken.getTokenType() == TokenType.WHILE) {
			accept(TokenType.WHILE);
			accept(TokenType.LPAREN);
			parseExpression();
			accept(TokenType.RPAREN);
			parseStatement();
		} else if (_currentToken.getTokenType() == TokenType.RETURN) {
			accept(TokenType.RETURN);
			if (_currentToken.getTokenType() != TokenType.SEMICOLON) {
				parseExpression();
			}
			accept(TokenType.SEMICOLON);
		} else {
			parseReference();
			// array ?
			if (_currentToken.getTokenType() == TokenType.LBRACKET) {
				accept(TokenType.LBRACKET);
				parseExpression();
				accept(TokenType.RBRACKET);
				accept(TokenType.EQUALS);
				parseExpression();
				accept(TokenType.SEMICOLON);
			}
			// method call ?
			else if (_currentToken.getTokenType() == TokenType.LPAREN) {
				accept(TokenType.LPAREN);
				if (_currentToken.getTokenType() != TokenType.RPAREN) {
					parseArgumentList();
				}
				accept(TokenType.RPAREN);
				accept(TokenType.SEMICOLON);
			} else {
				accept(TokenType.EQUALS);
				parseExpression();
				accept(TokenType.SEMICOLON);
			}
		}
	}

	private void parseArgumentList() throws SyntaxError {
		parseExpression();
		while (_currentToken.getTokenType() == TokenType.COMMA) {
			accept(TokenType.COMMA);
			parseExpression();
		}
	}

	private void parseReference() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.IDENTIFIER || _currentToken.getTokenType() == TokenType.THIS) {
			accept(_currentToken.getTokenType());
			while (_currentToken.getTokenType() == TokenType.PERIOD) {
				accept(TokenType.PERIOD);
				accept(TokenType.IDENTIFIER);
			}
		} else {
			throw new SyntaxError();
		}
	}

	private void parseExpression() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.UNOP) {
			accept(TokenType.UNOP);
			parseExpression();
		} else if (_currentToken.getTokenType() == TokenType.IDENTIFIER ||
				_currentToken.getTokenType() == TokenType.THIS) {
			parseReference();
			if (_currentToken.getTokenType() == TokenType.LBRACKET) {
				accept(TokenType.LBRACKET);
				parseExpression();
				accept(TokenType.RBRACKET);
			} else if (_currentToken.getTokenType() == TokenType.LPAREN) {
				accept(TokenType.LPAREN);
				if (_currentToken.getTokenType() != TokenType.RPAREN) {
					parseArgumentList();
				}
				accept(TokenType.RPAREN);
			}
		} else if (_currentToken.getTokenType() == TokenType.LPAREN) {
			accept(TokenType.LPAREN);
			parseExpression();
			accept(TokenType.RPAREN);
		} else if (_currentToken.getTokenType() == TokenType.NUM ||
				_currentToken.getTokenType() == TokenType.TRUE ||
				_currentToken.getTokenType() == TokenType.FALSE) {
			accept(_currentToken.getTokenType());
		} else if (_currentToken.getTokenType() == TokenType.NEW) {
			accept(TokenType.NEW);
			if (_currentToken.getTokenType() == TokenType.IDENTIFIER) {
				accept(TokenType.IDENTIFIER);
				if (_currentToken.getTokenType() == TokenType.LPAREN) {
					accept(TokenType.LPAREN);
					accept(TokenType.RPAREN);
				} else if (_currentToken.getTokenType() == TokenType.LBRACKET) {
					accept(TokenType.LBRACKET);
					parseExpression();
					accept(TokenType.RBRACKET);
				} else {
					throw new SyntaxError();
				}
			} else if (_currentToken.getTokenType() == TokenType.INT) {
				accept(TokenType.INT);
				accept(TokenType.LBRACKET);
				parseExpression();
				accept(TokenType.RBRACKET);
			} else {
				throw new SyntaxError();
			}
		} else {
			throw new SyntaxError();
		}
		
		if (_currentToken.getTokenType() == TokenType.BINOP) {
			accept(TokenType.BINOP);
			parseExpression();
		}
	}
	
	// This method will accept the token and retrieve the next token.
	// Can be useful if you want to error check and accept all-in-one.
	private void accept(TokenType expectedType) throws SyntaxError {
		if (_currentToken.getTokenType() == expectedType) {
			_currentToken = _scanner.scan();
			return;
		}

		// DONE: Report an error here.
		// "Expected token X, but got Y"
		_errors.reportError("Expected token " + expectedType.toString() + ", but got "
				+ _currentToken.getTokenType().toString());
		throw new SyntaxError();
	}
}
