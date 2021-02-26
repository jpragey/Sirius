package org.sirius.frontend.symbols;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.core.parser.InterfaceDeclarationParser;
import org.sirius.frontend.core.parser.ParserUtil;
import org.sirius.frontend.parser.SiriusParser;

import com.google.common.collect.ImmutableList;

public class SymbolExportVisitorTest {


	private Reporter reporter ;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	@AfterEach
	public void tearDown() {
		assert(this.reporter.ok());
	}
	
	
	private AstInterfaceDeclaration parseInterfaceDeclaration(String inputText) {
		
		SiriusParser parser = ParserUtil.createParser(reporter, inputText);
		ParseTree tree = parser.interfaceDeclaration();
				
		InterfaceDeclarationParser.InterfaceDeclarationVisitor visitor = new InterfaceDeclarationParser.InterfaceDeclarationVisitor(reporter);
		AstInterfaceDeclaration interfaceDeclaration = visitor.visit(tree);
		return interfaceDeclaration;
	}
	
	private AstModuleDeclaration buildModule(QName moduleQName, List<AstPackageDeclaration> packageDeclarations) {
		AstModuleDeclaration md = new AstModuleDeclaration(reporter, moduleQName, 
				AstToken.internal("1.0"), 
				new ModuleImportEquivalents(), 
				Collections.emptyList() /*moduleImports*/,
				packageDeclarations);
		return md;
	}

	private	AstClassDeclaration newClass(String simpleName, List<FunctionDefinition> partialLists) {
		AstClassDeclaration cd = new AstClassDeclaration(reporter, AstToken.internal(simpleName), 
				ImmutableList.of(),	//<TypeParameter> typeParameters,
				ImmutableList.copyOf(partialLists), //ImmutableList<PartialList> functionDeclarations,
				List.of(),		//<AstMemberValueDeclaration> valueDeclarations,
				List.of(),		//<AstFunctionParameter> anonConstructorArguments,
				List.of()		//<AncestorInfo> ancestorInfos
				); 
		return cd;
	}

	private	AstInterfaceDeclaration newInterface(String simpleName) {
		AstInterfaceDeclaration cd = AstInterfaceDeclaration.newInterface(reporter, AstToken.internal(simpleName));
		return cd;
	}

	private AstPackageDeclaration newPackage(QName qname, 
			List<FunctionDefinition> functionDeclarations, List<AstClassDeclaration> classDeclarations, 
			List<AstInterfaceDeclaration> interfaceDeclarations, List<AstMemberValueDeclaration> valueDeclarations) {
		return new AstPackageDeclaration(reporter, qname, 
				functionDeclarations, classDeclarations, 
				interfaceDeclarations, valueDeclarations);
	}

	private FunctionDefinition newFunctionDefinition(String nameStr) {
		FunctionDefinition pl = new FunctionDefinition(List.of() , 
				AstType.noType,// returnType, 
				false /*member*/,             
				AstToken.internal(nameStr), 
				Collections.emptyList() // body
				);
		return pl;
	}
	
	@Test
	@DisplayName("Simplest interface declarations")
	public void simplestInterfaceDeclarations() {
		
		AstModuleDeclaration md = buildModule(new QName("ma", "mb"), Arrays.asList(
			newPackage(new QName("a","b"), 
					List.of(newFunctionDefinition("fct")), //<FunctionDefinition>, 
					List.of(newClass("C", List.of(newFunctionDefinition("mfct")))), 
					List.of(newInterface("I")), 	//Arrays.asList<AstInterfaceDeclaration>(),
					List.of() 						//Arrays.asList<AstMemberValueDeclaration>() 
					)
			));

		md.visit(new QNameSetterVisitor());

		ExportedSymbolTable xsTable = new ExportedSymbolTable();
		
		SymbolExportVisitor v = new SymbolExportVisitor(xsTable);
		md.visit(v);
		
		assertThat(xsTable.getExportedClass    (new QName(/*"ma", "mb", */"a", "b", "C")).isPresent(), equalTo(true));
		assertThat(xsTable.getExportedFunction (new QName(/*"ma", "mb", */"a", "b", "fct")).isPresent(), equalTo(true));
		assertThat(xsTable.getExportedFunction (new QName(/*"ma", "mb", */"a", "b", "C", "mfct")).isPresent(), equalTo(true));
		assertThat(xsTable.getExportedInterface(new QName(/*"ma", "mb", */"a", "b", "I")).isPresent(), equalTo(true));
	}

}
