package org.sirius.frontend.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.Annotation;
import org.sirius.frontend.ast.AnnotationList;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.AstVoidType;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.QualifiedName;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.core.parser.FunctionDeclarationParser.FunctionParameterVisitor;
import org.sirius.frontend.core.parser.Parsers.FunctionParameterListVisitor;
//import org.sirius.frontend.core.parser.AnnotationListParser.AnnotationVisitor;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.AnnotationContext;
import org.sirius.frontend.parser.SiriusParser.AnnotationListContext;
import org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionDefinitionParameterListContext;
import org.sirius.frontend.parser.SiriusParser.QnameContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationListContext;


public record Parsers(Reporter reporter) {

	
	public static class QNameVisitor extends SiriusBaseVisitor<QualifiedName> {
		public QualifiedName visitQname(QnameContext ctx) 
		{
			List<AstToken> elements = ctx.LOWER_ID().stream()
					.map(termNode -> new AstToken(termNode.getSymbol()))
					.collect(Collectors.toList());
			
			QualifiedName qName = new QualifiedName(elements);
			return qName;
		};
	}

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

	public void f() {
		reporter.error("");
	}
	public class FunctionParameterListVisitor extends SiriusBaseVisitor< List<AstFunctionParameter> > {
		private Reporter reporter;
		
		public FunctionParameterListVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}
		
		@Override
		public List<AstFunctionParameter> visitFunctionDefinitionParameterList(FunctionDefinitionParameterListContext ctx) {
			FunctionParameterVisitor v = new FunctionParameterVisitor(reporter);
			
			List<AstFunctionParameter> functionParameters = 
			ctx.functionDefinitionParameter().stream()
				.map(formalArgCtx -> v.visit(formalArgCtx))
				.collect(Collectors.toList());
			
			int currentArgIndex = 0; // index in argument list
			for(var fp: functionParameters) {
				fp.setIndex(currentArgIndex++);
			}

			return functionParameters;
		}
	}

	public class FunctionDeclarationVisitor extends SiriusBaseVisitor<FunctionDeclaration> {
		private Reporter reporter;

		public FunctionDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		
		@Override
		public FunctionDeclaration visitFunctionDeclaration(FunctionDeclarationContext ctx) {
			
			// -- Annotation List
			AnnotationList annoList = new Parsers.AnnotationListVisitor().visit(ctx.annotationList());
			
			AstToken name = new AstToken(ctx.name);
			
			// -- Function parameters
			Parsers.FunctionParameterListVisitor argListVisitor = new FunctionParameterListVisitor(reporter);
			
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


}
