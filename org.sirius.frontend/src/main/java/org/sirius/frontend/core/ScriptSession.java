package org.sirius.frontend.core;

import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.ClassDeclaration;
import org.sirius.frontend.ast.ModuleDeclaration;
import org.sirius.frontend.ast.PackageDeclaration;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.ScriptCompilationUnitContext;
import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.frontend.symbols.SymbolResolutionVisitor;
import org.sirius.frontend.symbols.SymbolStructureVisitor;
import org.sirius.frontend.transform.CreateRootClassTransformer;

public class ScriptSession implements Session {

	private Reporter reporter;
	
	private List<ModuleContent> moduleContents = new ArrayList<>();
	private Optional<ShebangDeclaration> shebang = Optional.empty(); 

	public ScriptSession(Reporter reporter, InputTextProvider input) {
		super();
		this.reporter = reporter;
		addInput(input);
	}

	@Override
	public List<ModuleContent> getModuleContents() {
		return moduleContents;
	}

//	@Override
	private void addInput(InputTextProvider input) {
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

		GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
		ModuleDeclaration moduleDeclaration = new ModuleDeclaration(reporter);
		PackageDeclaration pd = new PackageDeclaration(reporter);
		moduleDeclaration.addPackageDeclaration(pd);
		
		parser.currentModule = moduleDeclaration;
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		ScriptCompilationUnitContext unitContext = parser.scriptCompilationUnit();
		assertNotNull(unitContext);
		ScriptCompilationUnit compilationUnit = unitContext.unit;

		this.shebang = compilationUnit.getShebangDeclaration();
		
		ModuleContent mc = new ModuleContent(reporter, moduleDeclaration);
		this.moduleContents.add(mc);
//		LocalSymbolTable rootSymbolTable = new LocalSymbolTable(reporter);

		// -- Package
		List<String> packageQName = Arrays.asList(input.getResourcePhysicalName().split("/"));


		// -- Root class transformer
		String rootClassName = "$root$";
		AstToken name = new AstToken(0, 0, 0, 0, rootClassName, "<unknown>");
		ClassDeclaration rootClass = new ClassDeclaration(reporter, false /*is interface*/, name);
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
		
	}
	
	private void applyVisitors(Reporter reporter, ScriptCompilationUnit compilationUnit, AstVisitor... visitors) {
		for(AstVisitor v: visitors) {
			compilationUnit.visit(v);
			if(reporter.hasErrors()) {
				return;
			}
		}
	}

	public Optional<ShebangDeclaration> getShebang() {
		return shebang;
	}
	
}
