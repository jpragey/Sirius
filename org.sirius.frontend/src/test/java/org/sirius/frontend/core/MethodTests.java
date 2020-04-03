package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

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
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class MethodTests {

	@Test 
	public void checkLocalVariableParsing() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){C s; public void f(){C s;}}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(1);
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

		Type type = lvs.getType();
		assert(type instanceof ClassDeclaration);
		assertEquals( ((ClassDeclaration)type).getQName(), new QName("p", "k", "C"));

	}
	
	@Test (description = "A local variable can have an initializer")
	public void localVariableInitializerTest() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; "
				+ "class C(){"
				+ "   Integer s10 = 10; "
				+ "   public void f(){Integer s11 = 11;}"
				+ "}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(1);
		assertEquals(pack.getQName().dotSeparated(), "p.k");
		
		
		ClassDeclaration cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));
		
		// -- function local value
		MemberFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));

		assertEquals(func.getBodyStatements().size(), 1);
		LocalVariableStatement funcLvs = (LocalVariableStatement)func.getBodyStatements().get(0);
		assertEquals(funcLvs.getName().getText(), "s11");

		assertTrue(funcLvs.getInitialValue().isPresent());

		Type funcLvstype = funcLvs.getType();
		assert(funcLvstype instanceof ClassDeclaration);
		
		
		assertEquals( ((ClassDeclaration)funcLvstype).getQName(), new QName("sirius", "lang", "Integer"));

		
		// -- class member
		assertEquals(cd.getValues().size(), 1);
		MemberValue lvs = cd.getValues().get(0);

		assertEquals(lvs.getName().getText(), "s10");
		
		assertTrue(lvs.getInitialValue().isPresent());

		Type type = lvs.getType();
		assert(type instanceof ClassDeclaration);
		assertEquals( ((ClassDeclaration)type).getQName(), new QName("sirius", "lang", "Integer"));

	}
}
