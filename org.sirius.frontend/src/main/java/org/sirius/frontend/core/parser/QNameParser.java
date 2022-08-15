package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ImportDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ImportDeclarationElementContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class QNameParser {

	
//	public static class QNameVisitor extends SiriusBaseVisitor<QualifiedName> {
//		public QualifiedName visitQname(QnameContext ctx) 
//		{
//			List<AstToken> elements = ctx.LOWER_ID().stream()
//					.map(termNode -> new AstToken(termNode.getSymbol()))
//					.collect(Collectors.toList());
//			
//			QualifiedName qName = new QualifiedName(elements);
//			return qName;
//		};
//	}
}
