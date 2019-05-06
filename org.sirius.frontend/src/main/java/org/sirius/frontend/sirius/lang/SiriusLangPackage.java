package org.sirius.frontend.sirius.lang;

import java.util.Arrays;
import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstToken;

public class SiriusLangPackage {

	private Reporter reporter;

	private static List<String> inputFileNames = Arrays.asList(
			"Object.sirius",
			"Anything.sirius"
			);
	
	
	public SiriusLangPackage(Reporter reporter) {
		super();
		this.reporter = reporter;
	}
	
	public SiriusLangPackage() {
		super();
	}

	public static AstToken tempToken(String content) {
		return new AstToken(0,0,0,0, content, "<sirius.lang resources>");
	}


	public static List<String> getInputFileNames() {
		return inputFileNames;
	}
}
