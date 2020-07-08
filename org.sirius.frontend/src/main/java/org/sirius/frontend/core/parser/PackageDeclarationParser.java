package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.core.parser.ModuleDeclarationParser.ModuleContentVisitor;
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
			
			List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
			List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
			List<AstClassDeclaration> classDeclarations = new ArrayList<>();
			List<PartialList> partialLists = new ArrayList<>();

			ModuleDeclarationParser.ModuleContentVisitor mcVisitor = new ModuleDeclarationParser.ModuleContentVisitor(reporter, 
					packageDeclarations, interfaceDeclarations, classDeclarations, partialLists);
			ctx.moduleContent().forEach(mcContext -> mcContext.accept(mcVisitor));
			
			
			return new AstPackageDeclaration(reporter, packageQName,
					partialLists, 
					classDeclarations, 
					interfaceDeclarations, 
					List.of()// <AstMemberValueDeclaration> valueDeclarations
					);
		}
	}
}
