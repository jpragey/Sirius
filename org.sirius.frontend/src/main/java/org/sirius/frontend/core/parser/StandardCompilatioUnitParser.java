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
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ConcreteModuleContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.PackageDescriptorCompilationUnitContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;
import org.sirius.frontend.parser.SiriusParser.ScriptCompilationUnitContext;
import org.sirius.frontend.parser.SiriusParser.StandardCompilationUnitContext;
import org.sirius.frontend.symbols.DefaultSymbolTable;

/** Visitor-based parser for the 'scriptCompilationUnit' rule.
 * 
 * @author jpragey
 *
 */
public class StandardCompilatioUnitParser {

	public static class StandardCompilationUnitVisitor extends SiriusBaseVisitor<StandardCompilationUnit> {
		private Reporter reporter;

		public StandardCompilationUnitVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public StandardCompilationUnit visitStandardCompilationUnit(StandardCompilationUnitContext ctx) {
			DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable("");
			StandardCompilationUnit unit = new StandardCompilationUnit(reporter, globalSymbolTable);
			return unit;
		}
	}
}
