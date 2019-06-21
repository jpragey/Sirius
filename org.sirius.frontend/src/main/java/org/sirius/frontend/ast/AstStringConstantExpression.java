package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.frontend.api.StringConstantExpression;

public class AstStringConstantExpression implements AstExpression {
	
	private AstToken contentToken;
	private String contentString;
	
	public AstStringConstantExpression(AstToken content) {
		super();
		this.contentToken = content;
		String text = content.getText();
		this.contentString = text.substring(1, text.length()-1);
	}
	
	
	public AstToken getContentToken() {
		return contentToken;
	}
	
	public String getContentString() {
		return contentString;
	}


	@Override
	public void visit(AstVisitor visitor) {
		visitor.startStringConstant(this);
		visitor.endStringConstant(this);
	}
	@Override
	public Optional<Type> getType() {
		return Optional.empty();
	}


	@Override
	public StringConstantExpression getExpression() {
		return new StringConstantExpression() {

			@Override
			public Token getContent() {
				return contentToken.asToken();
			}

			@Override
			public String getText() {
				return contentString;
			}
		};
	}
}
