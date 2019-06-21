package org.sirius.frontend.core;

import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.ModuleDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.StandardCompilationUnitContext;
import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.frontend.symbols.SymbolResolutionVisitor;
import org.sirius.frontend.symbols.SymbolStructureVisitor;
import org.sirius.frontend.transform.CreateRootClassTransformer;

public class StandardSession implements Session {

	private Reporter reporter;

	private GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
	
	private List<ModuleContent> moduleContents = new ArrayList<>();

	
	public StandardSession(Reporter reporter, List<InputTextProvider> inputs) {
		super();
		this.reporter = reporter;
		this.addInput(inputs);
	}


	@Override
	public List<ModuleContent> getModuleContents() {
		return this.moduleContents;
	}

	private SiriusParser createParser(InputTextProvider input, AstFactory astFactory) {
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

//		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
		parser.currentModule = new AstModuleDeclaration(reporter);
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));
		
		return parser;
	}
	
	private AstModuleDeclaration parseModuleDescriptor(InputTextProvider input) {
		SiriusParser parser = createParser(input, new AstFactory(reporter, globalSymbolTable));
		ModuleDeclarationContext ctxt = parser.moduleDeclaration();
//		ModuleContent mc = new ModuleContent(reporter, ctxt.declaration);
		return ctxt.declaration;
	}
	
	private void parseInput(InputTextProvider input) {

		SiriusParser parser = createParser(input, new AstFactory(reporter, globalSymbolTable));
		// -- Parsing
		StandardCompilationUnitContext unitContext = parser.standardCompilationUnit();
		assertNotNull(unitContext);
		StandardCompilationUnit compilationUnit = unitContext.stdUnit;

//		LocalSymbolTable rootSymbolTable = new LocalSymbolTable(reporter);

		// -- Package
//		List<String> packageQName = Arrays.asList(input.getResourcePhysicalName().split("/"));
		QName packageQName = new PhysicalResourceQName(input.getResourcePhysicalName()).toQName();


		// -- Root class transformer
		String rootClassName = "$root$";
		AstToken name = new AstToken(0, 0, 0, 0, rootClassName, "<unknown>");
		AstClassDeclaration rootClass = new AstClassDeclaration(reporter, false /*is interface*/, name);
		CreateRootClassTransformer createRootClassTransformer = new CreateRootClassTransformer(reporter, rootClass);

		// -- Various transformations
		applyVisitors(reporter, compilationUnit, 
				// Add top-level functions in a 'root' class
				createRootClassTransformer, 
					
				// Set symbol tables parents (thus create the ST tree), add symbols to tables
				new SymbolStructureVisitor(/*rootSymbolTable, */globalSymbolTable, packageQName)	
				);


		// -- Resolve symbols in expressions
		applyVisitors(reporter, compilationUnit, 
				new SymbolResolutionVisitor(globalSymbolTable, packageQName)
				);
		
		List<AstModuleDeclaration> moduleDeclarations = compilationUnit.getModuleDeclarations();
		for(AstModuleDeclaration moduleDeclaration: moduleDeclarations) {
			ModuleContent moduleContent = new ModuleContent(reporter, moduleDeclaration);
			this.moduleContents.add(moduleContent);
		}
	}
	
	private List<AstModuleDeclaration> parseModuleDescriptors(List<InputTextProvider> inputs) {
		List<AstModuleDeclaration> moduleDeclarations = new ArrayList<>();
		for(InputTextProvider input: inputs) {
			if(input.isModuleDescriptor()) {
				AstModuleDeclaration md = parseModuleDescriptor(input);
				moduleDeclarations.add(md);
			}
		}
		return moduleDeclarations;
	}

	private AstPackageDeclaration parsePackageDescriptor(InputTextProvider input) {
		SiriusParser parser = createParser(input, new AstFactory(reporter, globalSymbolTable));
		AstPackageDeclaration pd = parser.packageDescriptorCompilationUnit().packageDeclaration.declaration;
		return pd;
	}

	/** Parse package declarations for a module.
	 * 
	 * @param inputs
	 * @param moduleContent
	 * @return
	 */
	private void parsePackagesDescriptors(List<InputTextProvider> inputs, ModuleContent moduleContent) {
		List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();


		for(InputTextProvider input: inputs) {
			if(input.isPackageDescriptor()) {
				PhysicalPath modulePPath = moduleContent.getModulePath();
				PhysicalPath packagePPath = input.getPackagePhysicalPath();
				if(packagePPath.startWith(modulePPath)) {
					AstPackageDeclaration pd = parsePackageDescriptor(input);
//					PackageContent pc = new PackageContent(pd);
					packageDeclarations.add(pd);
				}
			}
		}
		
		if(packageDeclarations.isEmpty()) {
			// -- Add initial package (name is module qname)
			QualifiedName name = moduleContent.getModuleDeclaration().getqName();
			AstPackageDeclaration unnamedPackage = new AstPackageDeclaration (reporter, name.toQName());
			packageDeclarations.add(unnamedPackage);
		}
		
		moduleContent.addPackageContents(packageDeclarations);
	}
	
	void parseCodeInput(InputTextProvider input, AstPackageDeclaration packageContent, ModuleContent  moduleContent) {
		parseInput(input);
	}
	
//	@Override
	private void addInput(List<InputTextProvider> inputs) {
		
		// -- Parse module descriptors
		List<AstModuleDeclaration> modules = parseModuleDescriptors(inputs);
		this.moduleContents.addAll(modules.stream()
				.map(md -> new ModuleContent(reporter, md))
				.collect(Collectors.toList()));
		
		// -- Add packages descriptors
		for(ModuleContent mc: this.moduleContents) {
// 			List<PackageContent> modulePackages = 
 					parsePackagesDescriptors(inputs, mc);
//			mc.addPackageContents(modulePackages);
		}

		// -- Add code to packages
		for(InputTextProvider input: inputs) {
			if(input.isModuleDescriptor() || input.isPackageDescriptor())
				continue;
			
			for(ModuleContent mc: this.moduleContents) {
				for(AstPackageDeclaration pc: mc.getPackageContents()) {
					if(input.getPackageLogicalPath().matchQName(pc.getQname())) {
						parseCodeInput(input, pc, mc);
						break;
					}
				}
			}
		}
		for(ModuleContent mc: this.moduleContents) {
			mc.createDefaultPackageIfNeeded();
		}		
	}

	private void applyVisitors(Reporter reporter, StandardCompilationUnit compilationUnit, AstVisitor... visitors) {
		for(AstVisitor v: visitors) {
			compilationUnit.visit(v);
			if(reporter.hasErrors()) {
				return;
			}
		}
	}

	@Override
	public List<ModuleDeclaration> getModuleDeclarations() {
		return this.moduleContents.stream()
				.map( (ModuleContent mc ) -> mc.getModuleDeclaration().getModuleDeclaration())
				.collect(Collectors.toList());
	}

}
