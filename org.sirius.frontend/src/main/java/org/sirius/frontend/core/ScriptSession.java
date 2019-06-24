package org.sirius.frontend.core;

import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstVisitor;
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
	
	private void addInput(InputTextProvider input) {
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

		GlobalSymbolTable globalSymbolTable = new GlobalSymbolTable();
		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
		AstModuleDeclaration moduleDeclaration = new AstModuleDeclaration(reporter);
//		AstPackageDeclaration pd = new AstPackageDeclaration(reporter);
//		moduleDeclaration.addPackageDeclaration(pd);
		
		parser.currentModule = moduleDeclaration;

//		ScriptCurrentState scriptCurrentState = new ScriptCurrentState(reporter);
//		parser.scriptCurrentState = scriptCurrentState;
		
		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		ScriptCompilationUnitContext unitContext = parser.scriptCompilationUnit();
		assertNotNull(unitContext);
		ScriptCompilationUnit compilationUnit = unitContext.unit;
		compilationUnit.updateParentsDeeply();

		this.shebang = compilationUnit.getShebangDeclaration();
		
//		ModuleContent mc = new ModuleContent(reporter, moduleDeclaration);
//		ModuleContent mc = new ModuleContent(reporter, compilationUnit.getCurrentModule());
//		this.moduleContents.add(mc);
		this.moduleContents.addAll(compilationUnit.getModuleDeclarations().stream()
				.map(mod -> new ModuleContent(reporter, compilationUnit.getCurrentModule()))
				.collect(Collectors.toList())
						
						);
		
		
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

	@Override
	public List<ModuleDeclaration> getModuleDeclarations() {
		
		return this.moduleContents.stream()
				.map( (ModuleContent mc ) -> mc.getModuleDeclaration().getModuleDeclaration())
				.collect(Collectors.toList());
	}
}
