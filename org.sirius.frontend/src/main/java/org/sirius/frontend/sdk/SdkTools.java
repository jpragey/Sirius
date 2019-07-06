package org.sirius.frontend.sdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstFunctionFormalArgument;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.symbols.GlobalSymbolTable;
import org.sirius.sdk.tooling.Inherit;
import org.sirius.sdk.tooling.Sdk;
import org.sirius.sdk.tooling.SiriusMethod;
import org.sirius.sdk.tooling.TopLevelClass;
import org.sirius.sdk.tooling.TopLevelMethods;

public class SdkTools {
	private Reporter reporter;

	// SDK packages, by QNames
	private Map<QName, AstPackageDeclaration> packagesMap = new HashMap<QName, AstPackageDeclaration>();

	private QName packageQName = new QName("sirius", "lang"); 
	private AstPackageDeclaration rootPd = new AstPackageDeclaration(reporter, packageQName);

	/** Class containing top-level functions */
	private AstClassDeclaration topLevelClass;

	
	public AstClassDeclaration getTopLevelClass() {
		return topLevelClass;
	}

	public SdkTools(Reporter reporter) {
		super();
		this.reporter = reporter;
		
		this.topLevelClass = createClassInPackage(reporter, rootPd, "$package$");
		
		packagesMap.put(packageQName, rootPd);
	}

	private static AstClassDeclaration createClassInPackage(Reporter reporter, AstPackageDeclaration pkg, String name) {
		AstClassDeclaration cd = new AstClassDeclaration(reporter, false/*interfaceType*/, AstToken.internal(name));
		cd.setPackageDeclaration(pkg);
		return cd;
		
	}
	
	public void parseSdk(GlobalSymbolTable symbolTable) {
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
	}
	
	private void parseClass(Class<?> clss, TopLevelClass topLevelClassAnno, GlobalSymbolTable symbolTable) {
//		AstToken name = AstToken.internal(topLevelClassAnno.name());
		String name = topLevelClassAnno.name();
		
//		AstClassDeclaration cd = new AstClassDeclaration(reporter, false/*interfaceType*/, name);
//		AstClassDeclaration cd = createClassInPackage(reporter, rootPd, "$package$");
		
		String pkgQName = topLevelClassAnno.packageQName();
		String[] pkgNameElements = pkgQName.split("\\.");
		QName classPkgQName = new QName(pkgNameElements);
		AstPackageDeclaration pd = packagesMap.get(classPkgQName);
		if(pd == null) {
			reporter.error("SDK class/interface " + clss + " doesn't belong to the language SDK package " +
					packageQName.dotSeparated() + " (found : " + classPkgQName + ")");
			
			pd = rootPd;	// Just to allow compilation to keep on  
		}

		AstClassDeclaration cd = createClassInPackage(reporter, pd, name);
//		cd.setPackageDeclaration(pd);

		symbolTable.addClass(cd);
		symbolTable.addClass(cd);	// TODO: check correctness (sirius.lang content is known by default)
		
		// -- ancestors/implemented interfaces
		Inherit [] annos = clss.getAnnotationsByType(Inherit.class);
		for(Inherit inherit: annos ) {
			QName pkg = QName.parseDotSeparated(inherit.packageQName());
			cd.addAncestor(pkg, inherit.name());
		}
		
		System.out.println("Anno: " + annos);
		
	}
	
	private void parseTopLevel(Class<?> clss, TopLevelMethods topLevelMethods, GlobalSymbolTable symbolTable) {
		QName classPkgQName = new QName(topLevelMethods.packageQName().split("\\."));
		if(!classPkgQName.equals(packageQName)) {
			reporter.error("SDK top-level " + clss.getName() + " refers to package " + classPkgQName + ", only root package " + packageQName + " allowed yet.");
			return;
		}
		
		for(Method method: clss.getDeclaredMethods()) {
			parseTopLevelFunction(method, classPkgQName, symbolTable);
//			SiriusMethod m = method.getDeclaredAnnotation(SiriusMethod.class);
//			if(m != null) {
////				String methodName = m.methodName();
////				if(methodName.isEmpty())
////					methodName = method.getName();
////
////				AstType returnType = new AstVoidType();	// TODO
////				AstFunctionDeclaration fd = new AstFunctionDeclaration(reporter, 
////						new AnnotationList() ,	// TODO 
////						AstToken.internal(methodName), 
////						returnType);
////				this.topLevelClass.addFunctionDeclaration(fd);
////				
////				fd.setContainerQName(classPkgQName);
////				
////				symbolTable.addFunction(classPkgQName, fd);
//			}
			
		}
	}
	
	private void parseTopLevelFunction(Method method, QName classPkgQName, GlobalSymbolTable symbolTable) {
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
		this.topLevelClass.addFunctionDeclaration(fd);
		
		fd.setContainerQName(classPkgQName);

		// -- function arguments
		for(Parameter parameter: method.getParameters()) {
			org.sirius.sdk.tooling.Parameter anno = parameter.getAnnotation(org.sirius.sdk.tooling.Parameter.class);
			if(anno == null)
				continue;
			
			String name = parameter.getName();
			SimpleType type = new SimpleType(AstToken.internal(anno.typeQName()));
			AstFunctionFormalArgument arg = new AstFunctionFormalArgument(type, AstToken.internal(name));
			fd.addFormalArgument(arg);
		}
		
		symbolTable.addFunction(fd);
		
	}
	
	
	
}
