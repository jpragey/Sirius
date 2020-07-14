package org.sirius.backend.jvm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sirius.backend.jvm.mocktypes.MockAbstractFunction;
import org.sirius.backend.jvm.mocktypes.MockClassType;
import org.sirius.backend.jvm.mocktypes.MockFunctionFormalArgument;
import org.sirius.backend.jvm.mocktypes.MockVoidType;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;

public class DescriptorsFactoryTest {

	private Reporter reporter;
	private DescriptorFactory factory;
	
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
