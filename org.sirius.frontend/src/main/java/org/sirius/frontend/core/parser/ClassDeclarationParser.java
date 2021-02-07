package org.sirius.frontend.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstClassOrInterface;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationListContext;

import com.google.common.collect.ImmutableList;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ClassDeclarationParser {
	private Reporter reporter;
	
	public ClassDeclarationParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class ClassDeclarationVisitor extends SiriusBaseVisitor<AstClassDeclaration> {
		private Reporter reporter;
//		private QName containerQName0;

		public ClassDeclarationVisitor(Reporter reporter/*, QName containerQName*/) {
			super();
			this.reporter = reporter;
//			this.containerQName = containerQName;
		}

		@Override
		public AstClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.TYPE_ID(0).getSymbol());
			
			// -- Constructor arguments
			FunctionDeclarationParser.FunctionParameterListVisitor parameterVisitor = new FunctionDeclarationParser.FunctionParameterListVisitor(reporter);
			List<AstFunctionParameter> anonConstructorArguments = parameterVisitor.visitFunctionParameterList(ctx.functionParameterList());
			
			// -- Implemented interfaces
			List<AstClassOrInterface.AncestorInfo> ancestors = ctx.TYPE_ID().stream()
				.skip(1)
				.map(terminalNode -> new AstClassOrInterface.AncestorInfo(new AstToken(terminalNode.getSymbol())))
				.collect(Collectors.toList());
			
			// -- type parameters
			FunctionDeclarationParser.TypeParameterListVisitor typeParameterListVisitor = new FunctionDeclarationParser.TypeParameterListVisitor(reporter);
			
			TypeParameterDeclarationListContext c =  ctx.typeParameterDeclarationList();
			List<TypeParameter> typeParameters = // TODO: Optional ???
					(c == null) 
					? Collections.emptyList() 
					: typeParameterListVisitor.visit(c);
			
			// -- Member functions
//			QName containerQName = new QName("TODO");	// TODO
			FunctionDeclarationParser.FunctionDefinitionVisitor fctVisitor = new FunctionDeclarationParser.FunctionDefinitionVisitor(reporter);
			List<FunctionDefinition> methods = ctx.children.stream()
				.map(parseTree -> parseTree.accept(fctVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			MemberValueDeclarationParser.MemberValueVisitor memberValuesVisitor = new MemberValueDeclarationParser.MemberValueVisitor(reporter);
			List<AstMemberValueDeclaration> memberValues = ctx.children.stream()
				.map(parseTree -> parseTree.accept(memberValuesVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
//			boolean interfaceType = false;
			
//			QName packageQName  = containerQName;
			
			AstClassDeclaration classDeclaration = new AstClassDeclaration(
					reporter, 
//					interfaceType, 
					name, 
//					packageQName,
					ImmutableList.copyOf(typeParameters),
					ImmutableList.copyOf(methods),
					memberValues,
					anonConstructorArguments,
					ancestors
					);

			return classDeclaration;
		}
	}

}
