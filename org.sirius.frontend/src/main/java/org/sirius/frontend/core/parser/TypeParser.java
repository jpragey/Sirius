package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstArrayType;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.IntersectionType;
import org.sirius.frontend.ast.LambdaDeclaration;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.UnionType;
import org.sirius.frontend.parser.SParserBaseVisitor;
import org.sirius.frontend.parser.SParser.ArrayTypeContext;
import org.sirius.frontend.parser.SParser.BracketedTypeContext;
import org.sirius.frontend.parser.SParser.IntersectionTypeContext;
import org.sirius.frontend.parser.SParser.LambdaDeclarationContext;
import org.sirius.frontend.parser.SParser.SimpleType0Context;
import org.sirius.frontend.parser.SParser.UnionTypeContext;

/** Visitor-based parser for the 'type' rule.
 * 
 * @author jpragey
 *
 */
public class TypeParser {
	private Reporter reporter;
	
	public TypeParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}



	public static class TypeVisitor extends SParserBaseVisitor<AstType> {
		private Reporter reporter;

		public TypeVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstType visitUnionType(UnionTypeContext ctx) {
			AstType leftType = visit(ctx.first);
			AstType rightType = visit(ctx.second);
			return new UnionType(leftType, rightType);
		}

		@Override
		public AstType visitIntersectionType(IntersectionTypeContext ctx) {
			AstType leftType = visit(ctx.first);
			AstType rightType = visit(ctx.second);
			return new IntersectionType(leftType, rightType);
		}

		@Override
		public AstType visitArrayType(ArrayTypeContext ctx) {
			return new AstArrayType(visit(ctx.type()));
		}

		@Override
		public AstType visitBracketedType(BracketedTypeContext ctx) {
			return visit(ctx.type());
		}

		@Override
		public AstType visitSimpleType0(SimpleType0Context ctx) {
			assert(ctx.TYPE_ID() != null);
			AstToken name = new AstToken(ctx.TYPE_ID().getSymbol());
			
			List<AstType> typeParams = ctx.children.stream()
					.map(this::visit)
				.filter(Objects::nonNull)
				.collect(Collectors.toUnmodifiableList())
				;
			
			
			AstType myType = new SimpleType(reporter, name, List.copyOf(typeParams));
			return myType;
		}
		
		@Override
		public AstType visitLambdaDeclaration(LambdaDeclarationContext ctx) {
			LambdaDeclarationParser.LambdaDeclarationVisitor declVisitor = new LambdaDeclarationParser.LambdaDeclarationVisitor(reporter);
			LambdaDeclaration lambdaDeclaration = declVisitor.visit(ctx);
			return lambdaDeclaration;
		}
	}

}
