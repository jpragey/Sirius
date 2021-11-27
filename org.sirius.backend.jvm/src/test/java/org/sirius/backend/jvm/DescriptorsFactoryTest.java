package org.sirius.backend.jvm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.mocktypes.MockAbstractFunction;
import org.sirius.backend.jvm.mocktypes.MockFunctionFormalArgument;
import org.sirius.backend.jvm.mocktypes.MockVoidType;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.MemberValue;

public class DescriptorsFactoryTest {

	private Reporter reporter;
	private DescriptorFactory factory;

	
	private class MockClassType implements ClassType {

		private QName qname;
		
		public MockClassType(QName qname) {
			super();
			this.qname = qname;
		}

		@Override
		public QName qName() {
			return qname;
		}
//		@Override
//		public boolean isAncestorOrSame(Type type) {
//			throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
//		}

		@Override
		public List<MemberValue> memberValues() {
			return List.of();
		}

		@Override
		public List<AbstractFunction> memberFunctions() {
			return List.of();
		}
		@Override
		public Optional<ExecutionEnvironment> executionEnvironment() {
			return Optional.empty();
		}

	}

	
	
	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		this.factory = new DescriptorFactory(reporter);
	}
	
	@Test
	public void simpleTypeDescriptorAreCorrect() {
		assertEquals(factory.fieldDescriptor(new MockVoidType()), "V");
		assertEquals(factory.fieldDescriptor(new MockClassType(new QName("aa", "bb", "CC"))), "Laa/bb/CC;");
	}
	
	@Test
	public void methodDescriptorsAreCorrect() {
		QName funcQName = new QName("aa", "bb", "aFunc"); 
		MockAbstractFunction func = new MockAbstractFunction(
				funcQName, 
				new MockClassType(new QName("RetVal")));
		
		func.getArguments().add(new MockFunctionFormalArgument(funcQName.child("arg0"), new MockClassType(new QName("zz", "Arg0"))));
		func.getArguments().add(new MockFunctionFormalArgument(funcQName.child("arg1"), new MockClassType(new QName("zz", "Arg1"))));
		
		assertEquals(factory.methodDescriptor(func), "(Lzz/Arg0;Lzz/Arg1;)LRetVal;");
	}
}
