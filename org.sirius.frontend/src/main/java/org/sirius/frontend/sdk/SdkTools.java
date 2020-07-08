package org.sirius.frontend.sdk;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.Session;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstClassOrInterface;
import org.sirius.frontend.ast.AstFunctionDeclarationBuilder;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.ast.PartialList;
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

	private AstModuleDeclaration sdkModule;

	public SdkTools(Reporter reporter, DefaultSymbolTable symbolTable) {
		super();
		this.reporter = reporter;

		this.sdkModule = parseSdk(symbolTable);
				
		this.sdkModule.getPackageDeclarations().forEach(pkg->{
			QName pkgQName = pkg.getQname();
			packagesMap.put(pkgQName, pkg);
		});
	}

	AstModuleDeclaration parseSdk(DefaultSymbolTable symbolTable) {
		
		List<Class<?>> sdkClasses = Sdk.sdkClasses();
		
		List<AstClassOrInterface> classOrInterfaces = new ArrayList<>();
		List<PartialList> allPartialLists = new ArrayList<>();
		for(Class<?> clss: sdkClasses) {
			
			TopLevelClass topLevelClassAnno = clss.getDeclaredAnnotation(TopLevelClass.class);	// can be null
			TopLevelMethods topLevelmethodsAnno = clss.getDeclaredAnnotation(TopLevelMethods.class);	// can be null
			
			if(topLevelClassAnno != null) {
				AstClassOrInterface classOrIntf =  parseClass(clss, topLevelClassAnno, symbolTable);
				classOrInterfaces.add(classOrIntf);
			}
			if(topLevelmethodsAnno != null) {
				List<PartialList> partialLists = parseTopLevel(clss, topLevelmethodsAnno, symbolTable);
				allPartialLists.addAll(partialLists);
//				System.out.println("- Top-level methods: " + clss + ", anno: " + topLevelmethodsAnno);
			}
		}
		List<AstClassDeclaration> classDeclarations = classOrInterfaces.stream()
				.filter(cl -> (cl instanceof AstClassDeclaration))
				.map(cl -> (AstClassDeclaration)cl)
				.collect(Collectors.toList());
		List<AstInterfaceDeclaration> interfaceDeclarations = classOrInterfaces.stream()
				.filter(cl -> (cl instanceof AstInterfaceDeclaration))
				.map(cl -> (AstInterfaceDeclaration)cl)
				.collect(Collectors.toList());
		
		AstPackageDeclaration pd = new AstPackageDeclaration(reporter, QName.empty, 
				allPartialLists,		//functionDeclarations, 
				classDeclarations, 
				interfaceDeclarations, 
				List.of()	//valueDeclarations
				);
		
		ModuleImportEquivalents equivalents = new ModuleImportEquivalents(); // TODO: check
		List<ModuleImport> moduleImports = Collections.emptyList();
		AstModuleDeclaration md = new AstModuleDeclaration(reporter, siriusLangQName, versionToken, equivalents, moduleImports, List.of(pd));
		
		QNameSetterVisitor qNameSetterVisitor = new QNameSetterVisitor();
		md.visit(qNameSetterVisitor);
		
		SymbolTableFillingVisitor fillingVisitor = new SymbolTableFillingVisitor(symbolTable);
		md.visit(fillingVisitor);
		
		SymbolResolutionVisitor resolutionVisitor = new SymbolResolutionVisitor(reporter, symbolTable);
		md.visit(resolutionVisitor);

		classDeclarations.forEach(cd -> {symbolTable.addClass(cd);});
		interfaceDeclarations.forEach(id-> {symbolTable.addInterface(id);});
		allPartialLists.forEach(pl ->  {symbolTable.addFunction(pl);});
		
		return md;
	}
	
	private AstClassOrInterface parseClass(Class<?> clss, TopLevelClass topLevelClassAnno, DefaultSymbolTable symbolTable) {
		String name = topLevelClassAnno.name();
		
		AstClassOrInterface classOrIntf;
		if(clss.isInterface()) {
			classOrIntf = new AstInterfaceDeclaration(reporter, AstToken.internal(name));
		} else {
			classOrIntf = AstClassDeclaration.newClass(reporter, AstToken.internal(name));
		}
		
		// -- ancestors/implemented interfaces
		Inherit [] annos = clss.getAnnotationsByType(Inherit.class);
		for(Inherit inherit: annos ) {
			QName pkg = QName.parseDotSeparated(inherit.packageQName());
			QName inheritName = pkg.child(inherit.name());
			classOrIntf.addAncestor(AstToken.internal(inheritName.getLast()) /*pkg, inherit.name()*/);	// TODO: WTF ???
		}

		return classOrIntf;
	}
	
	private List<PartialList> parseTopLevel(Class<?> clss, TopLevelMethods topLevelMethods, DefaultSymbolTable symbolTable) {
//		QName classPkgQName = new QName(topLevelMethods.packageQName().split("\\."));
//		if(!classPkgQName.equals(siriusLangQName)) {
//			reporter.error("SDK top-level " + clss.getName() + " refers to package " + classPkgQName + ", only root package " + siriusLangQName + " allowed yet.");
//			return List.of();
//		}
		
		List<PartialList> partialLists = new ArrayList<>();
		for(Method method: clss.getDeclaredMethods()) {
			
			SiriusMethod m = method.getDeclaredAnnotation(SiriusMethod.class);
			if(m != null ) {
				PartialList partialList = parseTopLevelFunction(method, m, /*classPkgQName, */symbolTable);
				partialLists.add(partialList);
			}
		}
		return partialLists;
	}
	
	private PartialList parseTopLevelFunction(Method method, SiriusMethod m, /*QName classPkgQName, */DefaultSymbolTable symbolTable) {
		
		String methodName = m.methodName();
		if(methodName.isEmpty())
			methodName = method.getName();

		AstType returnType = new AstVoidType();	// TODO
		AstFunctionDeclarationBuilder fd = new AstFunctionDeclarationBuilder(reporter, 
				new AnnotationList() ,	// TODO 
				AstToken.internal(methodName), 
				returnType,
//				classPkgQName,
				true /*TODO: concrete ???*/
				, (method.getModifiers() & Modifier.STATIC) != 0	// TODO: ???
				, Collections.emptyList()
				);

		// -- function arguments
		List<AstFunctionParameter> args = new ArrayList<>(method.getParameters().length);
		for(Parameter parameter: method.getParameters()) {
			org.sirius.sdk.tooling.Parameter anno = parameter.getAnnotation(org.sirius.sdk.tooling.Parameter.class);
			if(anno == null)
				continue;
			
			String name = parameter.getName();
			QNameRefType type = new QNameRefType(anno.typeQName());
			
			type.setSymbolTable(symbolTable);
			
			AstFunctionParameter arg = new AstFunctionParameter(type, AstToken.internal(name));
			arg.setSymbolTable(symbolTable);
			args.add(arg);
		}
		
		PartialList partialList = fd.withFunctionArguments(args);
		
		fd.assignSymbolTable(symbolTable);
		
		return partialList;
	}
}
