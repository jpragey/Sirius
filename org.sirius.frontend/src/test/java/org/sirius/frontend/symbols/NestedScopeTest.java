package org.sirius.frontend.symbols;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;

public class NestedScopeTest {

	@Test
	@DisplayName("Check qnames in class methods and values")
	public void classContentScopeTest() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){C s; public void f(){C s;}}");
		AstClassDeclaration cd = session.getAstModules().get(0).getPackageDeclarations().get(0).getClassDeclarations().get(0);
		PartialList methodF = cd.getFunctionDeclarations().get(0);
		
		Optional<PartialList> fFunct =  cd.getScope().getFunction("f");
		assertThat(fFunct.isPresent(), equalTo(true));
		assertThat(fFunct.get().getqName(), equalTo(new QName("p", "k", "C", "f")));
		assertThat(fFunct.get().getPartials().get(0).getqName(), equalTo(new QName("p", "k", "C", "f")));
		
		Optional<AstMemberValueDeclaration> sValue =  cd.getScope().getValue("s");
		assertThat(sValue.isPresent(), equalTo(true));
		assertThat(sValue.get().getQname(), equalTo(new QName("p", "k", "C", "s")));
		

	}
	@Test
	@DisplayName("Check qnames in functions and values")
	public void functionsContentScopeTest() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){C s;}}");
		PartialList pl = session
				.getAstModules().get(0)					// 
				.getPackageDeclarations().get(0)		// p.k
				.getClassDeclarations().get(0)			// C
				.getFunctionDeclarations().get(0);		// f
		
		Optional<AstLocalVariableStatement> sValue =  pl.getPartials().get(0).getScope().getLocalVariable("s");
		assertThat(sValue.isPresent(), equalTo(true));
		assertThat(sValue.get().getVarName().getText(), equalTo("s"));
	}
	
}
