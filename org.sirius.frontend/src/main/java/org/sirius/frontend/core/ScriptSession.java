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
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.core.parser.ScriptCompilatioUnitParser;
import org.sirius.frontend.parser.SLexer;
import org.sirius.frontend.parser.Sirius;
import org.sirius.frontend.parser.Sirius.ScriptCompilationUnitContext;
import org.sirius.frontend.sdk.SdkTools;
import org.sirius.frontend.symbols.Scope;

public class ScriptSession implements Session {

	private Reporter reporter;
	
	@Override
	public Reporter getReporter() {
		return reporter;
	}

	private List<ModuleDeclaration> modules = new ArrayList<>();
	
	private Optional<ShebangDeclaration> shebang = Optional.empty(); 

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
	
	private void addInput(InputTextProvider input) {
		SdkTools sdkTools = new SdkTools(reporter);

		Scope sdkScope = sdkTools.getScope();
		this.compilationUnit = parseScriptInput(input, sdkScope);

		stdTransform(reporter, input, compilationUnit);

		this.shebang = compilationUnit.getShebangDeclaration();

		this.astModules = compilationUnit.getModuleDeclarations();
		this.modules = new ArrayList<>();
		for(AstModuleDeclaration astMd: astModules) {
			ModuleDeclaration md = astMd.getModuleDeclaration();
			this.modules.add(md);
		}
	}
	
	private ScriptCompilationUnit parseScriptInput(InputTextProvider input, Scope sdkScope) {
		
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SLexer lexer = new SLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		Sirius parser = new Sirius(tokenStream);

		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		ScriptCompilationUnitContext unitContext = parser.scriptCompilationUnit();

		ScriptCompilatioUnitParser.ScriptCompilationUnitVisitor visitor = new ScriptCompilatioUnitParser.ScriptCompilationUnitVisitor(reporter, sdkScope);
		ScriptCompilationUnit compilationUnit = visitor.visit(unitContext);

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
