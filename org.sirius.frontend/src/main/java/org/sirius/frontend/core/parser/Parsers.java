package org.sirius.frontend.core.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.Annotation;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.ShebangDeclaration;
import org.sirius.frontend.core.parser.FunctionDeclarationParser.FunctionParameterVisitor;
import org.sirius.frontend.core.parser.ModuleDeclarationParser.PackageElements;
import org.sirius.frontend.core.parser.Parsers.NewModuleDeclaration;
import org.sirius.frontend.parser.Sirius.AnnotationContext;
import org.sirius.frontend.parser.Sirius.AnnotationListContext;
import org.sirius.frontend.parser.Sirius.FunctionDeclarationContext;
import org.sirius.frontend.parser.Sirius.FunctionDefinitionParameterListContext;
import org.sirius.frontend.parser.Sirius.ImplementedInterfacesContext;
import org.sirius.frontend.parser.Sirius.ModuleHeaderContext;
import org.sirius.frontend.parser.Sirius.NewCompilationUnitContext;
import org.sirius.frontend.parser.Sirius.NewModuleDeclarationContext;
import org.sirius.frontend.parser.Sirius.PackageDeclarationContext;
import org.sirius.frontend.parser.Sirius.PackageTopLevelDeclarationsContext;
import org.sirius.frontend.parser.Sirius.QnameContext;
import org.sirius.frontend.parser.Sirius.TypeContext;
//import org.sirius.frontend.core.parser.AnnotationListParser.AnnotationVisitor;
import org.sirius.frontend.parser.SiriusBaseVisitor;


public record Parsers(Reporter reporter, CommonTokenStream tokens) {

	/****************************************************************************/
	/** 							QName		 								*/
	/****************************************************************************/
	
	/** Qualified name qname with position information (Tokens) */
	public class QualifiedNameVisitor extends SiriusBaseVisitor<QualifiedName> {
		public QualifiedName visitQname(QnameContext ctx) 
		{
			List<AstToken> elements = ctx.LOWER_ID().stream()
					.map(termNode -> new AstToken(termNode.getSymbol()))
					.collect(Collectors.toList());
			
			QualifiedName qName = new QualifiedName(elements);
			return qName;
		};
	}
	
	/** Qualified name (raw, no source code position information ) */
	public static class QNameVisitor extends SiriusBaseVisitor<QName> {	// TODO: should have its own namespace
		public QName visitQname(QnameContext ctx) 
		{
			List<String> elements = ctx.LOWER_ID().stream()
					.map(termNode -> termNode.getSymbol().getText())
					.collect(Collectors.toList());

			QName qName = new QName(elements);
			return qName;
	};
}

	/****************************************************************************/
	/** 							Annotations 								*/
	/****************************************************************************/

	public static class AnnotationVisitor extends SiriusBaseVisitor<Annotation> {
		@Override
		public Annotation visitAnnotation(AnnotationContext ctx) {
			AstToken name = new AstToken(ctx.LOWER_ID().getSymbol());
			return new Annotation(name);
		}
	}

	public static class AnnotationListVisitor extends SiriusBaseVisitor<AnnotationList> {
		@Override
		public AnnotationList visitAnnotationList(AnnotationListContext ctx) {
			
			AnnotationVisitor visitor = new AnnotationVisitor();
			List<Annotation> annotations = ctx.annotation().stream()
					.map(annoCtxt -> annoCtxt.accept(visitor))
					.collect(Collectors.toList());
			
			return new AnnotationList(annotations);
		}
	}

	/****************************************************************************/
	/** 							Functions	 								*/
	/****************************************************************************/
	public class FunctionParameterListVisitor extends SiriusBaseVisitor< List<AstFunctionParameter> > {
		
		@Override
		public List<AstFunctionParameter> visitFunctionDefinitionParameterList(FunctionDefinitionParameterListContext ctx) {
			FunctionParameterVisitor fpVisitor = new FunctionParameterVisitor(reporter);

			List<AstFunctionParameter> functionParameters = 
					ctx.functionDefinitionParameter().stream()
					.map(fpVisitor::visit)
					.collect(Collectors.toList());
			
			int currentArgIndex = 0; // index in argument list
			for(var fp: functionParameters) {
				fp.setIndex(currentArgIndex++);
			}

			return functionParameters;
		}
	}

	public class FunctionDeclarationVisitor extends SiriusBaseVisitor<FunctionDeclaration> {

