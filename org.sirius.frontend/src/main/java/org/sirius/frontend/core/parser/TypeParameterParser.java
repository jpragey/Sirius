package org.sirius.frontend.core.parser;

import java.util.Optional;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class TypeParameterParser {
	private Reporter reporter;
	
	public TypeParameterParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class TypeParameterVisitor extends SiriusBaseVisitor<TypeParameter> {
		private Reporter reporter;

		public TypeParameterVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public TypeParameter visitTypeParameterDeclaration(TypeParameterDeclarationContext ctx) {
			Variance variance;
			
			ParseTree varianceTree = ctx.children.get(0);
			String varianceString = varianceTree.getText();
//			System.out.println(varianceString);

			if(ctx.IN() != null) {
				variance = Variance.IN;
			}
			else if(ctx.OUT() != null) {
				variance = Variance.OUT;
			} else {
				variance = Variance.INVARIANT;
			}
			
			Optional<AstType> defaultType = Optional.empty();
			
			TypeContext defaultTypeContext =  ctx.type();
			if(defaultTypeContext != null) {	// Thre's a default value
				TypeParser.TypeVisitor visitor = new TypeParser.TypeVisitor(reporter);
				defaultType = Optional.of(defaultTypeContext.accept(visitor));
			}
			
			AstToken name = new AstToken(ctx.TYPE_ID().getSymbol());

			return new TypeParameter(variance, name, defaultType);
		}
	}

}
