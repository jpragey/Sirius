package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
	@Override
	public Reporter getReporter() {
		return reporter;
	}

	private List<ModuleDeclaration> modules = new ArrayList<>();
	
	private Optional<ShebangDeclaration> shebang = Optional.empty(); 

	private DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable();

	private ScriptCompilationUnit compilationUnit;

	private List<AstModuleDeclaration> astModules = new ArrayList<>();

	public ScriptSession(Reporter reporter, InputTextProvider input) {
		super();
		this.reporter = reporter;
		addInput(input);
	}

	
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

		this.shebang = compilationUnit.getShebangDeclaration();

		this.astModules = compilationUnit.getModuleDeclarations();
		this.modules = new ArrayList<>();
		for(AstModuleDeclaration astMd: astModules) {
			ModuleDeclaration md = astMd.getModuleDeclaration();
			this.modules.add(md);
		}
	}
	

	private ScriptCompilationUnit parseScriptInput(InputTextProvider input) {
		
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		parser.factory = astFactory;
		
		parser.currentModule = AstModuleDeclaration.createUnnamed(reporter);	// TODO: WTF ???

		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));


		// -- Parsing
		ScriptCompilationUnitContext unitContext = parser.scriptCompilationUnit();
		ScriptCompilationUnit compilationUnit = unitContext.unit;
		
		return compilationUnit;
	}

	public Optional<ShebangDeclaration> getShebang() {
		return shebang;
	}

	@Override
	public List<ModuleDeclaration> getModuleDeclarations() {
		return this.modules;
	}


	public List<AstModuleDeclaration> getAstModules() {
		return astModules;
	}
	
	
}
