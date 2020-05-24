package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.MemberValueAccessExpression;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
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
		
		AbstractFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));

//		assertEquals(func.getBodyStatements().size(), 1);
//		LocalVariableStatement lvs = (LocalVariableStatement)func.getBodyStatements().get(0);

		assertEquals(cd.getMemberValues().size(), 1);
		MemberValue lvs = cd.getMemberValues().get(0);

		assertEquals(lvs.getName().getText(), "s");

		Type type = lvs.getType();
		assert(type instanceof ClassDeclaration);
		assertEquals( ((ClassDeclaration)type).getQName(), new QName("p", "k", "C"));

	}
	
	@Test 
	public void checkMemberValueParsing() {
		String script = "#!\n "
//				+ "class B() {}   "
				+ "class A() {Integer mib = 42;}   "
//				+ "Integer main() {return 42;}";
				+ "A main() {A a = A(); return a.mib;}";
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript(script);
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		List<PackageDeclaration> packages = md.getPackages();
		PackageDeclaration pack = packages.get(0);
		assertEquals(pack.getQName().dotSeparated(), "");
		
		List<ClassDeclaration> classes = pack.getClasses();
		ClassDeclaration cd = classes.get(0);
		assertEquals(cd.getQName(), new QName("A"));
		
//		ClassDeclaration packCd = pack.getClasses().get(1);
//		assertEquals(cd.getQName(), new QName("A"));
		List<AbstractFunction> tlFuncs = pack.getFunctions();
		AbstractFunction mainFunc = tlFuncs.get(0);
		assertEquals(mainFunc.getQName().dotSeparated(), "main");
		
		List<Statement> body = mainFunc.getBodyStatements().get();
		assertEquals(body.size(), 2);
		
		LocalVariableStatement locVarStmt = (LocalVariableStatement)body.get(0);
		Type locVarType = locVarStmt.getType();
		assert(locVarType instanceof ClassDeclaration);
		ClassDeclaration locVarCD = (ClassDeclaration)locVarType;
		List<MemberValue> members = locVarCD.getMemberValues();
		MemberValue member0 = members.get(0);
		
		Type member0Type = member0.getType();
		
		ReturnStatement retStmt = (ReturnStatement)body.get(1);
		
		Expression retExpr = retStmt.getExpression();
		MemberValueAccessExpression maccExpr = (MemberValueAccessExpression)retExpr;
		
		Expression ex = maccExpr.getContainerExpression();
		
//		MemberFunction func = cd.getFunctions().get(0);
//		assertEquals(func.getQName(), new QName("A", "f"));
//
//		assertEquals(func.getBodyStatements().size(), 1);
//		LocalVariableStatement lvs = (LocalVariableStatement)func.getBodyStatements().get(0);
		
//
//		assertEquals(cd.getValues().size(), 1);
//		MemberValue lvs = cd.getValues().get(0);
//
//		assertEquals(lvs.getName().getText(), "s");
//
//		Type type = lvs.getType();
//		assert(type instanceof ClassDeclaration);
//		assertEquals( ((ClassDeclaration)type).getQName(), new QName("p", "k", "C"));
//
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
		AbstractFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));

		assertEquals(func.getBodyStatements().get().size(), 1);
		LocalVariableStatement funcLvs = (LocalVariableStatement)func.getBodyStatements().get().get(0);
		assertEquals(funcLvs.getName().getText(), "s11");

		assertTrue(funcLvs.getInitialValue().isPresent());

		Type funcLvstype = funcLvs.getType();
		assert(funcLvstype instanceof ClassDeclaration);
		
		
		assertEquals( ((ClassDeclaration)funcLvstype).getQName(), new QName("sirius", "lang", "Integer"));

		
		// -- class member
		assertEquals(cd.getMemberValues().size(), 1);
		MemberValue lvs = cd.getMemberValues().get(0);

		assertEquals(lvs.getName().getText(), "s10");
		
		assertTrue(lvs.getInitialValue().isPresent());

		Type type = lvs.getType();
		assert(type instanceof ClassDeclaration);
		assertEquals( ((ClassDeclaration)type).getQName(), new QName("sirius", "lang", "Integer"));

	}
}
