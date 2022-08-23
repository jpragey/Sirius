package org.sirius.frontend.core.parser;

import java.util.Optional;

import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.Sirius.TypeContext;
import org.sirius.frontend.parser.Sirius.TypeParameterDeclarationContext;

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

	public class TypeParameterVisitor extends SiriusBaseVisitor<TypeParameter> {

		public TypeParameterVisitor() {
			super();
		}

		@Override
		public TypeParameter visitTypeParameterDeclaration(TypeParameterDeclarationContext ctx) {
			Variance variance;
			
			if(ctx.IN() != null) {
				variance = Variance.IN;
			}
			else if(ctx.OUT() != null) {
				variance = Variance.OUT;
			} else {
				variance = Variance.INVARIANT;
			}
			
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			TypeContext defaultTypeContext =  ctx.type();
			Optional<AstType> defaultType = Optional.ofNullable(defaultTypeContext)
					.map(typeVisitor::visit);
			
			AstToken name = new AstToken(ctx.TYPE_ID().getSymbol());

			return new TypeParameter(variance, name, defaultType);
		}
	}

}
