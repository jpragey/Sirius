package org.sirius.backend.jvm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.common.error.AccumulatingReporter;
import org.sirius.common.error.Reporter;
import org.sirius.common.error.ShellReporter;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.api.VoidType;

public class DescriptorsFactoryTest {

	private Reporter reporter;
	private DescriptorFactory factory;

	@BeforeEach
	public void setup() {
		this.reporter = new AccumulatingReporter(new ShellReporter());
		this.factory = new DescriptorFactory(reporter);
	}
	
	@Test
	public void fieldDescriptor_forVoidType_isV() {
		VoidType voidType = mock(VoidType.class);
		assertEquals(factory.fieldDescriptor(voidType), "V");
	}

	@Test
	@DisplayName("field descriptor for field 'aa.bb.CC''")
	public void fieldDescriptor_forClassType_isLabc() {
		ClassType classTypeMock = mock(ClassType.class);
		when(classTypeMock.qName()).thenReturn(new QName("aa", "bb", "CC"));

		assertEquals(factory.fieldDescriptor(classTypeMock), "Laa/bb/CC;");
	}
	
	@Test
	@DisplayName("Method descriptor for function 'void()' (must be '()V')")
	public void methodDescriptors_noArg_returnVoid() {
		
		AbstractFunction func = mock(AbstractFunction.class);

		when(func.returnType()).thenReturn(Type.voidType);
		when(func.parameters()).thenReturn(List.of());
		
		assertEquals("()V", factory.methodDescriptor(func));
	}

	@Test
	@DisplayName("Method descriptor for function RetVal() ")
	public void methodDescriptors_noArg_returnAClass() {
		
		AbstractFunction func = mock(AbstractFunction.class);
		ClassType returnClassMock = mock(ClassType.class);
		when(returnClassMock.qName()).thenReturn(new QName("RetVal"));
		
		when(func.returnType()).thenReturn(returnClassMock);
		when(func.parameters()).thenReturn(List.<FunctionParameter>of());

		assertEquals("()LRetVal;", factory.methodDescriptor(func));
	}

	@Test
	@DisplayName("Method descriptor for function void(zz.Arg0 arg0, zz.Arg1 arg1) ")
	public void methodDescriptors_twoArgs_returnVoid() {
		
		AbstractFunction func = mock(AbstractFunction.class);
		
		when(func.returnType()).thenReturn(Type.voidType);
		
		// arg0 declaration : zz.Arg0 arg0
		FunctionParameter arg0 = mock(FunctionParameter.class);
		ClassType arg0ClassMock = mock(ClassType.class);
		when(arg0ClassMock.qName()).thenReturn(new QName("zz", "Arg0"));
		when(arg0.getType()).thenReturn(arg0ClassMock);
		
		// arg1 declaration : zz.Arg1 arg1
		FunctionParameter arg1 = mock(FunctionParameter.class);
		ClassType arg1ClassMock = mock(ClassType.class);
		when(arg1ClassMock.qName()).thenReturn(new QName("zz", "Arg1"));
		when(arg1.getType()).thenReturn(arg1ClassMock);
		
		when(func.parameters()).thenReturn(List.<FunctionParameter>of(arg0, arg1));
		assertEquals("(Lzz/Arg0;Lzz/Arg1;)V", factory.methodDescriptor(func));
	}

}
