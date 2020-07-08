package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ConcreteModuleContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
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
			QNameParser.QNameVisitor nameVisitor = new QNameParser.QNameVisitor();
			Optional<QName> qname = Optional.empty();
			if(ctx.nameQName != null )
				qname = Optional.of(ctx.nameQName.accept(nameVisitor).toQName());
//			if(ctx.qname != null) {
//				QualifiedName qualifiedName = ctx.qname.accept(nameVisitor);
//				qname = Optional.of(qualifiedName.toQName());
//			}
				
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
			AstToken key = new AstToken(ctx.key);
			AstToken val = new AstToken(ctx.value);
			
			equivalents.put(key, val);
			return null;
		}
	}	
	
	public static class ModuleContentVisitor extends SiriusBaseVisitor<Void> {
		Reporter reporter; 
		List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
		List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
		List<AstClassDeclaration> classDeclarations = new ArrayList<>();
		List<PartialList> partialLists = new ArrayList<>();
		
		public ModuleContentVisitor(Reporter reporter,
				List<AstPackageDeclaration> packageDeclarations,
				List<AstInterfaceDeclaration> interfaceDeclarations, List<AstClassDeclaration> classDeclarations,
				List<PartialList> partialLists) {
			super();
			this.reporter = reporter;
			this.packageDeclarations = packageDeclarations;
			this.interfaceDeclarations = interfaceDeclarations;
			this.classDeclarations = classDeclarations;
			this.partialLists = partialLists;
		}
			@Override
		public Void visitPackageDeclaration(PackageDeclarationContext ctx) {
				PackageDeclarationParser.PackageDeclarationVisitor v = new PackageDeclarationParser.PackageDeclarationVisitor(reporter);
				AstPackageDeclaration partialList = ctx.accept(v);
				this.packageDeclarations.add(partialList);
				return null;
		}
		@Override
		public Void visitFunctionDeclaration(FunctionDeclarationContext ctx) {
			FunctionDeclarationParser.FunctionDeclarationVisitor v = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter);
			
			PartialList partialList = ctx.accept(v);
			this.partialLists.add(partialList);
			return null;
		}
		@Override
		public Void visitClassDeclaration(ClassDeclarationContext ctx) {
			ClassDeclarationParser.ClassDeclarationVisitor visitor = new ClassDeclarationParser.ClassDeclarationVisitor (reporter/*, new QName()*/ /* containerQName */);

			AstClassDeclaration cd = ctx.accept(visitor);
			this.classDeclarations.add(cd);
			return null;
		}
		@Override
		public Void visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
			InterfaceDeclarationParser.InterfaceDeclarationVisitor visitor = new InterfaceDeclarationParser.InterfaceDeclarationVisitor(reporter);

			AstInterfaceDeclaration id = ctx.accept(visitor);
			this.interfaceDeclarations.add(id);
			return null;
		}
	}
	public static class ConcreteModuleVisitor extends SiriusBaseVisitor<AstModuleDeclaration> {
		private Reporter reporter;

		public ConcreteModuleVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		@Override
		public AstModuleDeclaration visitConcreteModule(ConcreteModuleContext ctx) {
			// -- concrete module
			List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
			List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
			List<AstClassDeclaration> classDeclarations = new ArrayList<>();
			List<PartialList> partialLists = new ArrayList<>();
			ModuleContentVisitor moduleContentVisitor = new ModuleContentVisitor(reporter, packageDeclarations, interfaceDeclarations, classDeclarations, partialLists);
			
			ctx.moduleContent().forEach(mcContext -> mcContext.accept(moduleContentVisitor));
			
			// --
			ModuleDeclarationVisitor mdVisitor = new ModuleDeclarationVisitor(reporter);
			AstModuleDeclaration result;
			ModuleDeclarationContext moduleDeclarationContext = ctx.moduleDeclaration();
			if(moduleDeclarationContext != null) {
				result = moduleDeclarationContext.accept(mdVisitor);
			} else {
				ModuleImportEquivalents equiv = new ModuleImportEquivalents();
				List<ModuleImport> moduleImports = List.of();
				result = AstModuleDeclaration.createUnnamed(reporter, equiv, moduleImports);
			}
			
			packageDeclarations.forEach(pkdDecl->result.addPackageDeclaration(pkdDecl));
			interfaceDeclarations.forEach(pkdDecl->result.addInterfaceDeclaration(pkdDecl));
			classDeclarations.forEach(pkdDecl->result.addClassDeclaration(pkdDecl));
			partialLists.forEach(pkdDecl->result.addFunctionDeclaration(pkdDecl));
					
			return result;
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
			QName qualifiedName = ctx.qname().accept(nameVisitor);
			
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
