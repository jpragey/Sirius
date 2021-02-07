package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.LinkedList;
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
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.core.parser.FunctionDeclarationParser.FunctionDefinitionVisitor;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ConcreteModuleContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDefinitionContext;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ModuleDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.ModuleImportContext;
import org.sirius.frontend.parser.SiriusParser.ModuleVersionEquivalentContext;
import org.sirius.frontend.parser.SiriusParser.PackageDeclarationContext;

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
	
	public static class PackageElementVisitor extends SiriusBaseVisitor<Void> {
		Reporter reporter; 
		List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
		PackageElements packageElements;
		
		public PackageElementVisitor(Reporter reporter,
				List<AstPackageDeclaration> packageDeclarations,
				PackageElements packageElements) 
		{
			super();
			this.reporter = reporter;
			this.packageDeclarations = packageDeclarations;
			this.packageElements = packageElements;
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
//			FunctionDeclarationParser.FunctionDeclarationVisitor v = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter);
			FunctionDefinitionVisitor v = new FunctionDefinitionVisitor(reporter);
			
			FunctionDefinition functionDefinition = ctx.accept(v);
			this.packageElements.functiondefinitions.add(functionDefinition);
			return null;
		}
		@Override
		public Void visitFunctionDefinition(FunctionDefinitionContext ctx) {
			FunctionDefinitionVisitor v = new FunctionDefinitionVisitor(reporter);
			
			FunctionDefinition functionDefinition = ctx.accept(v);
			this.packageElements.functiondefinitions.add(functionDefinition);
			return null;
		}
		
		@Override
		public Void visitClassDeclaration(ClassDeclarationContext ctx) {
			ClassDeclarationParser.ClassDeclarationVisitor visitor = new ClassDeclarationParser.ClassDeclarationVisitor (reporter/*, new QName()*/ /* containerQName */);

			AstClassDeclaration cd = ctx.accept(visitor);
			this.packageElements.classDeclarations.add(cd);
			return null;
		}
		@Override
		public Void visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
			InterfaceDeclarationParser.InterfaceDeclarationVisitor visitor = new InterfaceDeclarationParser.InterfaceDeclarationVisitor(reporter);

			AstInterfaceDeclaration id = ctx.accept(visitor);
			this.packageElements.interfaceDeclarations.add(id);
			return null;
		}
	}
	
	public static class PackageElements {
		public List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
		public List<AstClassDeclaration> classDeclarations = new ArrayList<>();
		public List<FunctionDefinition> functiondefinitions = new ArrayList<>();
		public boolean isEmpty() {
			return interfaceDeclarations.isEmpty() && 
					classDeclarations.isEmpty() &&
					functiondefinitions.isEmpty();
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
			PackageElements packageElements = new PackageElements();
			
			// -- package content
			LinkedList<AstPackageDeclaration> packageDeclarations = new LinkedList<>();
			PackageElementVisitor moduleContentVisitor = new PackageElementVisitor(reporter, packageDeclarations, packageElements);
			
			ctx.packageElement().forEach(mcContext -> mcContext.accept(moduleContentVisitor));
			
			
			// -- Module declaration
			AstModuleDeclaration result;
			ModuleDeclarationContext moduleDeclarationContext = ctx.moduleDeclaration();
			if(moduleDeclarationContext != null) {	// Explicit module
				
				AstPackageDeclaration pd = new AstPackageDeclaration(reporter, QName.empty, packageElements.functiondefinitions, packageElements.classDeclarations, 
						packageElements.interfaceDeclarations, List.of() /*valueDeclarations*/);
				ModuleDeclarationVisitor mdVisitor = new ModuleDeclarationVisitor(reporter, List.of(pd)/*packageElements*/);
				result = moduleDeclarationContext.accept(mdVisitor);
			} else {								// Unnamed module
				ModuleImportEquivalents equiv = new ModuleImportEquivalents();
				List<ModuleImport> moduleImports = List.of();
				
				if(!packageElements.isEmpty()) { // package elements before first package declaration => prepend unnamed package
					AstPackageDeclaration unnamedPackage = new AstPackageDeclaration(reporter, QName.empty, 
							packageElements.functiondefinitions, packageElements.classDeclarations, packageElements.interfaceDeclarations, List.of() /*valueDeclarations*/);
					
					packageDeclarations.addFirst(unnamedPackage);
				}
				
				result = AstModuleDeclaration.createUnnamed(reporter, equiv, moduleImports, packageDeclarations);
			}
					
			return result;
		}
	}
	public static class ModuleDeclarationVisitor extends SiriusBaseVisitor<AstModuleDeclaration> {
		private Reporter reporter;
		private List<AstPackageDeclaration> packageDeclarations;

		public ModuleDeclarationVisitor(Reporter reporter, /*PackageElements packageElements*/List<AstPackageDeclaration> packageDeclarations) {
			super();
			this.reporter = reporter;
			this.packageDeclarations  =packageDeclarations;
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
			
			return new AstModuleDeclaration(reporter, qualifiedName, version, equivalents, moduleImports, packageDeclarations);
		}
	}
}
