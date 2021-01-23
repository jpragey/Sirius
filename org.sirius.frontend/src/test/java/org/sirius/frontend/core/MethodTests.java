package org.sirius.frontend.core;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.FunctionActualArgument;
import org.sirius.frontend.api.FunctionFormalArgument;
import org.sirius.frontend.api.LocalVariableReference;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.MemberValueAccessExpression;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstReturnStatement;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.Partial.FunctionImpl;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.SimpleReferenceExpression;
import org.sirius.frontend.parser.Compiler;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.Symbol;

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
	
	@Test
	@DisplayName("A local variable can have an initializer")
	public void localVariableInitializerTest() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n package p.k; "
				+ "class C(){"
				+ "   Integer s10 = 10; "
				+ "   public void f(){Integer s11 = 11;}"
				+ "}");
		
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.getPackages().get(0);
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
	
	void assertIsFctArgInteger(int argIndex, String expectedName,  AbstractFunction apiFunc) {
		FunctionFormalArgument arg1 = apiFunc.getArguments().get(argIndex);
		assertEquals(arg1.getQName().getLast(), expectedName);
		assert(arg1.getType() instanceof ClassDeclaration);
		
		ClassDeclaration arg1Type = (ClassDeclaration)arg1.getType();
		assertEquals(arg1Type.getQName(), new QName("sirius", "lang", "Integer"));
		
	}

	@Test
	@DisplayName("Simple call of a global function")
	public void functionsWith3ParametersHaveASuitableDelegateStructureTest() {
		ScriptSession session = Compiler.compileScript("#!\n"
				+ "void add(Integer x, Integer y, Integer z) {}");
		
		AstModuleDeclaration module = session.getAstModules().get(0);
		AstPackageDeclaration pack = module.getPackageDeclarations().get(0);
		assertEquals(pack.getQname().dotSeparated(), "");
		PartialList func = pack.getFunctionDeclarations().get(0);
		assertEquals(func.getqName().dotSeparated(), "add");
		
		assertEquals(func.getPartials().size(), 4);
		Partial partial0 = func.getPartials().get(0);
////		assertEquals(partial0.getCaptures().size(), 0);
		assertThat(partial0.getArgs(), hasSize(3));

		Partial partial1 = func.getPartials().get(1);
		Partial partial2 = func.getPartials().get(2);

		Partial partial3 = func.getPartials().get(3);
////		assertEquals(partial3.getCaptures().size(), 3);
////		assertEquals(partial3.getCaptures().get(2).getName().getText(), "z");
		assertThat(partial3.getArgs(), hasSize(0));
		
//		assert(func.getDelegate().isPresent());
		
	}
	
	
	@Test
	@DisplayName("Definition of a simple global function with parameters")
