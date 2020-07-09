package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ConcreteModuleContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.PackageDescriptorCompilationUnitContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;
import org.sirius.frontend.parser.SiriusParser.ScriptCompilationUnitContext;
import org.sirius.frontend.symbols.DefaultSymbolTable;

/** Visitor-based parser for the 'scriptCompilationUnit' rule.
 * 
 * @author jpragey
 *
 */
public class ScriptCompilatioUnitParser {

	public static class ScriptCompilationUnitVisitor extends SiriusBaseVisitor<ScriptCompilationUnit> {
		private Reporter reporter;
		private DefaultSymbolTable globalSymbolTable;

		public ScriptCompilationUnitVisitor(Reporter reporter, DefaultSymbolTable globalSymbolTable) {
			super();
			this.reporter = reporter;
			this.globalSymbolTable = globalSymbolTable;
		}

		@Override
		public ScriptCompilationUnit visitScriptCompilationUnit(ScriptCompilationUnitContext ctx) {
			
			// -- Shebang
			ShebangDeclarationParser.ShebangVisitor shebangVisitor = new ShebangDeclarationParser.ShebangVisitor();
			Optional<ShebangDeclaration> shebangDeclaration = Optional.empty();
			//if(ctx.shebangDeclaration() != null)
				shebangDeclaration = Optional.of(ctx.shebangDeclaration().accept(shebangVisitor));
			
			// -- Import declarations
		    //( importDeclaration 			{$unit.addImport($importDeclaration.declaration);  })*
			ImportDeclarationParser.ImportDeclarationVisitor importVisitor = new ImportDeclarationParser.ImportDeclarationVisitor(reporter);
			List<ImportDeclaration> imports = ctx.importDeclaration().stream()
					.map(importDeclCtx -> importDeclCtx.accept(importVisitor))
					.collect(Collectors.toList());
			
			// -- module declarations
			ModuleDeclarationParser.ConcreteModuleVisitor moduleVisitor = new ModuleDeclarationParser.ConcreteModuleVisitor(reporter);
//			ModuleDeclarationParser.ModuleDeclarationVisitor moduleVisitor = new ModuleDeclarationParser.ModuleDeclarationVisitor(reporter);
//			List<ConcreteModuleContext> cmodules = ctx.concreteModule();
			List<AstModuleDeclaration> modules = ctx.concreteModule().stream()
					.map(mCtx -> mCtx.accept(moduleVisitor))
					.filter(md -> md!=null)
					.collect(Collectors.toList());
			
			
//			DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable("root");	// TODO

			return new ScriptCompilationUnit(reporter, globalSymbolTable, shebangDeclaration, imports, /*packages, */modules);
		}
	}
}
