package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ModuleDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ModuleImportContext;
import org.sirius.frontend.parser.SiriusParser.ModuleVersionEquivalentContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;
import org.sirius.frontend.parser.SiriusParser.ShebangDeclarationContext;

/** Visitor-based parser for the 'shebangDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ShebangDeclarationParser {

	public static class ShebangVisitor extends SiriusBaseVisitor<ShebangDeclaration> {

		@Override
		public ShebangDeclaration visitShebangDeclaration(ShebangDeclarationContext ctx) {
			AstToken content = new AstToken(ctx.SHEBANG);
			return new ShebangDeclaration(content);
		}
	}
}
