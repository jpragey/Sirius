package org.sirius.frontend.core;

import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ClassDeclaration;
import org.sirius.frontend.ast.CompilationUnit;
import org.sirius.frontend.ast.ModuleDescriptorCompilationUnit;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.CompilationUnitContext;
import org.sirius.frontend.parser.SiriusParser.ModuleDescriptorCompilationUnitContext;
import org.sirius.frontend.sirius.lang.SiriusLangPackage;
import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.frontend.symbols.SymbolResolutionVisitor;
import org.sirius.frontend.symbols.SymbolStructureVisitor;
import org.sirius.frontend.transform.CreateRootClassTransformer;

public class ModuleContent {

	private Reporter reporter;
	public final static String rootClassName = "$root$";

	private String pkgName;
	
	 
	private List<CompilationUnit> compilationUnits;
	private ModuleDescriptorCompilationUnit moduleDescriptorCompilationUnit;
	
	public ModuleContent(Reporter reporter, String pkgName, //SiriusLangPackage languagePackage,
			List<CompilationUnit> compilationUnits,
			ModuleDescriptorCompilationUnit moduleDescriptorCompilationUnit
			/*, List<InputTextProvider> textProviders/*, InputTextProvider moduleDescriptorProvider*/) 
	{
		super();
		this.reporter = reporter;
		this.pkgName = pkgName;

		this.compilationUnits = compilationUnits;
		this.moduleDescriptorCompilationUnit = moduleDescriptorCompilationUnit;
	}

	public static ModuleContent parseAll(Reporter reporter, String pkgName, SiriusLangPackage languagePackage, GlobalSymbolTable globalSymbolTable, List<InputTextProvider> textProviders) {


		List<CompilationUnit> compilationUnits = new ArrayList<>();
		ModuleDescriptorCompilationUnit mdcu = null;
		
		// -- Root class transformer
		AstToken name = new AstToken(0, 0, 0, 0, rootClassName, "<unknown>");
		ClassDeclaration rootClass = new ClassDeclaration(reporter, false /*is interface*/, name);
		CreateRootClassTransformer createRootClassTransformer = new CreateRootClassTransformer(reporter, rootClass);
		
		for(InputTextProvider provider: textProviders) {
			if(provider.isModuleDescriptor()) {
				mdcu = parseModuleDescriptor(reporter, languagePackage, provider, globalSymbolTable, createRootClassTransformer);
			} else {
				CompilationUnit cu = parseSingleInput(reporter, languagePackage, provider, globalSymbolTable, createRootClassTransformer);
				compilationUnits.add(cu);
			}
		}
		
		assert(mdcu != null);
		ModuleContent moduleContent = new ModuleContent(reporter, pkgName, compilationUnits, mdcu); 
		
		return moduleContent;
	}


	private static ModuleDescriptorCompilationUnit parseModuleDescriptor(Reporter reporter, SiriusLangPackage languagePackage, 
			InputTextProvider input, 
			GlobalSymbolTable globalSymbolTable,
			CreateRootClassTransformer createRootClassTransformer) 
	{
		
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
//		lexer.languagePackage = languagePackage;
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);
//		parser.languagePackage = languagePackage;

		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		ModuleDescriptorCompilationUnitContext unitContext = parser.moduleDescriptorCompilationUnit();
		assertNotNull(unitContext);
		ModuleDescriptorCompilationUnit compilationUnit = unitContext.unit;

		return compilationUnit;
	}
	
	private static CompilationUnit parseSingleInput(Reporter reporter, SiriusLangPackage languagePackage, 
			InputTextProvider input, 
			GlobalSymbolTable globalSymbolTable,
			CreateRootClassTransformer createRootClassTransformer) 
	{
		
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		CompilationUnitContext unitContext = parser.compilationUnit();
		assertNotNull(unitContext);
		CompilationUnit compilationUnit = unitContext.unit;

//		LocalSymbolTable rootSymbolTable = new LocalSymbolTable(reporter);

		// -- Package
		List<String> packageQName = Arrays.asList(input.getResourcePhysicalName().split("/"));


		
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
		
		return compilationUnit;
	}
	
	private static void applyVisitors(Reporter reporter, CompilationUnit compilationUnit, AstVisitor... visitors) {
		for(AstVisitor v: visitors) {
			compilationUnit.visit(v);
			if(reporter.hasErrors()) {
				return;
			}
		}
	}
	
	public List<CompilationUnit> getCompilationUnits() {
		return compilationUnits;
	}

	public ModuleDescriptorCompilationUnit getModuleDescriptorCompilationUnit() {
		return moduleDescriptorCompilationUnit;
	}

	@Override
	public String toString() {
		return "[Module " + pkgName + "]";
	}

	public String getPkgName() {
		return pkgName;
	}
	
}
