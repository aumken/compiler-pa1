package miniJava.SyntacticAnalyzer;

public class Token {
	private TokenType _type;
	private String _text;
	private int _line;
	private int _column;

	public Token(TokenType type, String text, int line, int column) {
		this._type = type;
		this._text = text;
		this._line = line;
		this._column = column;
	}

	public TokenType getTokenType() {
		return _type;
	}

	public String getTokenText() {
		return _text;
	}

	public int getLine() {
		return _line;
	}

	public int getColumn() {
		return _column;
	}
}