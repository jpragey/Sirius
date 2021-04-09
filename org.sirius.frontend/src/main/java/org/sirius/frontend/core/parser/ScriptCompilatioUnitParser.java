package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ScriptCompilationUnitContext;
import org.sirius.frontend.symbols.Scope;

/** Visitor-based parser for the 'scriptCompilationUnit' rule.
 * 
 * @author jpragey
 *
 */
public class ScriptCompilatioUnitParser {

	public static class ScriptCompilationUnitVisitor extends SiriusBaseVisitor<ScriptCompilationUnit> {
		private Reporter reporter;
		private Scope globalScope;

		public ScriptCompilationUnitVisitor(Reporter reporter, Scope sdkScope) {
			super();
			this.reporter = reporter;
			this.globalScope = new Scope(Optional.of(sdkScope), "ScriptCU");
		}

		@Override
		public ScriptCompilationUnit visitScriptCompilationUnit(ScriptCompilationUnitContext ctx) {
			
			// -- Shebang
			ShebangDeclarationParser.ShebangVisitor shebangVisitor = new ShebangDeclarationParser.ShebangVisitor();
			Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();
			if(ctx.shebangDeclaration() != null)
				shebangDeclaration = Optional.of(ctx.shebangDeclaration().accept(shebangVisitor));
			
			// -- Import declarations
			ImportDeclarationParser.ImportDeclarationVisitor importVisitor = new ImportDeclarationParser.ImportDeclarationVisitor(reporter);
			List<ImportDeclaration> imports = ctx.importDeclaration().stream()
					.map(importDeclCtx -> importDeclCtx.accept(importVisitor))
					.collect(Collectors.toList());
			
			// -- module declarations
			ModuleDeclarationParser.ConcreteModuleVisitor moduleVisitor = new ModuleDeclarationParser.ConcreteModuleVisitor(reporter);
			List<AstModuleDeclaration> modules = ctx.concreteModule().stream()
					.map(mCtx -> mCtx.accept(moduleVisitor))
					.filter(md -> md!=null)
					.collect(Collectors.toList());
			
			return new ScriptCompilationUnit(reporter, globalScope, shebangDeclaration, imports, modules);
		}
	}
}
