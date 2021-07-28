package org.sirius.backend.jvm.functional;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ExecutionEnvironmentImpl;
import org.sirius.frontend.apiimpl.FunctionClassImpl;

public class ExecutableClassTest {

	@Test
	@DisplayName("Demo: create/run an executable class (happy path)")
//	@Disabled("temp")
	public void simpleClassExecutionDemo() {
		QName qName = new QName("a", "b","MainClass");
		
		ExecutionEnvironment environment = new ExecutionEnvironmentImpl(
				Type.voidType,	// return type
				List.of()		// List<Statement> bodyStatements;
				) ;
		
		
		
//		MapOfList<QName, FunctionDefinition> allFctMap = new MapOfList<QName, FunctionDefinition>();
////		List<AbstractFunction> memberFunctions, 
//		List<MemberValue> valueDeclarations, //List<AncestorInfo> ancestors,
//		List<AstInterfaceDeclaration> interfaces, 
//		Optional<ExecutionEnvironment> executionEnvironment);
//		
//		ClassDeclarationImpl cd0 = new ClassDeclarationImpl(qName, 
//				MapOfList<QName, FunctionDefinition> allFctMap, 
////				List<AbstractFunction> memberFunctions, 
//				List<MemberValue> valueDeclarations, //List<AncestorInfo> ancestors,
//				List<AstInterfaceDeclaration> interfaces, 
//				Optional<ExecutionEnvironment> executionEnvironment);
//		
//		ClassDeclarationImpl cd = new ClassDeclarationImpl(qName, 
//				List.of(),// new MapOfList<QName, FunctionDefinition>(), // allFctMap, 
//				List.of(),//List<MemberValue> valueDeclarations, //List<AncestorInfo> ancestors,
//				List.of(),//List<AstInterfaceDeclaration> interfaces, 
//				Optional.of(environment)
//				);
		FunctionClassImpl functionClass = new FunctionClassImpl(qName, Type.voidType /*returnType*/, List.of() /*body*/);
	}
	
}