		@Override
		public FunctionDeclaration visitFunctionDeclaration(FunctionDeclarationContext ctx) {
			
			// -- Annotation List
			AnnotationList annoList = new Parsers.AnnotationListVisitor().visit(ctx.annotationList());
			
			AstToken name = new AstToken(ctx.name);
			
			// -- Function parameters
			Parsers.FunctionParameterListVisitor argListVisitor = new FunctionParameterListVisitor();
			
			List<AstFunctionParameter> functionParams = argListVisitor.visit(ctx.functionDefinitionParameterList());
			
			// -- Return type
			TypeParser.TypeVisitor typeVisitor = new TypeParser.TypeVisitor(reporter);
			
			TypeContext returnContext = ctx.returnType;
			AstType returnType =  (returnContext == null) ?
				AstVoidType.instance :
				typeVisitor.visit(returnContext);
			
			boolean member = false; 
			
			return new FunctionDeclaration(annoList, functionParams, returnType, member, name) ;
		}
	}

	/****************************************************************************/
	/** 							Class/interface aux							*/
	/****************************************************************************/
	public class ImplementClauseVisitor extends SiriusBaseVisitor<List<AstToken>> {
		@Override
		public List<AstToken> visitImplementedInterfaces(ImplementedInterfacesContext ctx) {
			List<AstToken> interfaceNames = ctx.TYPE_ID().stream().map(termNode -> new AstToken(termNode.getSymbol())).toList();
			return interfaceNames;
		}
	}

	
	public class PackageDeclarationVisitor extends SiriusBaseVisitor<AstPackageDeclaration> {

		@Override
		public AstPackageDeclaration visitPackageDeclaration(PackageDeclarationContext ctx) {
			
			QNameVisitor visitor = new QNameVisitor();
			QName packageQName = ctx.qname().accept(visitor);
			
			List<AstPackageDeclaration> packageDeclarations = new ArrayList<>();
//			List<AstInterfaceDeclaration> interfaceDeclarations = new ArrayList<>();
//			List<AstClassDeclaration> classDeclarations = new ArrayList<>();
//			List<PartialList> partialLists = new ArrayList<>();
			
			PackageElements packageElements = new PackageElements();

			ModuleDeclarationParser.PackageElementVisitor mcVisitor = new ModuleDeclarationParser(reporter, tokens) .new PackageElementVisitor(//reporter, 
					packageDeclarations, packageElements /*interfaceDeclarations, classDeclarations, partialLists*/);
//			ctx.packageElement().forEach(mcContext -> mcContext.accept(mcVisitor));
			ctx.packageElement().forEach(mcContext -> mcVisitor.visit(mcContext));
			
			
			return new AstPackageDeclaration(reporter, packageQName,
					packageElements.functiondefinitions, 
					packageElements.classDeclarations, 
					packageElements.interfaceDeclarations, 
					List.of()// <AstMemberValueDeclaration> valueDeclarations
					);
		}
	}

	/****************************************************************************/
	/** 							New CompilationUnit							*/
	/****************************************************************************/

	public static record ModuleHeader(QName qname) {}
	public static record NewModuleDeclaration(ModuleHeader moduleHeader) {}
	public static record CompilationUnit(Optional<ShebangDeclaration> shebangDeclaration, List<ModuleHeader> modules) {}

//	public static SiriusBaseVisitor<ModuleHeader> moduleHeaderVisitor = new SiriusBaseVisitor<ModuleHeader>() {
	public static class ModuleHeaderVisitor extends SiriusBaseVisitor<ModuleHeader> {

		@Override
		public ModuleHeader visitModuleHeader(ModuleHeaderContext ctx) {
			Parsers.QNameVisitor qnameVisitor = new Parsers.QNameVisitor();
			QName qname = qnameVisitor.visitQname(ctx.qname());
			
			ModuleHeader mh = new ModuleHeader(qname);
			return mh;
		}
		public ModuleHeader visitNewModuleDeclaration(org.sirius.frontend.parser.Sirius.NewModuleDeclarationContext ctx) {
			return visitModuleHeader(ctx.moduleHeader());
		};
		
	};
	
	public static class CompilationUnitVisitor extends SiriusBaseVisitor<CompilationUnit> {
//		public static SiriusBaseVisitor<CompilationUnit> compilationUnitVisitor = new SiriusBaseVisitor<CompilationUnit>() {
		ModuleHeaderVisitor moduleHeaderVisitor = new ModuleHeaderVisitor();
		
		public CompilationUnit visitNewCompilationUnit(NewCompilationUnitContext ctx) {

			SiriusBaseVisitor<ShebangDeclaration> sbv = new ShebangDeclarationParser.ShebangVisitor();
			
			List<ModuleHeader> mods  = ctx.newModuleDeclaration().stream()
					.map((NewModuleDeclarationContext nmdCtx) -> moduleHeaderVisitor.visitNewModuleDeclaration(nmdCtx))
					.collect(Collectors.toList());
			
			
			Optional<ShebangDeclaration> shebangDeclaration = Optional.ofNullable(
					ctx.shebangDeclaration()).map(sbv::visitShebangDeclaration);
			
			return new CompilationUnit(shebangDeclaration, mods);
			
		};
	};

}
