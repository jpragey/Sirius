package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.PackageDescriptorCompilationUnitContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class PackageDescriptorCompilatioUnitParser {

	
	
	public static class PackageDescriptorCompilationUnitVisitor extends SiriusBaseVisitor<PackageDescriptorCompilationUnit> {
		private Reporter reporter;

		public PackageDescriptorCompilationUnitVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public PackageDescriptorCompilationUnit visitPackageDescriptorCompilationUnit(
				PackageDescriptorCompilationUnitContext ctx) {

			Parsers.PackageDeclarationVisitor visitor = new Parsers(reporter).new PackageDeclarationVisitor();
			AstPackageDeclaration pd = visitor.visit(ctx.packageDeclaration());
	
			return new PackageDescriptorCompilationUnit(reporter, pd);
		}
		
	}
}
