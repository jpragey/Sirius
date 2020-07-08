package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstClassOrInterface;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.AstType;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;

import com.google.common.collect.ImmutableList;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class InterfaceDeclarationParser {
	private Reporter reporter;
	
	public InterfaceDeclarationParser(Reporter reporter) {
		super();
		this.reporter = reporter;
	}

	public static class InterfaceDeclarationVisitor extends SiriusBaseVisitor<AstInterfaceDeclaration> {
		private Reporter reporter;

		public InterfaceDeclarationVisitor(Reporter reporter) {
			super();
			this.reporter = reporter;
		}

		@Override
		public AstInterfaceDeclaration visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.TYPE_ID(0).getSymbol());
			
//			AstToken intfName = new AstToken(ctx.TYPE_ID(1).getSymbol());
			List<AstClassOrInterface.AncestorInfo> intfList = ctx.TYPE_ID().stream()
				.skip(1)
				.map(terminalNode -> new AstClassOrInterface.AncestorInfo(new AstToken(terminalNode.getSymbol())))
				.collect(Collectors.toList());
			
			
//			Optional<QName> packageQName = Optional.empty();
			
			
			// -- type parameters
			TypeParameterParser.TypeParameterVisitor typeParameterVisitor = new TypeParameterParser.TypeParameterVisitor(reporter);
			List<TypeParameter> typeParameters = ctx.typeParameterDeclaration().stream()
				.map(typeParamDeclCtxt -> typeParamDeclCtxt.accept(typeParameterVisitor))
				.collect(Collectors.toUnmodifiableList());

			
//			ImmutableList<TypeParameter> typeParameters = ImmutableList.of();
			
//			ImmutableList<PartialList> functionDeclarations = ImmutableList.of();
			
			// -- Member functions
			QName containerQName = new QName("TODO");	// TODO
			FunctionDeclarationParser.FunctionDeclarationVisitor fctVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter, containerQName);
			List<PartialList> methods = ctx.children.stream()
				.map(parseTree -> parseTree.accept(fctVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			MemberValueDeclarationParser.MemberValueVisitor memberValuesVisitor = new MemberValueDeclarationParser.MemberValueVisitor(reporter);
//			FunctionDeclarationParser.FunctionDeclarationVisitor fctVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter, containerQName);
			List<AstMemberValueDeclaration> memberValues = ctx.children.stream()
				.map(parseTree -> parseTree.accept(memberValuesVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			
			AstInterfaceDeclaration interfaceDeclaration = new AstInterfaceDeclaration(reporter, name, //packageQName, 
					ImmutableList.copyOf(methods), 
					ImmutableList.copyOf(typeParameters),
					ImmutableList.copyOf(intfList),
					ImmutableList.copyOf(memberValues));
			return interfaceDeclaration;
		}
	}

}
