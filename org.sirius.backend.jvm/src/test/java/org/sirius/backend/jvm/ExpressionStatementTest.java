package org.sirius.backend.jvm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

import org.sirius.backend.jvm.JvmClassWriterTest.MyClassLoader;
import org.sirius.backend.jvm.mocktypes.MockClassType;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.FrontEnd;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.core.TextInputTextProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExpressionStatementTest {

	Reporter reporter;

	@BeforeMethod
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}
	
	@AfterMethod
	public void teardown() {
		assertTrue(reporter.ok());
	}

	// TODO: factorize
	public static ScriptSession compileScript(String sourceCode) {
		
		Reporter reporter = new AccumulatingReporter(new ShellReporter());
		
		FrontEnd frontEnd = new FrontEnd(reporter);
		TextInputTextProvider provider = new TextInputTextProvider("some/package", "script.sirius", sourceCode);
		ScriptSession session = frontEnd.createScriptSession(provider);
		assertTrue(reporter.ok());
		
		return session;
	}

	@Test 
	public void checkFunctionCallIsPresentAsExpressionStatement() {
		ScriptSession session = compileScript("#!\n package p.k; class C(){public void f(){println(\"Hello\");}}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(0);
		assertEquals(pack.getQName().dotSeparated(), "p.k");
		
		
		ClassDeclaration cd = pack.getClasses().get(0);
		assertEquals(cd.getQName(), new QName("p", "k", "C"));
		
		MemberFunction func = cd.getFunctions().get(0);
		assertEquals(func.getQName(), new QName("p", "k", "C", "f"));

		assertEquals(func.getBodyStatements().size(), 1);
		ExpressionStatement statement = (ExpressionStatement)func.getBodyStatements().get(0);
		
		JvmBackend backend = new JvmBackend(reporter /*, Optional.empty()*//* classDir*/ /*, Optional.empty()*/ /* module*/, false /*verboseAst*/
				);
		backend.process(session);
		
	}

	@Test 
	public void checkFunctionCallIsPresentAsExpressionStatement0() {
		
		AccumulatingReporter reporter = new AccumulatingReporter(new ShellReporter()); 

		
//		
//		
//		
//		ClassDeclaration cd = newClassDeclaration(new QName("a", "b", "C"), Arrays.asList(
//				newMemberFunction(new QName("a", "b", "C", "main")))
//		);
//		JvmClassWriter writer = new JvmClassWriter(reporter, Optional.empty() /*class dir*/, false /* verbose 'ast' */);
//		Bytecode bytecode = writer.createByteCode(cd);
//		System.out.println("bytecode: " + bytecode.size() + " bytes");
////		assertEquals( JvmClassWriter.classInternalName(cd), "a/b/C");
//
//		// -- Check bytecode by loading it
//		MyClassLoader classloader = new MyClassLoader(getClass().getClassLoader(), bytecode.getBytes(), Arrays.asList("a.b.C")/*definedClasses*/);
//
//		@SuppressWarnings("rawtypes")
//		Class cls = classloader.loadClass("a.b.C" /*mainClassQName*/);
//		Object helloObj = cls.newInstance();
//		Method[] methods = helloObj.getClass().getDeclaredMethods();
//		
//		assertEquals(methods.length, 1);
////		assertEquals(methods[0].toString(), "public static int a.b.C.main(java.lang.String)");
//		assertEquals(methods[0].toString(), "public static void a.b.C.main()");
//		assertEquals(methods[0].getName(), "main");
//
//		for(Method m: methods)
//			System.out.println("Method: " + m);
//
//////		Object result = runBytecode(bytecode, Arrays.asList("a.b.C")/*definedClasses*/, "a.b.C" /*mainClassQName*/);
//
		
	}

}
