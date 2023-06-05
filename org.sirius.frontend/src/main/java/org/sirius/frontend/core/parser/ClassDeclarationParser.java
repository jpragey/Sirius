package org.sirius.frontend.core.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.parser.SParserBaseVisitor;
import org.sirius.frontend.parser.SParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SParser.ImplementedInterfacesContext;
import org.sirius.frontend.parser.SParser.TypeParameterDeclarationListContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class ClassDeclarationParser {
	private Reporter reporter;
	private Parsers parsers;
	private CommonTokenStream tokens;
	public ClassDeclarationParser(Reporter reporter, CommonTokenStream tokens) {
		super();
		this.reporter = reporter;
		this.parsers = new Parsers(reporter, tokens);
		this.tokens = tokens;
	}

//	public class ImplementClauseVisitor extends SParserBaseVisitor<List<AstToken>> {
//		@Override
//		public List<AstToken> visitImplementedInterfaces(ImplementedInterfacesContext ctx) {
//			List<AstToken> interfaceNames = ctx.TYPE_ID().stream().map(termNode -> new AstToken(termNode.getSymbol())).toList();
//			return interfaceNames;
//		}
//	}
	public class ClassDeclarationVisitor extends SParserBaseVisitor<AstClassDeclaration> {

		@Override
		public AstClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.className);
			
			// -- Constructor arguments
			Parsers.FunctionParameterListVisitor parameterVisitor = parsers.new FunctionParameterListVisitor();
			List<AstFunctionParameter> anonConstructorArguments = parameterVisitor.visitFunctionDefinitionParameterList(ctx.functionDefinitionParameterList());
			
			// -- Implemented interfaces
			ImplementedInterfacesContext ict =  ctx.implementedInterfaces();
			List<AstToken> ancestors = (ict == null) ? List.of() : parsers.new ImplementClauseVisitor().visit(ict);
			
			// -- type parameters
			FunctionDeclarationParser.TypeParameterListVisitor typeParameterListVisitor = new FunctionDeclarationParser.TypeParameterListVisitor(reporter);
			
			TypeParameterDeclarationListContext c =  ctx.typeParameterDeclarationList();
			List<TypeParameter> typeParameters = // TODO: Optional ???
					(c == null) 
					? Collections.emptyList() 
					: typeParameterListVisitor.visit(c);
			
			// -- Member functions
			FunctionDeclarationParser.FunctionDefinitionVisitor fctVisitor = new FunctionDeclarationParser(reporter, tokens).new FunctionDefinitionVisitor();
			List<FunctionDefinition> methods = ctx.children.stream()
				.map(parseTree -> parseTree.accept(fctVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			MemberValueDeclarationParser.MemberValueVisitor memberValuesVisitor = new MemberValueDeclarationParser.MemberValueVisitor(reporter, tokens);
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
