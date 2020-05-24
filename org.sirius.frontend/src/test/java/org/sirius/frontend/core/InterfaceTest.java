package org.sirius.frontend.core;

import static org.testng.Assert.assertEquals;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.parser.Compiler;
import org.testng.annotations.Test;

public class InterfaceTest {

	@Test (enabled = true)
	public void simpleInterfaceParsing() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; "
				+ "interface I {"
				+ "		public Integer f(){ "
				+ "    		return 42;"
				+ "		}"
				+ "}"
				+ "class C() implements I {}"
				+ "");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(1);
		assertEquals(pack.getQName().dotSeparated(), "p.k");
		
		// -- check interface
		InterfaceDeclaration cd = pack.getInterfaces().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "I"));
		
		AbstractFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "I", "f"));
		
		// -- check implementation
		ClassDeclaration implClass = pack.getClasses().get(0);
		assertEquals(implClass.getQName(), new QName("p", "k", "C"));
		
		AbstractFunction inheritedFunc = implClass.getFunctions().get(0);
		assertEquals(inheritedFunc.getQName(), new QName("p", "k", "I", "f"));
	}
	
}
