package miniJava.SyntacticAnalyzer;

import java.io.IOException;
import java.io.InputStream;

import miniJava.ErrorReporter;

public class Scanner {
	private InputStream _in;
	private ErrorReporter _errors;
	private StringBuilder _currentText;
	private char _currentChar;
	private int _line;
	private int _column;

	public Scanner(InputStream in, ErrorReporter errors) {
		this._in = in;
		this._errors = errors;
		this._currentText = new StringBuilder();
		this._line = 1;
		this._column = 0;

		nextChar();
	}

	public Token scan() {
		_currentText.setLength(0);

		while (Character.isWhitespace(_currentChar)) {
			skipIt();
		}

		if (_currentChar == '/') {
			takeIt();
			if (_currentChar == '/') {
				skipSingleLineComment();
				return scan();
			} else if (_currentChar == '*') {
				skipMultiLineComment();
				return scan();
			} else {
				return makeToken(TokenType.OPERATOR);
			}
		}

		if (_currentChar == (char) -1) {
			return makeToken(TokenType.EOT);
		}

		if (Character.isLetter(_currentChar)) {
			return scanIdentifier();
		}

		if (Character.isDigit(_currentChar)) {
			return scanNumber();
		}

		return scanOperator();
	}

	private Token scanIdentifier() {
		while (Character.isLetterOrDigit(_currentChar) || _currentChar == '_') {
			takeIt();
		}
		String text = _currentText.toString();
		TokenType type = getKeywordTokenType(text);
		return type != null ? makeToken(type) : makeToken(TokenType.IDENTIFIER);
	}

	private Token scanNumber() {
		while (Character.isDigit(_currentChar)) {
			takeIt();
		}
		return makeToken(TokenType.INTEGER_LITERAL);
	}

	private Token scanOperator() {
		char firstChar = _currentChar;
		takeIt();
		switch (firstChar) {
			case '&':
				if (_currentChar == '&')
					takeIt();
				break;
			case '|':
				if (_currentChar == '|')
					takeIt();
				break;
			case '=':
				if (_currentChar == '=')
					takeIt();
				break;
			case '!':
				if (_currentChar == '=')
					takeIt();
				break;
			case '<':
				if (_currentChar == '=')
					takeIt();
				break;
			case '>':
				if (_currentChar == '=')
					takeIt();
				break;
			case '+':
			case '-':
			case '*':
			case '/':
			case '(':
			case ')':
			case '{':
			case '}':
			case '[':
			case ']':
			case ';':
			case ',':
			case '.':
				break;
			default:
				_errors.reportError("Unknown operator: ", String.valueOf(firstChar));
				return makeToken(TokenType.ERROR);
		}
		String op = _currentText.toString();
		TokenType type = getOperatorTokenType(op);
		return type != null ? makeToken(type) : makeToken(TokenType.ERROR);
	}

	private void skipSingleLineComment() {
		while (_currentChar != '\n' && _currentChar != (char) -1) {
			skipIt();
		}
	}

	private void skipMultiLineComment() {
		boolean endFound = false;
		while (!endFound && _currentChar != (char) -1) {
			if (_currentChar == '*') {
				skipIt();
				if (_currentChar == '/') {
					endFound = true;
					skipIt();
				}
			} else {
				skipIt();
			}
		}
		if (!endFound) {
			_errors.reportError("Unclosed multiline comment");
		}
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
			_currentChar = (char) c;

			if (c == -1) {
				_currentChar = (char) -1;
			} else if (c > 127) {
				_errors.reportError("Invalid character: ", String.valueOf((char) c));
				nextChar();
			} else {
				if (_currentChar == '\n') {
					_line++;
					_column = 0;
				} else {
					_column++;
				}
			}
		} catch (IOException e) {
			_errors.reportError("I/O error: ", e.getMessage());
			_currentChar = (char) -1;
		}
	}

	private Token makeToken(TokenType toktype) {
		return new Token(toktype, _currentText.toString(), _line, _column - _currentText.length());
	}

	private TokenType getKeywordTokenType(String text) {
		switch (text) {
			case "class":
				return TokenType.CLASS;
			case "public":
				return TokenType.PUBLIC;
			case "private":
				return TokenType.PRIVATE;
			case "static":
				return TokenType.STATIC;
			case "void":
				return TokenType.VOID;
			case "int":
				return TokenType.INT;
			case "boolean":
				return TokenType.BOOLEAN;
			case "if":
				return TokenType.IF;
			case "else":
				return TokenType.ELSE;
			case "while":
				return TokenType.WHILE;
			case "return":
				return TokenType.RETURN;
			case "true":
				return TokenType.TRUE;
			case "false":
				return TokenType.FALSE;
			case "this":
				return TokenType.THIS;
			case "new":
				return TokenType.NEW;
			default:
				return null;
		}
	}

	private TokenType getOperatorTokenType(String op) {
		switch (op) {
			case "(":
				return TokenType.LPAREN;
			case ")":
				return TokenType.RPAREN;
			case "{":
				return TokenType.LCURLY;
			case "}":
				return TokenType.RCURLY;
			case "[":
				return TokenType.LSQUARE;
			case "]":
				return TokenType.RSQUARE;
			case ";":
				return TokenType.SEMICOLON;
			case ",":
				return TokenType.COMMA;
			case ".":
				return TokenType.DOT;
			case "=":
				return TokenType.EQUALS;
			case "+":
			case "-":
			case "*":
			case "/":
			case "&&":
			case "||":
			case "!":
			case "<":
			case ">":
			case "<=":
			case ">=":
			case "==":
			case "!=":
				return TokenType.OPERATOR;
			default:
				return null;
		}
	}
}
