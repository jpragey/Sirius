package org.sirius.frontend.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.core.ScriptSession;

public class LocalVariablesTest {

	@Test
	@Disabled("Recursive declaration/use of C causes StackOverflow - TODO")
	public void testMemberValuesAreResolved() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){C s;}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		List<PackageDeclaration> packages = md.packageDeclarations();
		PackageDeclaration pack = packages.get(0);
		assertEquals(pack.qName().dotSeparated(), "p.k");
		
		
		ClassType cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));
		
		assertEquals(cd.getMemberValues().size(), 1);
		MemberValue lvs = cd.getMemberValues().get(0);

		assertEquals(lvs.getName().getText(), "s");
		
		Type type = lvs.getType();
		assert(type instanceof ClassType);
		assertEquals( ((ClassType)type).getQName(), new QName("p", "k", "C"));

	}
	
	@Test
	@Disabled("Restore ASAP (when functions local variables won't use symboltables directly any more)")
	public void testLocalVariablesAreResolved() {
		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){void f(){C s;}}");
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){void f(){C1 s;}}  class C1(){}");
//		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.packageDeclarations().get(0);
		assertEquals(pack.qName().dotSeparated(), "p.k");
		
		
		ClassType cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));

		assertEquals(cd.getFunctions().size(), 1);
		AbstractFunction func = cd.getFunctions().get(0);
		
		assertEquals(func.getBodyStatements().get().size(), 1);
		LocalVariableStatement lvs = (LocalVariableStatement)func.getBodyStatements().get().get(0);
		
		assertEquals(lvs.getName().getText(), "s");

		Type type = lvs.getType();
		assert(type instanceof ClassType);
		assertEquals( ((ClassType)type).getQName(), new QName("p", "k", "C1"));
//		assertEquals( ((ClassType)type).getQName(), new QName("p", "k", "C"));
	}
}
