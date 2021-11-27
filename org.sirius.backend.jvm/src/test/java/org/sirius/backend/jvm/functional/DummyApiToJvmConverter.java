package org.sirius.backend.jvm.functional;

import java.util.List;

import org.sirius.backend.jvm.DescriptorFactory;
import org.sirius.backend.jvm.JvmExpressionStatement;
import org.sirius.backend.jvm.JvmIfElseStatement;
import org.sirius.backend.jvm.JvmReturnStatement;
import org.sirius.backend.jvm.JvmStatement;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.BlockStatement;
import org.sirius.frontend.api.BooleanType;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ExpressionStatement;
import org.sirius.frontend.api.FloatType;
import org.sirius.frontend.api.FunctionClass;
import org.sirius.frontend.api.FunctionDeclaration;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.IntegerType;
import org.sirius.frontend.api.LocalVariableStatement;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.StringConstantExpression;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.VoidType;

/** 
 * 
 * @author jpragey
 *
 */
public class DummyApiToJvmConverter {
	
	static class JvmType {
		private String name;

		public JvmType(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	static interface JvmTypes {
		static JvmType jvmVoidType = new JvmType("Void");
		static JvmType jvmBooleanType = new JvmType("Boolean");
		static JvmType jvmFloatType = new JvmType("Float");
		static JvmType jvmIntegerType = new JvmType("Integer");
		static JvmType jvmArrayType = new JvmType("Array");
		static JvmType jvmClassType = new JvmType("Class");
		static JvmType jvmFunctionDeclarationType = new JvmType("FunctionDeclaration");
		static JvmType jvmStringConstantType = new JvmType("StringConstant");
	}
	static JvmType mapToJvmType(Reporter reporter, Type apiType) {
		JvmType jvmType = JvmTypes.jvmVoidType;
		if(apiType == Type.voidType) jvmType = JvmTypes.jvmVoidType;
		else if(apiType == Type.booleanType) jvmType = JvmTypes.jvmBooleanType;
		else if(apiType == Type.floatType) jvmType = JvmTypes.jvmFloatType;
		else if(apiType == Type.integerType) jvmType = JvmTypes.jvmIntegerType;
		else if(apiType instanceof BooleanType ) jvmType = JvmTypes.jvmBooleanType;
		else if(apiType instanceof ArrayType) jvmType = JvmTypes.jvmArrayType;
		else if(apiType instanceof ClassType) jvmType = JvmTypes.jvmClassType;
		else if(apiType instanceof FloatType) jvmType = JvmTypes.jvmFloatType;
		else if(apiType instanceof FunctionDeclaration) jvmType = JvmTypes.jvmFunctionDeclarationType;
		else if(apiType instanceof IntegerType) jvmType = JvmTypes.jvmIntegerType;
		else if(apiType instanceof StringConstantExpression) jvmType = JvmTypes.jvmStringConstantType;
		else if(apiType instanceof VoidType) jvmType = JvmTypes.jvmVoidType;
		else reporter.fatal("Can't convert to JVM unsupported API Type " + apiType + " (" + apiType.getClass().getCanonicalName() + ")");
		
		return jvmType;
	}
	
	static JvmStatement mapToJvmStatement(Statement apiStmt, Reporter reporter, DescriptorFactory descriptorFactory) {
		JvmStatement jvmStatement = null;

//		if(apiStmt instanceof BlockStatement); // TODO: ???
//		else if(apiStmt instanceof ExpressionStatement) new JvmExpressionStatement(reporter, descriptorFactory, apiStmt, scope);
//		else if(apiStmt instanceof IfElseStatement) new JvmIfElseStatement(reporter, descriptorFactory, scopeManager, apiStmt, scope);
//		else if(apiStmt instanceof LocalVariableStatement); // Ignore
//		else if(apiStmt instanceof ReturnStatement) new JvmReturnStatement(reporter, descriptorFactory, apiStmt, scope);
//		
//		throw new UnsupportedOperationException("No bytecode to write for statement " + apiStmt.getClass().getCanonicalName());
		
		return jvmStatement;

	}
	
	void parse(Reporter reporter, FunctionClass functionClass ) {
		DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
		
		QName functionQName = functionClass.qName();
		Type apiType = functionClass.returnType();
		JvmType jvmType = mapToJvmType(reporter, apiType);
		
		List<Statement> apiStatements = functionClass.bodyStatements();
		for(Statement st: apiStatements) {
			JvmStatement stmt = mapToJvmStatement(st, reporter, descriptorFactory);
			
		}
	}

}
