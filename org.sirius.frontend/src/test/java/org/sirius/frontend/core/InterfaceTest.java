package org.sirius.frontend.core;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.parser.Compiler;

public class InterfaceTest {

	@Test
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
		
		PackageDeclaration pack = md.packageDeclarations().get(0);
		assertEquals(pack.qName().dotSeparated(), "p.k");
		
		// -- check interface
		ClassType cd = pack.getInterfaces().get(0);
		assertEquals(cd.qName(), new QName("p", "k", "I"));
		
		AbstractFunction func = cd.memberFunctions().get(0);
		assertEquals(func.qName(), new QName("p", "k", "I", "f"));
		
		// -- check implementation
		ClassType implClass = pack.getClasses().get(0);
		assertEquals(implClass.qName(), new QName("p", "k", "C"));
		
		AbstractFunction inheritedFunc = implClass.memberFunctions().get(0);
		assertEquals(inheritedFunc.qName(), new QName("p", "k", "I", "f"));
		
	}

	@Test
	public void multipleInterfaceParsing() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; "
				+ "interface I0 {}"
				+ "interface I1 {}"
				+ "interface I2 {}"
				+ "class C() implements I0, I1, I2 {}"
				+ "");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.packageDeclarations().get(0);
		assertEquals(pack.qName().dotSeparated(), "p.k");
		
		// -- check interface
		assertEquals(pack.getInterfaces().size(), 3);
		ClassType cd0 = pack.getInterfaces().get(0);
		assertEquals(cd0.qName(), new QName("p", "k", "I0"));
		
		assertEquals(pack.getInterfaces().stream().map(classType -> classType.qName().getLast()).toList(), 
				Arrays.asList("I0", "I1", "I2"));
	}

	@Test
	public void ParsingInterfaceWithMultipleImplemented_OK() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; "
				+ "interface I0 {}"
				+ "interface I1 {}"
				+ "interface I2 {}"
				+ "interface I implements I0,I1,I2 {}"
				+ "");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.packageDeclarations().get(0);
		assertEquals(pack.qName().dotSeparated(), "p.k");
		
		// -- check interface
		assertEquals(pack.getInterfaces().size(), 4);
		ClassType cd0 = pack.getInterfaces().get(0);
		assertEquals(pack.getInterfaces().get(0).qName(), new QName("p", "k", "I0"));
		assertEquals(pack.getInterfaces().get(1).qName(), new QName("p", "k", "I1"));
		assertEquals(pack.getInterfaces().get(2).qName(), new QName("p", "k", "I2"));
		assertEquals(pack.getInterfaces().get(3).qName(), new QName("p", "k", "I"));

//		ClassType intf = pack.getInterfaces().get(3);
//		intf.

//		
//		assertEquals(pack.getInterfaces().stream().map(classType -> classType.qName().getLast()).toList(), 
//				Arrays.asList("I0", "I1", "I2"));
	}

	////////////////////////////////////////////
//	public static class SInterface {}
//	public static class SClassDeclaration {}
//    
//	public SiriusBaseVisitor<Object> cdVisitor = new SiriusBaseVisitor<Object>() {
//    	public Object visitImplementedInterfaces(org.sirius.frontend.parser.SiriusParser.ImplementedInterfacesContext ctx) {
//    		List<String> intNames = ctx.TYPE_ID().stream().map(tn->tn.getText()).toList();
//    		return new SInterface();
//    	};
//    	public SClassDeclaration visitClassDeclaration(org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext ctx) {
//    		String className = ctx.className.getText();
//    		Object intfs = visit(ctx.implementedInterfaces());
//    		return new SClassDeclaration();
//    	};
//    };

//	public SiriusBaseVisitor<SClassDeclaration> cdVisitor = new SiriusBaseVisitor<SClassDeclaration>() {
//    	public SInterface visitImplementedInterfaces(org.sirius.frontend.parser.SiriusParser.ImplementedInterfacesContext ctx) {
//    		String intName0 = ctx.interfaceName.getText();
//    		List<String> intNames = ctx.TYPE_ID().stream().map(tn->tn.getText()).toList();
//    		return new SInterface();
//    	};
//    	public SClassDeclaration visitClassDeclaration(org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext ctx) {
//    		String className = ctx.className.getText();
//    		Object intfs = visit(ctx.implementedInterfaces());
//    		return new SClassDeclaration();
//    	};
//    };

//	@Test 
//	public void parseStr() {
////        CharStream in = CharStreams.fromFileName(DIRBASE + file);
////        CharStream in = CharStreams.fromString("implements2 I0, I1, I2");
//        CharStream in = CharStreams.fromString("class C() implements I0, I1, I2 {}");
//        SiriusLexer lexer = new SiriusLexer(in);
//        CommonTokenStream tokens = new CommonTokenStream(lexer);
//        SiriusParser parser = new SiriusParser(tokens);
////        aldebParser.StartContext tree = parser.start();
////        SiriusParser.ImplementedInterfacesContext tree = parser.implementedInterfaces();
//        SiriusParser.ClassDeclarationContext tree = parser.classDeclaration();
////        aldebCustomVisitor visitor = new aldebCustomVisitor();
////        visitor.visit(tree);
//        Object qn = cdVisitor.visit(tree);
//
//	}
	
	@Test
	public void multipleInterfaceParsing_2() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; "
				+ "interface I0 {}"
				+ "interface I1 {}"
				+ "interface I2 {}"
				+ "class C() implements I0{}"
				+ "");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.packageDeclarations().get(0);
		assertEquals(pack.qName().dotSeparated(), "p.k");
		
		// -- check interface
//		assertEquals(pack.getInterfaces().size(), 3);
//		ClassType cd0 = pack.getInterfaces().get(0);
//		assertEquals(cd0.qName(), new QName("p", "k", "I0"));
//		
//		assertEquals(pack.getInterfaces().stream().map(classType -> classType.qName().getLast()).toList(), 
//				Arrays.asList("I0", "I1", "I2"));
	}
}
