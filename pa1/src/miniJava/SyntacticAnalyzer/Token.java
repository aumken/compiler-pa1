package miniJava.SyntacticAnalyzer;

public class Token {
	private TokenType _type;
	private String _text;
	
	public Token(TokenType type, String text) {
		// DONE: Store the token's type and text
		this._type = type;
		this._text = text;
	}
	
	public TokenType getTokenType() {
		// DONE: Return the token type
		return this._type;
	}
	
	public String getTokenText() {
		// DONE: Return the token text
		return this._text;
	}
}
