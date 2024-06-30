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
			parseProgram();
		} catch (SyntaxError e) {
		}
	}

	private void parseProgram() throws SyntaxError {
		while (_currentToken.getTokenType() != TokenType.EOT) {
			parseClassDeclaration();
		}
		accept(TokenType.EOT);
	}

	private void parseClassDeclaration() throws SyntaxError {
		accept(TokenType.CLASS);
		accept(TokenType.IDENTIFIER, "36");
		accept(TokenType.LCURLY);

		while (_currentToken.getTokenType() != TokenType.RCURLY) {
			if (_currentToken.getTokenType() == TokenType.PUBLIC ||
					_currentToken.getTokenType() == TokenType.PRIVATE ||
					_currentToken.getTokenType() == TokenType.STATIC ||
					_currentToken.getTokenType() == TokenType.VOID ||
					isType(_currentToken.getTokenType())) {
				if (isMethodDeclaration()) {
					parseMethodDeclaration();
				} else {
					parseFieldDeclaration();
				}
			} else {
				_errors.reportError("Expected field or method declaration, but got ",
						_currentToken.getTokenText());
				throw new SyntaxError();
			}
		}
		accept(TokenType.RCURLY);
	}

	private boolean isMethodDeclaration() {
		return _currentToken.getTokenType() == TokenType.PUBLIC ||
				_currentToken.getTokenType() == TokenType.PRIVATE ||
				_currentToken.getTokenType() == TokenType.STATIC ||
				_currentToken.getTokenType() == TokenType.VOID ||
				isType(_currentToken.getTokenType());
	}

	private void parseFieldDeclaration() throws SyntaxError {
		parseVisibility();
		parseAccess();
		parseType();
		accept(TokenType.IDENTIFIER, "71");
		if (_currentToken.getTokenType() == TokenType.SEMICOLON) {
			accept(TokenType.SEMICOLON);
		} else {
			_errors.reportError("Expected token SEMICOLON, but got ", _currentToken.getTokenText());
			throw new SyntaxError();
		}
	}
	
	private void parseMethodDeclaration() throws SyntaxError {
		parseVisibility();
		parseAccess();
		if (_currentToken.getTokenType() == TokenType.VOID) {
			accept(TokenType.VOID);
		} else {
			parseType();
		}
		accept(TokenType.IDENTIFIER, "88");
		if (_currentToken.getTokenType() == TokenType.LPAREN) {
			accept(TokenType.LPAREN);
			if (_currentToken.getTokenType() != TokenType.RPAREN) {
				parseParameterList();
			}
			accept(TokenType.RPAREN);
		}
		if (_currentToken.getTokenType() == TokenType.LCURLY) {
			parseBlock();
		} else if (_currentToken.getTokenType() == TokenType.SEMICOLON) {
			accept(TokenType.SEMICOLON);
		} else {
			_errors.reportError("Expected method body or semicolon, but got ", _currentToken.getTokenText());
			throw new SyntaxError();
		}
	}

	

	private void parseVisibility() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.PUBLIC ||
				_currentToken.getTokenType() == TokenType.PRIVATE) {
			accept(_currentToken.getTokenType());
		}
	}

	private void parseAccess() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.STATIC) {
			accept(TokenType.STATIC);
		}
	}

	private void parseType() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.INT) {
			accept(TokenType.INT);
		} else if (_currentToken.getTokenType() == TokenType.BOOLEAN) {
			accept(TokenType.BOOLEAN);
		} else if (_currentToken.getTokenType() == TokenType.IDENTIFIER) {
			accept(TokenType.IDENTIFIER, "127");
		} else {
			_errors.reportError("Expected type, but got ", _currentToken.getTokenText());
			throw new SyntaxError();
		}

		if (_currentToken.getTokenType() == TokenType.LSQUARE) {
			accept(TokenType.LSQUARE);
			accept(TokenType.RSQUARE);
		}
	}
	private void parseParameterList() throws SyntaxError {
		parseType();
		accept(TokenType.IDENTIFIER, "140");
		while (_currentToken.getTokenType() == TokenType.COMMA) {
			accept(TokenType.COMMA);
			parseType();
			accept(TokenType.IDENTIFIER, "144");
		}
	}

	private void parseBlock() throws SyntaxError {
		accept(TokenType.LCURLY);
		while (_currentToken.getTokenType() != TokenType.RCURLY) {
			parseStatement();
		}
		accept(TokenType.RCURLY);
	}

	private void parseStatement() throws SyntaxError {
		switch (_currentToken.getTokenType()) {
			case LCURLY:
				parseBlock();
				break;
			case IF:
				parseIfStatement();
				break;
			case WHILE:
				parseWhileStatement();
				break;
			case RETURN:
				parseReturnStatement();
				break;
			default:
				parseExpressionStatement();
				break;
		}
	}

	private void parseIfStatement() throws SyntaxError {
		accept(TokenType.IF);
		accept(TokenType.LPAREN);
		parseExpression();
		accept(TokenType.RPAREN);
		parseStatement();
		if (_currentToken.getTokenType() == TokenType.ELSE) {
			accept(TokenType.ELSE);
			parseStatement();
		}
	}

	private void parseWhileStatement() throws SyntaxError {
		accept(TokenType.WHILE);
		accept(TokenType.LPAREN);
		parseExpression();
		accept(TokenType.RPAREN);
		parseStatement();
	}

	private void parseReturnStatement() throws SyntaxError {
		accept(TokenType.RETURN);
		if (_currentToken.getTokenType() != TokenType.SEMICOLON) {
			parseExpression();
		}
		accept(TokenType.SEMICOLON);
	}

	private void parseExpressionStatement() throws SyntaxError {
    if (isType(_currentToken.getTokenType())) {
        parseType();
        accept(TokenType.IDENTIFIER, "207");
        if (_currentToken.getTokenType() == TokenType.EQUALS) {
            accept(TokenType.EQUALS);
            parseExpression();
        }
    } else if (_currentToken.getTokenType() == TokenType.THIS
            || _currentToken.getTokenType() == TokenType.IDENTIFIER) {
        parseReference();
        if (_currentToken.getTokenType() == TokenType.EQUALS) {
            accept(TokenType.EQUALS);
            parseExpression();
        }
    } else {
        parseExpression();
    }
    accept(TokenType.SEMICOLON);
}
	
	
	private void parseExpression() throws SyntaxError {
		parseLogicalOrExpression();
	}

	private void parseLogicalOrExpression() throws SyntaxError {
		parseLogicalAndExpression();
		while (_currentToken.getTokenType() == TokenType.OPERATOR && _currentToken.getTokenText().equals("||")) {
			accept(TokenType.OPERATOR);
			parseLogicalAndExpression();
		}
	}

	private void parseLogicalAndExpression() throws SyntaxError {
		parseEqualityExpression();
		while (_currentToken.getTokenType() == TokenType.OPERATOR && _currentToken.getTokenText().equals("&&")) {
			accept(TokenType.OPERATOR);
			parseEqualityExpression();
		}
	}

	private void parseEqualityExpression() throws SyntaxError {
		parseRelationalExpression();
		while (_currentToken.getTokenType() == TokenType.OPERATOR &&
				(_currentToken.getTokenText().equals("==") || _currentToken.getTokenText().equals("!="))) {
			accept(TokenType.OPERATOR);
			parseRelationalExpression();
		}
	}

	private void parseRelationalExpression() throws SyntaxError {
		parseAdditiveExpression();
		while (_currentToken.getTokenType() == TokenType.OPERATOR &&
				(_currentToken.getTokenText().equals("<") || _currentToken.getTokenText().equals(">") ||
						_currentToken.getTokenText().equals("<=") || _currentToken.getTokenText().equals(">="))) {
			accept(TokenType.OPERATOR);
			parseAdditiveExpression();
		}
	}

	private void parseAdditiveExpression() throws SyntaxError {
		parseMultiplicativeExpression();
		while (_currentToken.getTokenType() == TokenType.OPERATOR &&
				(_currentToken.getTokenText().equals("+") || _currentToken.getTokenText().equals("-"))) {
			accept(TokenType.OPERATOR);
			parseMultiplicativeExpression();
		}
	}

	private void parseMultiplicativeExpression() throws SyntaxError {
		parseUnaryExpression();
		while (_currentToken.getTokenType() == TokenType.OPERATOR &&
				(_currentToken.getTokenText().equals("*") || _currentToken.getTokenText().equals("/"))) {
			accept(TokenType.OPERATOR);
			parseUnaryExpression();
		}
	}

	private void parseUnaryExpression() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.OPERATOR &&
				(_currentToken.getTokenText().equals("!") || _currentToken.getTokenText().equals("-"))) {
			accept(TokenType.OPERATOR);
			parseUnaryExpression();
		} else {
			parsePrimaryExpression();
		}
	}

	private void parsePrimaryExpression() throws SyntaxError {
		switch (_currentToken.getTokenType()) {
			case INTEGER_LITERAL:
				accept(TokenType.INTEGER_LITERAL);
				break;
			case TRUE:
				accept(TokenType.TRUE);
				break;
			case FALSE:
				accept(TokenType.FALSE);
				break;
			case THIS:
			case IDENTIFIER:
				parseReference();
				break;
			case NEW:
				parseNewExpression();
				break;
			case LPAREN:
				accept(TokenType.LPAREN);
				parseExpression();
				accept(TokenType.RPAREN);
				break;
			default:
				_errors.reportError("Unexpected token in primary expression: ", _currentToken.getTokenText());
				throw new SyntaxError();
		}
	}
	
	private void parseNewExpression() throws SyntaxError {
		accept(TokenType.NEW);
		if (_currentToken.getTokenType() == TokenType.INT) {
			accept(TokenType.INT);
			accept(TokenType.LSQUARE);
			parseExpression();
			accept(TokenType.RSQUARE);
		} else {

			accept(TokenType.IDENTIFIER, "331");
			if (_currentToken.getTokenType() == TokenType.LPAREN) {
				accept(TokenType.LPAREN);
				accept(TokenType.RPAREN);
			} else {
				accept(TokenType.LSQUARE);
				parseExpression();
				accept(TokenType.RSQUARE);
			}
		}
	}

	private void parseReference() throws SyntaxError {
		if (_currentToken.getTokenType() == TokenType.THIS) {
			accept(TokenType.THIS);
		} else if (_currentToken.getTokenType() == TokenType.IDENTIFIER) {
			accept(TokenType.IDENTIFIER, "347");
		} else {
			_errors.reportError("Expected 'this' or identifier, but got ", _currentToken.getTokenText());
			throw new SyntaxError();
		}

		while (true) {
			if (_currentToken.getTokenType() == TokenType.DOT) {
				accept(TokenType.DOT);
				accept(TokenType.IDENTIFIER, "356");
			} else if (_currentToken.getTokenType() == TokenType.LSQUARE) {
				accept(TokenType.LSQUARE);
				parseExpression();
				accept(TokenType.RSQUARE);
			} else if (_currentToken.getTokenType() == TokenType.LPAREN) {
				accept(TokenType.LPAREN);
				if (_currentToken.getTokenType() != TokenType.RPAREN) {
					parseArgumentList();
				}
				accept(TokenType.RPAREN);
			} else {
				break;
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

	private boolean isType(TokenType type) {
		return type == TokenType.INT || type == TokenType.BOOLEAN || type == TokenType.IDENTIFIER;
	}

	private void accept(TokenType expectedType) throws SyntaxError {
		if (_currentToken.getTokenType() == expectedType) {
			_currentToken = _scanner.scan();
		} else {
			_errors.reportError("Expected token ", expectedType.toString(),
					", but got ", _currentToken.getTokenText(), _currentToken.getTokenType().toString());
			throw new SyntaxError();
		}
	}

	private void accept(TokenType expectedType, String location) throws SyntaxError {
		if (_currentToken.getTokenType() == expectedType) {
			_currentToken = _scanner.scan();
		} else {
			_errors.reportError("Expected token ", expectedType.toString(),
					", but got ", _currentToken.getTokenText(), _currentToken.getTokenType().toString(), " at ", location);
			throw new SyntaxError();
		}
	}
}