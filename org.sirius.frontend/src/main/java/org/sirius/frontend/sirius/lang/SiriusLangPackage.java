package org.sirius.frontend.sirius.lang;

import java.util.Arrays;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.ClassDeclaration;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.ModuleDeclaration;
import org.sirius.frontend.ast.PackageDeclaration;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.Type;
import org.sirius.frontend.symbols.GlobalSymbolTable;

public class SiriusLangPackage {

	private Reporter reporter;

	private static List<String> inputFileNames = Arrays.asList(
			"Object.sirius",
			"Anything.sirius"
			);
	
	public static QName siriusLangQName = new QName("sirius", "lang");
//	public static QualifiedName siriusLangQName = new QualifiedName(Arrays.asList(
//			AstToken.internal("sirius", "<no source>"),
//			AstToken.internal("lang", "<no source>")
//			));

//	public static List<String> siriusLangNameAsList = siriusLangQName.getStringElements();

	private ModuleDeclaration siriusLangModule;

	private PackageDeclaration siriusLangPackage;

	// -- 'Public' class 
	private ClassDeclaration publicClassDeclaration;
	
	// -- 'public' annotation constructor
	private FunctionDeclaration publicAnnotationConstructor;
	
	// -- 'Integer' class 
	private ClassDeclaration integerClassDeclaration;
	
	private ClassDeclaration stringClassDeclaration;
	
	private ClassDeclaration stringifiableClassDeclaration;
	
	public SiriusLangPackage(Reporter reporter, GlobalSymbolTable globalSymbolTable) {
		super();
		this.reporter = reporter;
		

		this.siriusLangModule = new ModuleDeclaration(reporter);

		this.siriusLangPackage = new PackageDeclaration(reporter, siriusLangQName);

		
	}


	public static AstToken tempToken(String content) {
		return new AstToken(0,0,0,0, content, "<sirius.lang resources>");
	}

	public static List<String> getInputFileNames() {
		return inputFileNames;
	}
	
}
