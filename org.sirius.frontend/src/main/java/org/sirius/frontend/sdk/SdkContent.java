package org.sirius.frontend.sdk;

import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;

public class SdkContent {

	public static QName siriusLangQName = new QName("sirius", "lang"); 
	public static QName siriusLangIntegerQName = siriusLangQName.child("Integer"); 
	public static QName siriusLangBooleanQName = siriusLangQName.child("Boolean"); 
	public static QName siriusLangFloatQName = siriusLangQName.child("Float"); 
	public static QName siriusLangStringQName = siriusLangQName.child("String"); 

	
	private AstClassDeclaration siriusLangIntegerASTCD;
	private AstClassDeclaration siriusLangBooleanASTCD;
	private AstClassDeclaration siriusLangFloatASTCD;
	private AstClassDeclaration siriusLangStringASTCD;

	public SdkContent(AstModuleDeclaration sdkModule) {
		super();

//		Optional<AstPackageDeclaration> optPd = sdkModule.getPackageDeclarations().stream()
//				.filter(astPd -> siriusLangQName.equals(astPd.getQname()))
//				.findAny();
//		assert(optPd.isPresent());
//		AstPackageDeclaration pd =  optPd.get();
//		AstPackageDeclaration pd =  sdkModule.getPackageDeclaration(siriusLangQName);
//
//		
//		this.siriusLangIntegerASTCD = pd.getClassDeclaration(siriusLangIntegerQName);
//		this.siriusLangBooleanASTCD = pd.getClassDeclaration(siriusLangBooleanQName);
//		this.siriusLangFloatASTCD = pd.getClassDeclaration(siriusLangFloatQName);
//
//		this.siriusLangStringASTCD = pd.getClassDeclaration(siriusLangStringQName);
	}

	public AstClassDeclaration getSiriusLangIntegerASTCD() {
		return siriusLangIntegerASTCD;
	}

	public AstClassDeclaration getSiriusLangStringASTCD() {
		return siriusLangStringASTCD;
	}

	public AstClassDeclaration getSiriusLangBooleanASTCD() {
		return siriusLangBooleanASTCD;
	}

	public AstClassDeclaration getSiriusLangFloatASTCD() {
		return siriusLangFloatASTCD;
	}
	
}
