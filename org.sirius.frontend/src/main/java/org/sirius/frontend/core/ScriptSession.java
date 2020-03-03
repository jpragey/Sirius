package org.sirius.frontend.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.ScriptCompilationUnitContext;
import org.sirius.frontend.sdk.SdkTools;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class ScriptSession implements Session {

	private Reporter reporter;
	
	private List<ModuleContent> moduleContents = new ArrayList<>();
	private Optional<ShebangDeclaration> shebang = Optional.empty(); 

	private DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable();

	public ScriptSession(Reporter reporter, InputTextProvider input) {
		super();
		this.reporter = reporter;
		addInput(input);
	}

	@Override
	public List<ModuleContent> getModuleContents() {
		return moduleContents;
	}
	
	
	public DefaultSymbolTable getGlobalSymbolTable() {
		return globalSymbolTable;
	}

	private void addInput(InputTextProvider input) {
		SdkTools sdkTools = new SdkTools(reporter);
		sdkTools.parseSdk(globalSymbolTable);

		AbstractCompilationUnit compilationUnit = parseScriptInput(reporter, input, globalSymbolTable);
		
		compilationUnit.updateParentsDeeply();
		
		this.shebang = compilationUnit.getShebangDeclaration();

		this.moduleContents.addAll(compilationUnit.getModuleDeclarations().stream()
				.map(mod -> new ModuleContent(reporter, compilationUnit.getCurrentModule()))
				.collect(Collectors.toList())
				);

		stdTransform(reporter, input, compilationUnit, globalSymbolTable);

	}
	
	private AbstractCompilationUnit parseScriptInput(Reporter reporter, InputTextProvider input, DefaultSymbolTable globalSymbolTable) {
		AstFactory astFactory = new AstFactory(reporter, globalSymbolTable);
		SiriusParser parser = createParser(reporter, input, astFactory);
		
		parser.factory = astFactory;
		
		AstModuleDeclaration moduleDeclaration = new AstModuleDeclaration(reporter);
		
		parser.currentModule = moduleDeclaration;

		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));

		// -- Parsing
		ScriptCompilationUnitContext unitContext = parser.scriptCompilationUnit();
		AbstractCompilationUnit compilationUnit = unitContext.unit;
		
		stdTransform(reporter, input, compilationUnit, globalSymbolTable);

		return compilationUnit;
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
