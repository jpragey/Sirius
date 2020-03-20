package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.ScriptCompilationUnitContext;
import org.sirius.frontend.sdk.SdkTools;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class ScriptSession implements Session {

	private Reporter reporter;
	
//	private List<ModuleContent> moduleContents = new ArrayList<>();
//	private List<AstModuleDeclaration> modules0 = new ArrayList<>();
	@Override
	public Reporter getReporter() {
		return reporter;
	}

	private List<ModuleDeclaration> modules = new ArrayList<>();
	
	private Optional<ShebangDeclaration> shebang = Optional.empty(); 

	private DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable();

	private ScriptCompilationUnit compilationUnit;

	public ScriptSession(Reporter reporter, InputTextProvider input) {
		super();
		this.reporter = reporter;
		addInput(input);
	}

//	@Override
//	public List<ModuleContent> getModuleContents() {
//		return moduleContents;
//	}
	
	
	public ScriptCompilationUnit getCompilationUnit() {
		return compilationUnit;
	}

	public DefaultSymbolTable getGlobalSymbolTable() {
		return globalSymbolTable;
	}

	
	private void addInput(InputTextProvider input) {
		SdkTools sdkTools = new SdkTools(reporter);
		sdkTools.parseSdk(globalSymbolTable);

		this.compilationUnit = parseScriptInput(input);

		stdTransform(reporter, input, compilationUnit, globalSymbolTable);

		compilationUnit.updateParentsDeeply();
		
		this.shebang = compilationUnit.getShebangDeclaration();

//		this.moduleContents.addAll(compilationUnit.getModuleDeclarations().stream()
//				.map(mod -> new ModuleContent(reporter, compilationUnit.getCurrentModule()))
//				.collect(Collectors.toList())
//				);
		
		
		this.modules = new ArrayList<>();
		for(AstModuleDeclaration astMd: compilationUnit.getModuleDeclarations()) {
			ModuleDeclaration md = astMd.getModuleDeclaration();
			this.modules.add(md);
		}
//		this.modules.addAll(
//				compilationUnit.getModuleDeclarations().stream().map( AstModuleDeclaration::getModuleDeclaration)
//				.collect(Collectors.toList())
//				);

//		stdTransform(reporter, input, compilationUnit, globalSymbolTable);

	}
	
//	private SiriusParser createParser(Reporter reporter, InputTextProvider input, AstFactory astFactory) {
//		String sourceCode = input.getText();
//		
//		CharStream stream = CharStreams.fromString(sourceCode); 
//		
//		SiriusLexer lexer = new SiriusLexer(stream);
//		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
//		
//		SiriusParser parser = new SiriusParser(tokenStream);
//
//		parser.factory = astFactory;
//		
////		parser.currentModule = new AstModuleDeclaration(reporter);
//		parser.currentModule = AstModuleDeclaration.createUnnamed(reporter);	// TODO: WTF ???
//
//		parser.removeErrorListeners();
//		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));
//		
//		return parser;
//	}

	private ScriptCompilationUnit parseScriptInput(/*Reporter reporter, */InputTextProvider input /*, DefaultSymbolTable globalSymbolTable*/) {
		
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
//		parser.currentModule = new AstModuleDeclaration(reporter);
		parser.currentModule = AstModuleDeclaration.createUnnamed(reporter);	// TODO: WTF ???

		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

//		SiriusParser parser = createParser(reporter, input, astFactory);
		
//		parser.factory = astFactory;
		
//		AstModuleDeclaration moduleDeclaration = AstModuleDeclaration.createUnnamed(reporter);
//		
//		parser.currentModule = moduleDeclaration;
//
//		parser.removeErrorListeners();
//		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		ScriptCompilationUnitContext unitContext = parser.scriptCompilationUnit();
		ScriptCompilationUnit compilationUnit = unitContext.unit;
		
//		stdTransform(reporter, input, compilationUnit, globalSymbolTable);

		return compilationUnit;
	}

	public Optional<ShebangDeclaration> getShebang() {
		return shebang;
	}

	@Override
	public List<ModuleDeclaration> getModuleDeclarations() {
		return this.modules;
//		return this.modules.stream()
//				.map( AstModuleDeclaration::getModuleDeclaration)
//				.collect(Collectors.toList());
//		return this.moduleContents.stream()
//				.map( (ModuleContent mc ) -> mc.getModuleDeclaration().getModuleDeclaration())
//				.collect(Collectors.toList());
	}
}
