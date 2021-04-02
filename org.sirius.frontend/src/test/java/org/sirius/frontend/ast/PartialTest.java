package org.sirius.frontend.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.SymbolTableImpl;

public class PartialTest {

	
	@Test
	public void partialToStringTest() {
		AstFunctionArgument par0 = new AstFunctionArgument(AstType.noType, AstToken.internal("param 0"));
		AstFunctionArgument par1 = new AstFunctionArgument(AstType.noType, AstToken.internal("param 1"));
		Partial partial = new Partial (
				AstToken.internal("partial") ,
				Arrays.asList(par0, par1), 
				false, //boolean member,
				AstType.noType,// returnType,
				Collections.emptyList() // body 
				);
		
		String s = partial.toString();
	}
	
}
