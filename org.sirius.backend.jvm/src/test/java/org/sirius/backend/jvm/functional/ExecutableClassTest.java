package org.sirius.backend.jvm.functional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.DescriptorFactory;
import org.sirius.backend.jvm.InMemoryClassWriterListener;
import org.sirius.backend.jvm.JvmClass;
import org.sirius.backend.jvm.JvmMemberFunction;
import org.sirius.backend.jvm.JvmMemberValue;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IntegerConstantExpression;
import org.sirius.frontend.api.ReturnStatement;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ClassDeclarationImpl;
import org.sirius.frontend.apiimpl.ExecutionEnvironmentImpl;
import org.sirius.frontend.apiimpl.FunctionClassImpl;
import org.sirius.frontend.apiimpl.IntegerConstantExpressionImpl;
import org.sirius.frontend.apiimpl.ReturnStatementImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.*;

public class ExecutableClassTest {
	private Reporter reporter;
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
	}

	@Test
	@DisplayName("Demo: create/run an executable class (happy path)")
//	@Disabled("temp")
	/**
	 * Equivalent to:
	 * //package p0.p1 {
	 *   class MainFunc {
	 *     int operator() {
	 *       return 42;
	 *     }
	 *   }
	 * //} 
	 */
	public void simpleClassExecutionDemo() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		QName qName = new QName("a", "b","MainClass");
//		QName qName = new QName("a", "b","MainFunction");
		
//		ExecutionEnvironment environment = new ExecutionEnvironmentImpl(
//				Type.voidType,	// return type
//				List.<Statement>of()		// List bodyStatements;
//				) ;
		
//		Expression expression = new IntegerConstantExpressionImpl(42);
		
		// -- Create intermediate code
		FunctionClassImpl functionClass = new FunctionClassImpl(qName, Type.integerType /*returnType*/, List.of(
				new ReturnStatementImpl(
						new IntegerConstantExpressionImpl(42))
				) /*body*/);
		
		
		BackendOptions backendOptions = new BackendOptions(reporter, Optional.of("TODO") /*TODO*/);
		DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);

		
//		JvmMemberFunction jvmMemberFunction = new JvmMemberFunction(reporter, backendOptions, descriptorFactory, AbstractFunction memberFunction,
//						/*JvmScope containerScope,*/  true /*boolean isStatic*/);
//
//				
//				;

		
		
		JvmClass jvmClass = new JvmClass(reporter, qName, backendOptions, descriptorFactory, 
				List.<JvmMemberFunction>of(/*jvmMemberFunction*/),// memberFunctions, 
				List.<JvmMemberValue>of() // memberValues
				);
		Bytecode bc = jvmClass.createBytecode();
		InMemoryClassWriterListener.InMemoryClassLoader cl = InMemoryClassWriterListener.InMemoryClassLoader.create(reporter, getClass().getClassLoader(), List.of(bc));
		
		// -- Check result
		Class<?> cls = cl.loadClass("a.b.MainClass");
		assertThat(cls, notNullValue());

		Object helloObj = cls.getDeclaredConstructor().newInstance();
		Method[] methods = helloObj.getClass().getDeclaredMethods();
		
//		Method main = cls.getMethod("main", new Class[] { /* String[].class */});
//		Object[] argTypes = new Object[] {};
//		
//		Object result = main.invoke(null, argTypes /*, args*/);

		
	}
	
}
