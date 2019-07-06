package org.sirius.frontend.ast;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IntegerConstantExpression;

public class AstIntegerConstantExpression implements AstExpression {
	
	private AstToken content;
	private int value = 0;

	public AstIntegerConstantExpression(AstToken content, Reporter reporter) {
		super();
		this.content = content;
		String text = content.getText();
		try {
			this.value = Integer.parseInt(text);
		} catch(NumberFormatException e) {
			reporter.error("Value '" + text + "' is not a valid integer.", content, e);
		}
	}
	
	
	public AstToken getContentToken() {
		return content;
	}
	
	@Override
	public void visit(AstVisitor visitor) {
		visitor.startIntegerConstant(this);
		visitor.endIntegerConstant(this);
	}
	@Override
	public Optional<AstType> getType() {
		return Optional.empty();
	}


	@Override
	public Expression getExpression() {
		return new IntegerConstantExpression() {

			@Override
			public int getValue() {
				return value;
			}
			
		} ;
	}
}
