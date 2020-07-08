package org.sirius.frontend.core.parser;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.ModuleDescriptor;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ModuleDescriptorCompilationUnitContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ModuleDescriptorCompilatioUnitParser {

	public static class PackageDescriptorCompilationUnitVisitor extends SiriusBaseVisitor<AstModuleDeclaration> {
		private Reporter reporter;

		public PackageDescriptorCompilationUnitVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		@Override
		public AstModuleDeclaration visitModuleDescriptorCompilationUnit(ModuleDescriptorCompilationUnitContext ctx) {
			ModuleDeclarationParser.ModuleDeclarationVisitor visitor = new ModuleDeclarationParser.ModuleDeclarationVisitor(reporter);
			AstModuleDeclaration moduleDeclaration = ctx.moduleDeclaration().accept(visitor);
			
			return moduleDeclaration;
		}
	}
}
