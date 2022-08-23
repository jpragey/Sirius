package org.sirius.frontend.core.parser;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.Sirius.StandardCompilationUnitContext;
import org.sirius.frontend.symbols.Scope;

/** Visitor-based parser for the 'scriptCompilationUnit' rule.
 * 
 * @author jpragey
 *
 */
public class StandardCompilatioUnitParser {

	public static class StandardCompilationUnitVisitor extends SiriusBaseVisitor<StandardCompilationUnit> {
		private Reporter reporter;
		private Scope globalScope;

		public StandardCompilationUnitVisitor(Reporter reporter, Scope globalScope) {
			super();
			this.reporter = reporter;
			this.globalScope = globalScope;
		}

		@Override
		public StandardCompilationUnit visitStandardCompilationUnit(StandardCompilationUnitContext ctx) {
			StandardCompilationUnit unit = new StandardCompilationUnit(reporter, globalScope);
			return unit;
		}
	}
}