//	@Disabled("")
	public void defineFunctionWithParamsTest() {
		ScriptSession session = Compiler.compileScript("#!\n"
				+ "Integer add(Integer x, Integer y) {return x;}"
				+ "");
		
		AstModuleDeclaration module = session.getAstModules().get(0);
		AstPackageDeclaration pack = module.getPackageDeclarations().get(0);
		assertEquals(pack.getQname().dotSeparated(), "");
		PartialList func = pack.getFunctionDeclarations().get(0);
		assertEquals(func.getqName().dotSeparated(), "add");
		
		//func.getSymbolTable().dump();
		
//		assertEquals(func.getFormalArguments().size(), 2);
		Partial allArgsPartial = func.getPartials().get(0);
		assertSame(allArgsPartial, func.getAllArgsPartial());
		assertEquals(allArgsPartial.getArgs().size(), 2);
		
		DefaultSymbolTable partialSymbolTable = allArgsPartial.getSymbolTable();
		Optional<AstFunctionParameter> optArg = partialSymbolTable.lookupFunctionArgument("x");
		assert(optArg.isPresent());
		Optional<AstFunctionParameter> opt1Arg = partialSymbolTable.lookupFunctionArgument("y");
		assert(opt1Arg.isPresent());
		
		AstReturnStatement returnStatement = (AstReturnStatement)func.getBody().get().get(0);
		
		assert(returnStatement.getExpression() instanceof SimpleReferenceExpression);
		SimpleReferenceExpression returnExpr = (SimpleReferenceExpression)returnStatement.getExpression();
		DefaultSymbolTable st = returnExpr.getSymbolTable();
//		st.dump();
		Optional<Symbol> xOptSymbol = st.lookupBySimpleName("x");
		
//		DefaultSymbolTable symbolTable = returnExpr.getSymbolTable();
		
		
		
		
		// -- API
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration apiPack = md.getPackages().get(0);
		assertEquals(apiPack.getQName().dotSeparated(), "");
		
		AbstractFunction apiAddFunc = apiPack.getFunctions().get(0);
		assertEquals(apiAddFunc.getQName().dotSeparated(), "add");
		assertEquals(apiAddFunc.getArguments().size(), 2);

		
		assertIsFctArgInteger(0, "x",  apiAddFunc);
		assertIsFctArgInteger(1, "y",  apiAddFunc);

		assertNotNull(apiAddFunc.getBodyStatements());
		assertEquals(apiAddFunc.getBodyStatements().get().size(), 1);
		ReturnStatement retStmt = (ReturnStatement)apiAddFunc.getBodyStatements().get().get(0);
		Expression retExpr = retStmt.getExpression();
		assertThat(retExpr, instanceOf(LocalVariableReference.class /* FunctionActualArgument.class*/));
//		FunctionActualArgument refToXExpress = (FunctionActualArgument)retExpr;
		LocalVariableReference refToXExpress = (LocalVariableReference)retExpr;
		
		assertEquals(refToXExpress.getName().getText(), "x");
		Type xArgType = refToXExpress.getType();
		assert(xArgType instanceof ClassDeclaration);
		ClassDeclaration xClassDecl = (ClassDeclaration)xArgType;
		assertEquals(xClassDecl.getQName().dotSeparated(), "sirius.lang.Integer");

	}
	
	@Test
	@DisplayName("Simple call of a global function")
	public void callFunctionWithParamsTest() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n"
				+ "Integer add(Integer x, Integer y) {return x;}"
				+ "void main() {Integer i = add(42,43);}");
		
		AstModuleDeclaration module = session.getAstModules().get(0);
		AstPackageDeclaration pack = module.getPackageDeclarations().get(0);
		assertEquals(pack.getQname().dotSeparated(), "");
		PartialList func = pack.getFunctionDeclarations().get(0);
		assertEquals(func.getqName().dotSeparated(), "add");

		PartialList funcMain = pack.getFunctionDeclarations().get(1);
		assertEquals(funcMain.getqName().dotSeparated(), "main");
		
		Partial mainPartial = funcMain.byArgCount(0).get();
//		mainPartial.getSymbolTable().dump();
	}
	
	@Test
	@DisplayName("Partials of the same function have different API function (args count differ)")
	public void partialsOfSameFuncHaveDifferentAPIFuncs() {
//		ScriptSession session = Compiler.compileScript("#!\n package p.k; class C(){public void f(){String s;}}");
		ScriptSession session = Compiler.compileScript("#!\n"
				+ "Integer add(Integer x, Integer y) {return x;}"
				);
		
		AstModuleDeclaration module = session.getAstModules().get(0);
		AstPackageDeclaration pack = module.getPackageDeclarations().get(0);
		assertEquals(pack.getQname().dotSeparated(), "");
		PartialList func = pack.getFunctionDeclarations().get(0);
		assertEquals(func.getqName().dotSeparated(), "add");

		Partial partial0 = func.getPartials().get(0);
		FunctionImpl partial0Api = partial0.toAPI();
		assertThat(partial0Api.getArguments(), hasSize(2));
		
		Partial partial1 = func.getPartials().get(1);
		FunctionImpl partial1Api = partial1.toAPI();
		assertEquals(partial1Api.getArguments().size(), 1);
		
		Partial partial2 = func.getPartials().get(2);
		FunctionImpl partial2Api = partial2.toAPI();
		assertEquals(partial2Api.getArguments().size(), 0);
		
		
	}
	
}
