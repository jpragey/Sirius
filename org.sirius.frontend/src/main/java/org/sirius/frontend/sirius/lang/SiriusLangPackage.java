package org.sirius.frontend.sirius.lang;

import java.util.Arrays;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.symbols.DefaultSymbolTable;

public class SiriusLangPackage { // TODO: remove, unused ???

	private Reporter reporter;

	private static List<String> inputFileNames = Arrays.asList(
			"Object.sirius",
			"Anything.sirius"
			);
	
	public static QName siriusLangQName = new QName("sirius", "lang");
	private final static AstToken versionToken = new AstToken(0,0,0,0,"1.0","");
//	public static QualifiedName siriusLangQName = new QualifiedName(Arrays.asList(
//			AstToken.internal("sirius", "<no source>"),
//			AstToken.internal("lang", "<no source>")
//			));

//	public static List<String> siriusLangNameAsList = siriusLangQName.getStringElements();

	private AstModuleDeclaration siriusLangModule;

	private AstPackageDeclaration siriusLangPackage;

	// -- 'Public' class 
	private AstClassDeclaration publicClassDeclaration;
	
	// -- 'public' annotation constructor
	private AstFunctionDeclarationBuilder publicAnnotationConstructor;
	
	// -- 'Integer' class 
	private AstClassDeclaration integerClassDeclaration;
	
	private AstClassDeclaration stringClassDeclaration;
	
	private AstClassDeclaration stringifiableClassDeclaration;
	
	public SiriusLangPackage(Reporter reporter, DefaultSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		

		this.siriusLangModule = new AstModuleDeclaration(reporter, siriusLangPackage.getQname(), versionToken);	// TODO: WTF ???

		this.siriusLangPackage = new AstPackageDeclaration(reporter, siriusLangQName);

		
	}


	public static AstToken tempToken(String content) {
		return new AstToken(0,0,0,0, content, "<sirius.lang resources>");
	}

	public static List<String> getInputFileNames() {
		return inputFileNames;
	}
	
}
