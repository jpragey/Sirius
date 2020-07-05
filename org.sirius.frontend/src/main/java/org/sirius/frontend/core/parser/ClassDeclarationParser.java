package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstClassOrInterface;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ClassDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.FunctionFormalArgumentContext;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;

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
		private QName containerQName;

		public ClassDeclarationVisitor(Reporter reporter, QName containerQName) {
			super();
			this.reporter = reporter;
			this.containerQName = containerQName;
		}

		@Override
		public AstClassDeclaration visitClassDeclaration(ClassDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.TYPE_ID(0).getSymbol());
			
			// -- Constructor arguments
			FunctionDeclarationParser.FunctionParameterVisitor parameterVisitor = new FunctionDeclarationParser.FunctionParameterVisitor (reporter);
			List<AstFunctionParameter> anonConstructorArguments = 
			ctx.functionFormalArgument().stream()
				.map(argCtxt -> argCtxt.accept(parameterVisitor))
				.filter((AstFunctionParameter p) -> p!=null)
				.collect(Collectors.toList());
			
			// -- Implemented interfaces
			List<AstClassOrInterface.AncestorInfo> ancestors = ctx.TYPE_ID().stream()
				.skip(1)
				.map(terminalNode -> new AstClassOrInterface.AncestorInfo(new AstToken(terminalNode.getSymbol())))
				.collect(Collectors.toList());
			
			// -- type parameters
			TypeParameterParser.TypeParameterVisitor typeParameterVisitor = new TypeParameterParser.TypeParameterVisitor(reporter);
			List<TypeParameter> typeParameters = ctx.typeParameterDeclaration().stream()
				.map(typeParamDeclCtxt -> typeParamDeclCtxt.accept(typeParameterVisitor))
				.collect(Collectors.toUnmodifiableList());
			
			// -- Member functions
//			QName containerQName = new QName("TODO");	// TODO
			FunctionDeclarationParser.FunctionDeclarationVisitor fctVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter, containerQName);
			List<PartialList> methods = ctx.children.stream()
				.map(parseTree -> parseTree.accept(fctVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			MemberValueDeclarationParser.MemberValueVisitor memberValuesVisitor = new MemberValueDeclarationParser.MemberValueVisitor(reporter);
			List<AstMemberValueDeclaration> memberValues = ctx.children.stream()
				.map(parseTree -> parseTree.accept(memberValuesVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			boolean interfaceType = false;
			
			QName packageQName  = containerQName;
			
			AstClassDeclaration classDeclaration = new AstClassDeclaration(
					reporter, 
					interfaceType, 
					name, 
					packageQName,
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
