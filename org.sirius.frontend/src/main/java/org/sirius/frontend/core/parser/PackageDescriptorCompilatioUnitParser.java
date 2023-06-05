package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.parser.SParserBaseVisitor;
import org.sirius.frontend.parser.SParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SParser.PackageDescriptorCompilationUnitContext;
import org.sirius.frontend.parser.SParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class PackageDescriptorCompilatioUnitParser {

	
	
	public static class PackageDescriptorCompilationUnitVisitor extends SParserBaseVisitor<PackageDescriptorCompilationUnit> {
		private Reporter reporter;
		private CommonTokenStream tokens;

		public PackageDescriptorCompilationUnitVisitor(Reporter reporter, CommonTokenStream tokens) {
			super();
			this.reporter = reporter;
			this.tokens = tokens;
		}

		@Override
		public PackageDescriptorCompilationUnit visitPackageDescriptorCompilationUnit(
				PackageDescriptorCompilationUnitContext ctx) {

			Parsers.PackageDeclarationVisitor visitor = new Parsers(reporter, tokens).new PackageDeclarationVisitor();
			AstPackageDeclaration pd = visitor.visit(ctx.packageDeclaration());
	
			return new PackageDescriptorCompilationUnit(reporter, pd);
		}
		
	}
}
