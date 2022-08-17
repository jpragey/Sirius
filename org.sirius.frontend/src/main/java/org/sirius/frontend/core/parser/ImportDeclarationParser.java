package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ImportDeclarationElement;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ImportDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ImportDeclarationElementContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ImportDeclarationParser {

	
	public static class ImportDeclarationElementVisitor extends SiriusBaseVisitor<ImportDeclarationElement> {
		@Override
		public ImportDeclarationElement visitImportDeclarationElement(ImportDeclarationElementContext ctx) {
			
			Token importedTypeName = ctx.importName;
			
			Token aliasTk = ctx.alias;
			Optional<Token> alias = (aliasTk == null) ? 
							Optional.empty() : 
							Optional.of(aliasTk);

			return new ImportDeclarationElement(importedTypeName, alias);
		}
	}

	public static class ImportDeclarationVisitor extends SiriusBaseVisitor<ImportDeclaration> {
		private Reporter reporter;

		public ImportDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public ImportDeclaration visitImportDeclaration(ImportDeclarationContext ctx) {
			Parsers.QualifiedNameVisitor nameVisitor = new Parsers(reporter).new QualifiedNameVisitor();
			QualifiedName pack = nameVisitor.visit(ctx.qname());

			ImportDeclarationElementVisitor elementVisitor = new ImportDeclarationElementVisitor();
			List<ImportDeclarationElement> elements = ctx.importDeclarationElement().stream()
					.map(elemCtx -> elemCtx.accept(elementVisitor))
					.collect(Collectors.toList());
			
			
			return new ImportDeclaration(reporter, pack, elements);
		}
	}
}
