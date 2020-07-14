package org.sirius.frontend.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class PartialTest {

	
	@Test
	public void partialToStringTest() {
		AstFunctionParameter par0 = new AstFunctionParameter(AstType.noType, AstToken.internal("param 0"));
		AstFunctionParameter par1 = new AstFunctionParameter(AstType.noType, AstToken.internal("param 1"));
		Partial partial = new Partial (
				AstToken.internal("partial") ,
////				List<Capture> captures,
				List.of(),
				Arrays.asList(par0, par1), 
//				AstFunctionDeclarationBuilder function,
				
//				false, //boolean concrete,
				false, //boolean member,
				new QName(""),

				AstType.noType,// returnType,
				Optional.empty() //List<AstStatement> statements/*,
				);
		
		String s = partial.toString();
		

	}
	
	
}
