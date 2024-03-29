package org.sirius.frontend.sdk;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.StdAstTransforms;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstClassOrInterface;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.ImportDeclaration;
import org.sirius.frontend.ast.ModuleImport;
import org.sirius.frontend.ast.ModuleImportEquivalents;
import org.sirius.frontend.ast.QNameRefType;
import org.sirius.frontend.ast.ScriptCompilationUnit;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.symbols.Scope;
import org.sirius.frontend.symbols.SymbolTableImpl;
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
	private SdkContent sdkContent;
	private Scope scope;	// TODO: move to SDKContent

	public SdkTools(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.scope = new Scope("java.lang(SDK)");

		this.sdkModule = parseSdk(scope);
				
		this.sdkModule.getPackageDeclarations().forEach(pkg->{
			Optional<QName> pkgQName = pkg.getQname();
			assert(pkgQName.isPresent());	// all SDK modules must be named
			packagesMap.put(pkgQName.get(), pkg);
		});
		this.sdkContent = new SdkContent(this.sdkModule);
	}

	public Scope getScope() {
		return scope;
	}

	private AstModuleDeclaration parseSdk(Scope scope) {
		
		List<Class<?>> sdkClasses = Sdk.sdkClasses();
		
		List<AstClassOrInterface> classOrInterfaces = new ArrayList<>();
		List<FunctionDefinition> allFunctionDefs = new ArrayList<>();
		SymbolTableImpl symbolTable = scope.getSymbolTable();
		List<AstClassDeclaration> classDeclarations = new ArrayList<>();
		
		for(Class<?> clss: sdkClasses) {
			
			TopLevelClass topLevelClassAnno = clss.getDeclaredAnnotation(TopLevelClass.class);	// can be null
			TopLevelMethods topLevelmethodsAnno = clss.getDeclaredAnnotation(TopLevelMethods.class);	// can be null
			
			if(topLevelClassAnno != null) {
				AstClassOrInterface classOrIntf =  parseClass(clss, topLevelClassAnno, symbolTable);
				classOrInterfaces.add(classOrIntf);
				if(classOrIntf instanceof AstClassDeclaration)
					classDeclarations.add((AstClassDeclaration)classOrIntf);
			}
			if(topLevelmethodsAnno != null) {
				List<FunctionDefinition> partialLists = parseTopLevel(clss, topLevelmethodsAnno, scope.getSymbolTable() /* symbolTable*/);
				allFunctionDefs.addAll(partialLists);
			}
		}
		
		AstPackageDeclaration pd = new AstPackageDeclaration(reporter, 
				Optional.of(siriusLangQName), 
				allFunctionDefs,		//functionDeclarations, 
				classDeclarations, 
				List.<AstMemberValueDeclaration>of()	//valueDeclarations
				);
		
		ModuleImportEquivalents equivalents = new ModuleImportEquivalents(); // TODO: check
		List<ModuleImport> moduleImports = Collections.emptyList();
		AstModuleDeclaration md = new AstModuleDeclaration(reporter, Optional.of(siriusLangQName), versionToken, equivalents, moduleImports, List.of(pd)
				, List.<AstToken>of(/*TODO: ??? module comments expected*/));

		ScriptCompilationUnit compilationUnit = new ScriptCompilationUnit(
				reporter, 
				scope, 
				Optional.<ShebangDeclaration>empty(),
				List.<ImportDeclaration>of(),
				List.<AstModuleDeclaration>of(md));

		// -- Transforms
		StdAstTransforms.setQNames(compilationUnit);

		// -- Set scopes
		StdAstTransforms.setScopes(compilationUnit, scope);

		StdAstTransforms.fillSymbolTables(compilationUnit, scope);
		
		classDeclarations.forEach(cd -> {scope.getSymbolTable().addClass(cd);});

		allFunctionDefs.forEach(pl ->  {scope.getSymbolTable().addFunction(pl);});
		
		return md;
	}
	
	private AstClassOrInterface parseClass(Class<?> clss, TopLevelClass topLevelClassAnno, SymbolTableImpl symbolTable) {
		String name = topLevelClassAnno.name();
		
		AstClassOrInterface classOrIntf;
		classOrIntf = new AstClassDeclaration(reporter, AstToken.internal(name));
		
		// -- ancestors/implemented interfaces
		Inherit [] annos = clss.getAnnotationsByType(Inherit.class);
		for(Inherit inherit: annos ) {
			QName pkg = QName.parseAndValidate(inherit.packageQName(), reporter).get() /* TODO: check */;
			
			QName inheritName = pkg.child(inherit.name());
			classOrIntf.addAncestor(AstToken.internal(inheritName.getLast()) /*pkg, inherit.name()*/);	// TODO: WTF ???
		}

		return classOrIntf;
	}
	
	private List<FunctionDefinition> parseTopLevel(Class<?> clss, TopLevelMethods topLevelMethods, SymbolTableImpl symbolTable) {
		
		List<FunctionDefinition> functionDefinitions = new ArrayList<>();
		for(Method method: clss.getDeclaredMethods()) {
			
			SiriusMethod m = method.getDeclaredAnnotation(SiriusMethod.class);
			if(m != null ) {
				FunctionDefinition functionDefinition = parseTopLevelFunction(method, m, symbolTable);
				functionDefinitions.add(functionDefinition);
			}
		}
		return functionDefinitions;
	}
	
	private FunctionDefinition parseTopLevelFunction(Method method, SiriusMethod m, SymbolTableImpl symbolTable) {
		
		String methodName = m.methodName();
		if(methodName.isEmpty())
			methodName = method.getName();

		AstType returnType = new AstVoidType();	// TODO
		
		// -- function arguments
		List<AstFunctionParameter> args = new ArrayList<>(method.getParameters().length);
		int paramIndex = 0;
		for(Parameter parameter: method.getParameters()) {
			org.sirius.sdk.tooling.Parameter anno = parameter.getAnnotation(org.sirius.sdk.tooling.Parameter.class);
			if(anno == null)
				continue;
			
			String name = parameter.getName();
			QNameRefType type = new QNameRefType(anno.typeQName(), reporter);
			
			type.setSymbolTable(symbolTable);
			
			AstFunctionParameter arg = new AstFunctionParameter(type, AstToken.internal(name));
			arg.setIndex(paramIndex++);
			arg.setSymbolTable(symbolTable);
			args.add(arg);
		}
		
		AnnotationList annotationList = new AnnotationList();	// TODO 
		boolean member = !annotationList.contains("static");
		
		// TODO: -> FunctionDeclaration ???
		FunctionDefinition partialfunctionDefinitionList = new FunctionDefinition(annotationList, args, returnType, member, AstToken.internal(methodName), Collections.emptyList() /*body statements*/); 
		
		return partialfunctionDefinitionList;
	}

	public SdkContent getSdkContent() {
		return sdkContent;
	}
	
}
