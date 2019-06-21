package org.sirius.frontend.ast;

import java.util.Optional;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.TokenLocation;

public class AstToken implements org.sirius.common.core.Token {

	private int charPositionInLine;
	private int line;
	private int startIndex;
	private int stopIndex;
	private String text;
	private String sourceName;
	private Optional<TokenLocation> tokenLocation;
	

	public AstToken(int charPositionInLine, int line, int startIndex, int stopIndex, String text, String sourceName) {
		super();
		this.charPositionInLine = charPositionInLine;
		this.line = line;
		this.startIndex = startIndex;
		this.stopIndex = stopIndex;
		this.text = text;
		this.sourceName = sourceName;
		this.tokenLocation = Optional.of(new TokenLocation() {
			
			@Override
			public int getStopIndex() {
				return stopIndex;
			}
			
			@Override
			public int getStartIndex() {
				return startIndex;
			}
			
			@Override
			public String getSourceName() {
				return sourceName;
			}
			
			@Override
			public int getLine() {
				return line;
			}
			
			@Override
			public int getCharPositionInLine() {
				return charPositionInLine;
			}
		});
	}

	public AstToken(Token token) {
		this(token.getCharPositionInLine(), token.getLine(), token.getStartIndex(), token.getStopIndex(), token.getText(), token.getTokenSource().getSourceName());
	}

	public static AstToken internal(String text, String sourceName) {
		return new AstToken(0, 0, 0, 0, text, sourceName);
	}
	public static AstToken internal(String text) {
		return new AstToken(0, 0, 0, 0, text, "<internal>");
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

	public String getSourceName() {
		return sourceName;
	}
	
	@Override
	public String toString() {
		return text;
	}

	@Override
	public Optional<TokenLocation> getTokenLocation() {
		return tokenLocation;
	}
	
	public org.sirius.common.core.Token asToken() {
		return new org.sirius.common.core.Token() {
			
			@Override
			public Optional<TokenLocation> getTokenLocation() {
				return tokenLocation;
			}
			
			@Override
			public String getText() {
				return text;
			}
		};
	}
}
