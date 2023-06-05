package org.sirius.frontend.core.parser;

import java.util.Optional;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstExpression;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.parser.SParser.ExpressionContext;
import org.sirius.frontend.parser.SParser.MemberValueDeclarationContext;
import org.sirius.frontend.parser.SParserBaseVisitor;

public class MemberValueDeclarationParser {

	public static class MemberValueVisitor extends SParserBaseVisitor<AstMemberValueDeclaration> {
		private Reporter reporter;
		private ExpressionParser expressionParser;

		public MemberValueVisitor(Reporter reporter, CommonTokenStream tokens) {
			super();
			this.reporter = reporter;
			this.expressionParser = new ExpressionParser(reporter, tokens); 
		}

		@Override
		public AstMemberValueDeclaration visitMemberValueDeclaration(MemberValueDeclarationContext ctx) {
			// TODO: annotationList
//			AnnotationList annotations = new AnnotationList(); // TODO
			
			Parsers.AnnotationListVisitor annoVisitor = new Parsers.AnnotationListVisitor();
			AnnotationList annotations = annoVisitor.visit(ctx.annotationList());
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			AstType type = typeVisitor.visit(ctx.type());
			AstToken name = new AstToken(ctx.name);

			ExpressionParser.ExpressionVisitor expressionVisitor = this.expressionParser.new ExpressionVisitor(/*reporter*/);
			ExpressionContext initValueContext = ctx.expression();
			Optional<AstExpression> initialValue = (initValueContext == null) ?  
					Optional.empty() : 
					Optional.of(initValueContext.accept(expressionVisitor));

			return new AstMemberValueDeclaration(/*annotationList*/ annotations, type, name, initialValue);
		}
		
		
	}
}
