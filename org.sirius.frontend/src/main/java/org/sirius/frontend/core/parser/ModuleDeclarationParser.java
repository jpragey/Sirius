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
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ModuleDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ModuleImportContext;
import org.sirius.frontend.parser.SiriusParser.ModuleVersionEquivalentContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ModuleDeclarationParser {

	public static class ModuleImportVisitor extends SiriusBaseVisitor<ModuleImport> {
		@Override
		public ModuleImport visitModuleImport(ModuleImportContext ctx) {
			
			// -- shared
			boolean shared = (ctx.shared != null);

			// -- origin
			Optional<AstToken> origin = (ctx.origin == null) ? 
					Optional.empty() : 
					Optional.of(new AstToken(ctx.origin));
					
			// -- qname
			Optional<QName> qname = Optional.empty();
			QNameParser.QNameVisitor nameVisitor = new QNameParser.QNameVisitor();
			if(ctx.qname != null) {
				QualifiedName qualifiedName = ctx.qname.accept(nameVisitor);
				qname = Optional.of(qualifiedName.toQName());
			}
				
			Optional<String> qnameString = Optional.empty();
			if(ctx.nameString != null) {
				String rawText = ctx.nameString.getText();
				String trimmedName = 
						rawText.substring(1, rawText.length()-1)	// remove double quotes
						.trim();
				qnameString = Optional.of(trimmedName);
			}
			// -- version
			Token versionToken = ctx.version;
			Token versionStringTk = ctx.versionString;

			
			ModuleImport moduleImport = new ModuleImport(shared, origin, qname, qnameString, versionToken, versionStringTk);
			return moduleImport;
		}
	}
	
	public static class ImportEquivalentVisitor extends SiriusBaseVisitor<Void> {
		ModuleImportEquivalents equivalents;

		public ImportEquivalentVisitor(ModuleImportEquivalents equivalents) {
			super();
			this.equivalents = equivalents;
		}
		@Override
		public Void visitModuleVersionEquivalent(ModuleVersionEquivalentContext ctx) {
			AstToken key = ctx.key;
			AstToken val = ctx.value;
			
			equivalents.put(key, val);
			return null;
		}
	}	
	public static class ModuleDeclarationVisitor extends SiriusBaseVisitor<AstModuleDeclaration> {
		private Reporter reporter;

		public ModuleDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstModuleDeclaration visitModuleDeclaration(ModuleDeclarationContext ctx) {
			
			// -- name
			PackageDeclarationParser.QNameVisitor nameVisitor = new PackageDeclarationParser.QNameVisitor();
			QName qualifiedName = ctx.qname.accept(nameVisitor);
			
			// -- version
			AstToken version = new AstToken(ctx.version);

			// -- equivalents
			ModuleImportEquivalents equivalents = new ModuleImportEquivalents(); 
			ImportEquivalentVisitor equivalentVisitor = new ImportEquivalentVisitor(equivalents);
			ctx.moduleVersionEquivalent().stream().forEach(equivCtxt ->{equivCtxt.accept(equivalentVisitor);});
			
			// -- imports
			ModuleImportVisitor moduleImportVisitor = new ModuleImportVisitor();
			List<ModuleImport> moduleImports = ctx.children.stream()
				.map(tree -> tree.accept(moduleImportVisitor))
				.filter(modImport -> modImport!=null)
				.collect(Collectors.toList());
			
			return new AstModuleDeclaration(reporter, qualifiedName, version, equivalents, moduleImports);
		}
	}
}
