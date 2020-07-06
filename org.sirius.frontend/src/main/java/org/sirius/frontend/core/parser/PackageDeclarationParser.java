package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class PackageDeclarationParser {

	public static class QNameVisitor extends SiriusBaseVisitor<QName> {	// TODO: should have its own namespace
		public QName visitQname(QnameContext ctx) 
		{
			List<String> elements = ctx.LOWER_ID().stream()
					.map(termNode -> termNode.getSymbol().getText())
					.collect(Collectors.toList());
			
			QName qName = new QName(elements);
			return qName;
		};
	}
	
	
	public static class PackageDeclarationVisitor extends SiriusBaseVisitor<AstPackageDeclaration> {
		private Reporter reporter;

		public PackageDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstPackageDeclaration visitPackageDeclaration(PackageDeclarationContext ctx) {
			
			QNameVisitor visitor = new QNameVisitor();
			QName packageQName = ctx.qname().accept(visitor);
			
			return new AstPackageDeclaration(reporter, packageQName);
		}
	}
}
