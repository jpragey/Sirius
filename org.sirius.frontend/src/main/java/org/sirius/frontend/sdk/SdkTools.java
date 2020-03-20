package org.sirius.frontend.sdk;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.QNameRefType;
import org.sirius.frontend.symbols.DefaultSymbolTable;
import org.sirius.frontend.symbols.QNameSetterVisitor;
import org.sirius.frontend.symbols.SymbolResolutionVisitor;
import org.sirius.frontend.symbols.SymbolTableFillingVisitor;
import org.sirius.sdk.tooling.Inherit;
import org.sirius.sdk.tooling.Sdk;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;
import org.sirius.sdk.tooling.TopLevelMethods;

public class SdkTools {
	private Reporter reporter;

	// SDK packages, by QNames
	private Map<QName, AstPackageDeclaration> packagesMap = new HashMap<QName, AstPackageDeclaration>();

	private QName siriusLangQName = new QName("sirius", "lang"); 
	private final static AstToken versionToken = new AstToken(0,0,0,0,"1.0","");
//	private AstPackageDeclaration rootPd0 = new AstPackageDeclaration(reporter, siriusLangQName);

	/** Class containing top-level functions (named $package$ )*/
	private AstClassDeclaration topLevelClass;

	private AstModuleDeclaration sdkModule;
	private AstPackageDeclaration langPackage;	// sirius.lang

	
	public AstClassDeclaration getTopLevelClass() {
		return topLevelClass;
	}

	public SdkTools(Reporter reporter) {
		super();
		this.reporter = reporter;
		
		this.langPackage = new AstPackageDeclaration(reporter, siriusLangQName);
		this.topLevelClass = createClassInPackage(reporter, this.langPackage, "$package$");
		
		packagesMap.put(siriusLangQName, langPackage);
		
		this.sdkModule = new AstModuleDeclaration(reporter, siriusLangQName, versionToken);
		sdkModule.addPackageDeclaration(langPackage);
	}

	private static AstClassDeclaration createClassInPackage(Reporter reporter, AstPackageDeclaration pkg, String name) {
		AstClassDeclaration cd = new AstClassDeclaration(reporter, false/*interfaceType*/, AstToken.internal(name), Optional.of(pkg.getQname()));
//		cd.setPackageQName(pkg.getQname());
		return cd;
		
	}
	
	public void parseSdk(DefaultSymbolTable symbolTable) {
		
		List<Class<?>> sdkClasses = Sdk.sdkClasses();
		
		for(Class<?> clss: sdkClasses) {
			
			TopLevelClass topLevelClassAnno = clss.getDeclaredAnnotation(TopLevelClass.class);	// can be null
			TopLevelMethods topLevelmethodsAnno = clss.getDeclaredAnnotation(TopLevelMethods.class);	// can be null
			
			if(topLevelClassAnno != null) {
				parseClass(clss, topLevelClassAnno, symbolTable);
			}
			if(topLevelmethodsAnno != null) {
				parseTopLevel(clss, topLevelmethodsAnno, symbolTable);
//				System.out.println("- Top-level methods: " + clss + ", anno: " + topLevelmethodsAnno);
			}
		}
		
		this.sdkModule.visit(new QNameSetterVisitor());
		this.sdkModule.visit(new SymbolTableFillingVisitor(symbolTable));
		this.sdkModule.visit(new SymbolResolutionVisitor(reporter, symbolTable));
		
	}
	
	private void parseClass(Class<?> clss, TopLevelClass topLevelClassAnno, DefaultSymbolTable symbolTable) {
		String name = topLevelClassAnno.name();
		
		
		String pkgQName = topLevelClassAnno.packageQName();
		String[] pkgNameElements = pkgQName.split("\\.");
		QName classPkgQName = new QName(pkgNameElements);
		AstPackageDeclaration pd = packagesMap.get(classPkgQName);
		if(pd == null) {
			reporter.error("SDK class/interface " + clss + " doesn't belong to the language SDK package " +
					siriusLangQName.dotSeparated() + " (found : " + classPkgQName + ")");
			
			pd = langPackage;	// Just to allow compilation to keep on  
		}

		AstClassDeclaration cd = createClassInPackage(reporter, pd, name);
		
		symbolTable.addClass(cd);	// TODO: check correctness (sirius.lang content is known by default)

		
		// -- ancestors/implemented interfaces
		Inherit [] annos = clss.getAnnotationsByType(Inherit.class);
		for(Inherit inherit: annos ) {
			QName pkg = QName.parseDotSeparated(inherit.packageQName());
			cd.addAncestor(pkg, inherit.name());
		}

		this.langPackage.addClassDeclaration(cd);;

	}
	
	private void parseTopLevel(Class<?> clss, TopLevelMethods topLevelMethods, DefaultSymbolTable symbolTable) {
		QName classPkgQName = new QName(topLevelMethods.packageQName().split("\\."));
		if(!classPkgQName.equals(siriusLangQName)) {
			reporter.error("SDK top-level " + clss.getName() + " refers to package " + classPkgQName + ", only root package " + siriusLangQName + " allowed yet.");
			return;
		}
		
		for(Method method: clss.getDeclaredMethods()) {
			parseTopLevelFunction(method, classPkgQName, symbolTable);
		}
	}
	
	private void parseTopLevelFunction(Method method, QName classPkgQName, DefaultSymbolTable symbolTable) {
		SiriusMethod m = method.getDeclaredAnnotation(SiriusMethod.class);
		if(m == null) {
			return;
		}
		
		String methodName = m.methodName();
		if(methodName.isEmpty())
			methodName = method.getName();

		AstType returnType = new AstVoidType();	// TODO
		AstFunctionDeclaration fd = new AstFunctionDeclaration(reporter, 
				new AnnotationList() ,	// TODO 
				AstToken.internal(methodName), 
				returnType);
		fd.setContainerQName(classPkgQName);
		this.topLevelClass.addFunctionDeclaration(fd);
		
//		fd.setContainerQName(classPkgQName);

		// -- function arguments
		for(Parameter parameter: method.getParameters()) {
			org.sirius.sdk.tooling.Parameter anno = parameter.getAnnotation(org.sirius.sdk.tooling.Parameter.class);
			if(anno == null)
				continue;
			
			String name = parameter.getName();
//			SimpleType type = new SimpleType(AstToken.internal(anno.typeQName()));
			QNameRefType type = new QNameRefType(anno.typeQName());
			
			type.setSymbolTable(symbolTable);
			
			AstFunctionFormalArgument arg = new AstFunctionFormalArgument(type, AstToken.internal(name));
			arg.setSymbolTable(symbolTable);
			fd.addFormalArgument(arg);
		}
		
		symbolTable.addFunction(fd);
		fd.assignSymbolTable(symbolTable);
		
		this.langPackage.addFunctionDeclaration(fd);
	}
	
}
