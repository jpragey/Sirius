package org.sirius.frontend.core;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.ast.AstFactory;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.parser.SiriusParser.ModuleDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.StandardCompilationUnitContext;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class StandardSession implements Session {

	private Reporter reporter;

	private DefaultSymbolTable globalSymbolTable = new DefaultSymbolTable();
	
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
		return ctxt.declaration;
	}

	private void parseStandardInput(InputTextProvider input) {
		SiriusParser parser = createParser(input, new AstFactory(reporter, globalSymbolTable));
		// -- Parsing
		StandardCompilationUnitContext unitContext = parser.standardCompilationUnit();
		AbstractCompilationUnit compilationUnit = unitContext.stdUnit;
		
		parseInput(input, compilationUnit);
	}


	private void parseInput(InputTextProvider input, AbstractCompilationUnit compilationUnit) {


		// -- Various transformations
		stdTransform(reporter, input, compilationUnit, globalSymbolTable);
		
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
		parseStandardInput(input);
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

	@Override
	public List<ModuleDeclaration> getModuleDeclarations() {
		return this.moduleContents.stream()
				.map( (ModuleContent mc ) -> mc.getModuleDeclaration().getModuleDeclaration())
				.collect(Collectors.toList());
	}

}
