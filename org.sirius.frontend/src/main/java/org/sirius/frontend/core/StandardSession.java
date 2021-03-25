package org.sirius.frontend.core;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.PackageDescriptorCompilationUnit;
import org.sirius.frontend.ast.StandardCompilationUnit;
import org.sirius.frontend.core.parser.ModuleDescriptorCompilatioUnitParser;
import org.sirius.frontend.core.parser.PackageDescriptorCompilatioUnitParser;
import org.sirius.frontend.core.parser.StandardCompilatioUnitParser;
import org.sirius.frontend.core.stdlayout.ModuleFiles;
import org.sirius.frontend.core.stdlayout.PackageFiles;
import org.sirius.frontend.parser.SiriusLexer;
import org.sirius.frontend.parser.SiriusParser;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class StandardSession implements Session {

	private Reporter reporter;

	private Scope globalScope = new Scope();
	
	private List<ModuleContent> moduleContents = new ArrayList<>();

	public StandardSession(Reporter reporter, List<ModuleFiles> inputs) {
		super();
		this.reporter = reporter;
		this.addInputModules(inputs);
	}


	@Override
	public Reporter getReporter() {
		return reporter;
	}

	private SiriusParser createParser(InputTextProvider input) {
		String sourceCode = input.getText();
		
		CharStream stream = CharStreams.fromString(sourceCode); 
		
		SiriusLexer lexer = new SiriusLexer(stream);
		CommonTokenStream tokenStream = new CommonTokenStream(lexer);
		
		SiriusParser parser = new SiriusParser(tokenStream);

		parser.removeErrorListeners();
		parser.addErrorListener(new AntlrErrorListenerProxy(reporter));
		
		return parser;
	}
	
	private AstModuleDeclaration parseModuleDescriptor(InputTextProvider input) {
		SiriusParser parser = createParser(input);
		
		ModuleDescriptorCompilatioUnitParser.PackageDescriptorCompilationUnitVisitor v = new ModuleDescriptorCompilatioUnitParser.PackageDescriptorCompilationUnitVisitor(reporter);
		
		AstModuleDeclaration md = parser.moduleDescriptorCompilationUnit().accept(v);
		return md;
	}

	private void parseStandardInput(InputTextProvider input) {
		SiriusParser parser = createParser(input);

		ParseTree tree = parser.standardCompilationUnit();
		
		StandardCompilatioUnitParser.StandardCompilationUnitVisitor visitor = new StandardCompilatioUnitParser.StandardCompilationUnitVisitor(reporter);
		StandardCompilationUnit compilationUnit = visitor.visit(tree);

		parseInput(input, compilationUnit);
	}


	private void parseInput(InputTextProvider input, AbstractCompilationUnit compilationUnit) {


		// -- Various transformations
		stdTransform(reporter, input, compilationUnit, globalScope);
		
		List<AstModuleDeclaration> moduleDeclarations = compilationUnit.getModuleDeclarations();
		for(AstModuleDeclaration moduleDeclaration: moduleDeclarations) {
			ModuleContent moduleContent = new ModuleContent(reporter, moduleDeclaration);
			this.moduleContents.add(moduleContent);
		}
	}
	
	private List<AstModuleDeclaration> parseModuleDescriptors_old(List<InputTextProvider> inputs) { // TODO delete
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
		SiriusParser parser = createParser(input);
		ParseTree tree = parser.packageDescriptorCompilationUnit();
				
		PackageDescriptorCompilatioUnitParser.PackageDescriptorCompilationUnitVisitor visitor = new PackageDescriptorCompilatioUnitParser.PackageDescriptorCompilationUnitVisitor(reporter);
		PackageDescriptorCompilationUnit packageCU = visitor.visit(tree);
		
		return packageCU.getPackageDeclaration();
	}

	private void addInputModules(List<ModuleFiles> inputs) {
		
		for(ModuleFiles mf: inputs) {
			InputTextProvider input = mf.getModuleDescriptor();
			assert(input.isModuleDescriptor());
			AstModuleDeclaration md = parseModuleDescriptor(input);
			ModuleContent mc = new ModuleContent(reporter, md);
			this.moduleContents.add(mc);

			// -- parse package declarator

			for(PackageFiles packageFiles: mf.getPackages()) {
				InputTextProvider pdInputTextProvider = packageFiles.getPackageDescriptor();
				assert(pdInputTextProvider.isPackageDescriptor());

				PhysicalPath modulePPath = md.getModulePPath().get();
				PhysicalPath packagePPath = pdInputTextProvider.getPackagePhysicalPath();
				if(packagePPath.startWith(modulePPath)) {
					AstPackageDeclaration pd = parsePackageDescriptor(pdInputTextProvider);
					md.addPackageDeclaration(pd);
					
				}
				
				// Parse non-declarators source code
				for(InputTextProvider sourceInput: packageFiles.getSourceFiles() ) {
					parseStandardInput(sourceInput);
				}
			}
			
			// -- If module has no package declarator, add a default one
			if(md.getPackageDeclarations().isEmpty()) {
				QName qname = md.getqName();
				AstPackageDeclaration pd = new AstPackageDeclaration(reporter, qname, 
						Collections.emptyList(),	// List<PartialList> functionDeclarations, 
						Collections.emptyList(),	//List<AstClassDeclaration> classDeclarations, 
						Collections.emptyList(),	//List<AstInterfaceDeclaration> interfaceDeclarations, 
						Collections.emptyList()		//List<AstMemberValueDeclaration> valueDeclarations
						);
				md.addPackageDeclaration(pd);
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
