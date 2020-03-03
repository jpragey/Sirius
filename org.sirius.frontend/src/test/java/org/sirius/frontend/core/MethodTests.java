package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class MethodTests {

	@Test 
	public void checkLocalVariableParsing() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){C s; public void f(){C s;}}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(0);
		assertEquals(pack.getQName().dotSeparated(), "p.k");
		
		
		ClassDeclaration cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));
		
		MemberFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));

//		assertEquals(func.getBodyStatements().size(), 1);
//		LocalVariableStatement lvs = (LocalVariableStatement)func.getBodyStatements().get(0);

		assertEquals(cd.getValues().size(), 1);
		MemberValue lvs = cd.getValues().get(0);

		assertEquals(lvs.getName().getText(), "s");
////		assertEquals(lvs.getType().getClass(), "s");

	}
}
