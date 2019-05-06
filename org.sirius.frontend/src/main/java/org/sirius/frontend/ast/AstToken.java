package org.sirius.frontend.ast;

import org.antlr.v4.runtime.Token;

public class AstToken implements org.sirius.frontend.api.Token, org.sirius.common.error.Token {

	private int charPositionInLine;
	private int line;
	private int startIndex;
	private int stopIndex;
	private String text;
	private String sourceName;
	

	public AstToken(int charPositionInLine, int line, int startIndex, int stopIndex, String text, String sourceName) {
		super();
		this.charPositionInLine = charPositionInLine;
		this.line = line;
		this.startIndex = startIndex;
		this.stopIndex = stopIndex;
		this.text = text;
		this.sourceName = sourceName;
	}

	public AstToken(Token token) {
		this(token.getCharPositionInLine(), token.getLine(), token.getStartIndex(), token.getStopIndex(), token.getText(), token.getTokenSource().getSourceName());
	}

	public int getCharPositionInLine() {
		return charPositionInLine;
	}
	
	public int getLine()		{
		return line;
	}
	public int getStartIndex()	{
		return startIndex;
	}
	public int getStopIndex()	{
		return stopIndex;
	}
	public String getText()	{
		return text;
	}

	@Override
	public String getSourceName() {
		return sourceName;
	}
	
	@Override
	public String toString() {
		return text;
	}
}
