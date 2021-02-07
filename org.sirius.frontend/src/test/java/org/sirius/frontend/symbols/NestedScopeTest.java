package org.sirius.frontend.symbols;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;

public class NestedScopeTest {

	@Test
	@DisplayName("Check qnames in class methods and values")
	public void classContentScopeTest() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){C s; public void f(){C s;}}");
		AstClassDeclaration cd = session.getAstModules().get(0).getPackageDeclarations().get(0).getClassDeclarations().get(0);
		FunctionDefinition methodF = cd.getFunctionDefinitions().get(0);
		
		Optional<FunctionDefinition> fFunct =  cd.getScope().getFunction("f");
		assertThat(fFunct.isPresent(), equalTo(true));
		assertThat(fFunct.get().getqName(), equalTo(new QName("p", "k", "C", "f")));
		assertThat(fFunct.get().getPartials().get(0).getqName(), equalTo(new QName("p", "k", "C", "f")));
		
		Optional<AstMemberValueDeclaration> sValue =  cd.getScope().getValue("s");
		assertThat(sValue.isPresent(), equalTo(true));
		assertThat(sValue.get().getQname(), equalTo(new QName("p", "k", "C", "s")));
		

	}

	@Test
	@DisplayName("Check qnames in class methods and values")
//	@Disabled
	public void functionScopeByArumentsScopeTest() {
		ScriptSession session = Compiler.compileScript("#!\n public void f(Integer x, Integer y, Integer z){}");
		FunctionDefinition methodF = session.getAstModules().get(0).getPackageDeclarations().get(0).getFunctionDeclarations().get(0);
		
		assertThat(methodF.getPartials(), hasSize(4));
		
		Scope scope0 = methodF.getPartials().get(0).getScope();
		assertTrue(scope0.lookupSymbol("x").isEmpty());
		assertTrue(scope0.lookupSymbol("y").isEmpty());
		assertTrue(scope0.lookupSymbol("z").isEmpty());
		
		Scope scope1 = methodF.getPartials().get(1).getScope();
		assertTrue(scope1.lookupSymbol("x").isPresent());
		assertTrue(scope1.lookupSymbol("y").isEmpty());
		assertTrue(scope1.lookupSymbol("z").isEmpty());
		
//		Partial partial2 = methodF.getPartials().get(2);
		Scope scope2 = methodF.getPartials().get(2).getScope();
		assertTrue(scope2.lookupSymbol("x").isPresent());
		assertTrue(scope2.lookupSymbol("y").isPresent());
		assertTrue(scope2.lookupSymbol("z").isEmpty());
		
//		Partial partial3 = methodF.getPartials().get(3);
		Scope scope3 = methodF.getPartials().get(3).getScope();
		assertTrue(scope3.lookupSymbol("x").isPresent());
		assertTrue(scope3.lookupSymbol("y").isPresent());
		assertTrue(scope3.lookupSymbol("z").isPresent());
		
		
//		PartialList methodF = cd.getFunctionDeclarations().get(0);
		
//		Optional<PartialList> fFunct =  cd.getScope().getFunction("f");
//		assertThat(fFunct.isPresent(), equalTo(true));
//		assertThat(fFunct.get().getqName(), equalTo(new QName("p", "k", "C", "f")));
//		assertThat(fFunct.get().getPartials().get(0).getqName(), equalTo(new QName("p", "k", "C", "f")));
//		
//		Optional<AstMemberValueDeclaration> sValue =  cd.getScope().getValue("s");
//		assertThat(sValue.isPresent(), equalTo(true));
//		assertThat(sValue.get().getQname(), equalTo(new QName("p", "k", "C", "s")));
		
	}
	
	@Test
	@DisplayName("Check qnames in functions and values")
	public void functionsContentScopeTest() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){C s;}}");
		FunctionDefinition pl = session
				.getAstModules().get(0)					// 
				.getPackageDeclarations().get(0)		// p.k
				.getClassDeclarations().get(0)			// C
				.getFunctionDefinitions().get(0);		// f
		
		Optional<AstLocalVariableStatement> sValue =  pl.getPartials().get(0).getScope().getLocalVariable("s");
		assertThat(sValue.isPresent(), equalTo(true));
		assertThat(sValue.get().getVarName().getText(), equalTo("s"));
	}

}
