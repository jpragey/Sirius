package org.sirius.frontend.core.parser;

import java.util.List;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.Sirius.ModuleDescriptorCompilationUnitContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ModuleDescriptorCompilatioUnitParser {

	public static class PackageDescriptorCompilationUnitVisitor extends SiriusBaseVisitor<AstModuleDeclaration> {
		private Reporter reporter;
		private CommonTokenStream tokens;

		public PackageDescriptorCompilationUnitVisitor(Reporter reporter, CommonTokenStream tokens) {
			super();
			this.reporter = reporter;
			this.tokens = tokens;
		}
		@Override
		public AstModuleDeclaration visitModuleDescriptorCompilationUnit(ModuleDescriptorCompilationUnitContext ctx) {
			
			ModuleDeclarationParser.ModuleDeclarationVisitor visitor = new ModuleDeclarationParser.ModuleDeclarationVisitor(reporter, tokens);
			AstModuleDeclaration moduleDeclaration = visitor.visit(ctx.moduleDeclaration()).build(List.of() /*AstPackageDeclaration*/ );
			
			return moduleDeclaration;
		}
	}
}
