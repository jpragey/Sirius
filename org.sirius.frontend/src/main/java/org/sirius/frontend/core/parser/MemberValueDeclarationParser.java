package org.sirius.frontend.core.parser;

import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ExpressionContext;
import org.sirius.frontend.parser.SiriusParser.MemberValueDeclarationContext;

public class MemberValueDeclarationParser {

	public static class MemberValueVisitor extends SiriusBaseVisitor<AstMemberValueDeclaration> {
		private Reporter reporter;

		public MemberValueVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstMemberValueDeclaration visitMemberValueDeclaration(MemberValueDeclarationContext ctx) {
			// TODO: annotationList
			AnnotationList annotations = new AnnotationList(); // TODO
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType type = ctx.type().accept(typeVisitor);
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());
			
			ExpressionParser.ExpressionVisitor expressionVisitor = new ExpressionParser.ExpressionVisitor(reporter);
			ExpressionContext initValueContext = ctx.expression();
			Optional<AstExpression> initialValue = (initValueContext == null) ?  
					Optional.empty() : Optional.of(initValueContext.accept(expressionVisitor));

			return new AstMemberValueDeclaration(annotations, type, name, initialValue);
		}
		
		
	}
}
