package org.sirius.frontend.integration;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FunctionCall;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.TypeCastExpression;
import org.sirius.frontend.core.ScriptSession;
import org.sirius.frontend.parser.Compiler;
import org.sirius.frontend.symbols.Symbol;

public class SdkCallTest {

	@Test
	@Disabled("println() Doesn't take Stringifiable for now")
	public void sdkCalled() {
//		ScriptSession session = Compiler.compileScript("#!\n void run() {println(\"Hello\");}");
		ScriptSession session = Compiler.compileScript("#!\n void run() {println(42);}");

		assertEquals(session.getModuleDeclarations().size(), 1);
		ModuleDeclaration md = session.getModuleDeclarations().get(0);
		
		PackageDeclaration pack = md.packageDeclarations().get(0);
		AbstractFunction runFunction = pack.getFunctions().get(0);
		
		Statement callExprStatement = runFunction.bodyStatements().get().get(0);
		assert(callExprStatement instanceof ExpressionStatement);

		Expression callExpression = ((ExpressionStatement)callExprStatement).expression();
		assert(callExpression instanceof FunctionCall);
		FunctionCall functionCall = (FunctionCall)callExpression;
		
		assertEquals(functionCall.arguments().size(), 1);
		Expression argExpression = functionCall.arguments().get(0);

		TypeCastExpression argTypeCast = (TypeCastExpression) argExpression;
		IntegerConstantExpression argIntConstant = (IntegerConstantExpression)argTypeCast.expression();
		
		assertEquals(argIntConstant.value(), 42);
		
		Symbol symbol = session.getCompilationUnit().getScope().getSymbolTable()/*.getGlobalSymbolTable()*/.lookupByQName(new QName("sirius", "lang", "println")).get();
		
	}
	
}
