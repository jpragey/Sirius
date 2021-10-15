package org.sirius.backend.jvm.functional;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.BackendOptions;
import org.sirius.backend.jvm.Bytecode;
import org.sirius.backend.jvm.ClassExecutorFunction;
import org.sirius.backend.jvm.Constants;
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
import org.sirius.frontend.api.FunctionClass;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
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

		// -- Create intermediate code
		FunctionClass functionClass = new FunctionClassImpl(qName, Type.integerType /*returnType*/, List.of(
				new ReturnStatementImpl(
						new IntegerConstantExpressionImpl(42))
				) /*body*/);
		
		
		BackendOptions backendOptions = new BackendOptions(reporter, Optional.of("TODO") /*TODO*/);
		DescriptorFactory descriptorFactory = new DescriptorFactory(reporter);
		
		JvmClass jvmClass = new JvmClass(reporter, qName, backendOptions, descriptorFactory, 
				List.<JvmMemberFunction>of(), 
				List.<JvmMemberValue>of() 
				, List.<FunctionClass>of(functionClass)
				);
		Bytecode bc = jvmClass.createBytecode();
		InMemoryClassWriterListener.InMemoryClassLoader cl = InMemoryClassWriterListener.InMemoryClassLoader.create(reporter, getClass().getClassLoader(), List.of(bc));
		
		// -- Check result
		Class<?> cls = cl.loadClass("a.b.MainClass");
		assertThat(cls, notNullValue());

		Object mainclassInstance = cls.getDeclaredConstructor().newInstance();
		Method[] methods = mainclassInstance.getClass().getDeclaredMethods();
		
		Stream.of(methods).forEach(m -> {
			System.out.println("  Method: " + m.getName());
		});
		
		assertThat(ClassExecutorFunction.functionSName, is("$sir$execute$"));
		Method main = cls.getMethod(Constants.SIRIUS_EXECCLASS_EXEC_FUNC_NAME, new Class[] { /* String[].class */});
		
		Object[] argTypes = new Object[] {};
		
		// Call method
		Object result = main.invoke(mainclassInstance, argTypes /*, args*/);
		assertThat(result, instanceOf(sirius.lang.Integer.class));
		sirius.lang.Integer intResult = (sirius.lang.Integer)result;
		
//		System.out.println("result: " + result + " : " + result.getClass());
		System.out.println("result: " + intResult.getValue() + " : " + result.getClass());
		assertThat(intResult.getValue(), is(42));
		
	}
	
}
