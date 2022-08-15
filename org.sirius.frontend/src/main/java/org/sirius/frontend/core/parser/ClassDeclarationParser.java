package org.sirius.frontend.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationListContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ClassDeclarationParser {
	private Reporter reporter;
	private Parsers parsers;

	public ClassDeclarationParser(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.parsers = new Parsers(reporter);
	}

	public class ClassDeclarationVisitor extends SiriusBaseVisitor<AstClassDeclaration> {
		private Reporter reporter;

		public ClassDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.TYPE_ID(0).getSymbol());
			
			// -- Constructor arguments
			Parsers.FunctionParameterListVisitor parameterVisitor = parsers.new FunctionParameterListVisitor();
			List<AstFunctionParameter> anonConstructorArguments = parameterVisitor.visitFunctionDefinitionParameterList(ctx.functionDefinitionParameterList());
			
			// -- Implemented interfaces
			List<AstToken> ancestors = ctx.TYPE_ID().stream()
				.skip(1)
				.map(terminalNode -> new AstToken(terminalNode.getSymbol()))
				.collect(Collectors.toList());
			
			// -- type parameters
			FunctionDeclarationParser.TypeParameterListVisitor typeParameterListVisitor = new FunctionDeclarationParser.TypeParameterListVisitor(reporter);
			
			TypeParameterDeclarationListContext c =  ctx.typeParameterDeclarationList();
			List<TypeParameter> typeParameters = // TODO: Optional ???
					(c == null) 
					? Collections.emptyList() 
					: typeParameterListVisitor.visit(c);
			
			// -- Member functions
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
			
			AstClassDeclaration classDeclaration = new AstClassDeclaration(
					reporter, 
					name, 
					List.copyOf(typeParameters),
					List.copyOf(methods),
					memberValues,
					anonConstructorArguments,
					ancestors
					);

			return classDeclaration;
		}
	}

}
