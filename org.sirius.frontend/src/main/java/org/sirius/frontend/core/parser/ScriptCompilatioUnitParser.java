package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SParserBaseVisitor;
import org.sirius.frontend.parser.SParser.ScriptCompilationUnitContext;
import org.sirius.frontend.symbols.Scope;

/** Visitor-based parser for the 'scriptCompilationUnit' rule.
 * 
 * @author jpragey
 *
 */
public class ScriptCompilatioUnitParser {

	public static class ScriptCompilationUnitVisitor extends SParserBaseVisitor<ScriptCompilationUnit> {
		private Reporter reporter;
		private Scope globalScope;
		private CommonTokenStream tokens;

		public ScriptCompilationUnitVisitor(Reporter reporter, Scope sdkScope, CommonTokenStream tokens) {
			super();
			this.reporter = reporter;
			this.tokens = tokens;
			this.globalScope = new Scope(Optional.of(sdkScope), "ScriptCU");
		}

		@Override
		public ScriptCompilationUnit visitScriptCompilationUnit(ScriptCompilationUnitContext ctx) {
			
			// -- Shebang
			ShebangDeclarationParser.ShebangVisitor shebangVisitor = new ShebangDeclarationParser.ShebangVisitor();

			Optional<ShebangDeclaration> shebangDeclaration = Optional.ofNullable(ctx.shebangDeclaration())
					.map(shebangVisitor::visit);
			
			// -- Import declarations
			ImportDeclarationParser.ImportDeclarationVisitor importVisitor = new ImportDeclarationParser(tokens).new ImportDeclarationVisitor(reporter);
			List<ImportDeclaration> imports = ctx.importDeclaration().stream()
					.map(importVisitor::visit)
					.collect(Collectors.toList());
			
			// -- module declarations
			ModuleDeclarationParser.ConcreteModuleVisitor moduleVisitor = new ModuleDeclarationParser(reporter, tokens).new ConcreteModuleVisitor();
			List<AstModuleDeclaration> modules = ctx.concreteModule().stream()
					.map(moduleVisitor::visit)
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			
			return new ScriptCompilationUnit(reporter, globalScope, shebangDeclaration, imports, modules);
		}
	}
}
