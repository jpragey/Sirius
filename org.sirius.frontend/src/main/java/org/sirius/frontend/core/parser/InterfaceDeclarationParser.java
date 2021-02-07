package org.sirius.frontend.core.parser;

import java.util.Collections;
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
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.PartialList;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.ast.Variance;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationListContext;

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
			
			List<AstClassOrInterface.AncestorInfo> intfList = ctx.TYPE_ID().stream()
				.skip(1)
				.map(terminalNode -> new AstClassOrInterface.AncestorInfo(new AstToken(terminalNode.getSymbol())))
				.collect(Collectors.toList());
			
			// -- type parameters
			FunctionDeclarationParser.TypeParameterListVisitor typeParameterListVisitor = new FunctionDeclarationParser.TypeParameterListVisitor(reporter);
			
			TypeParameterDeclarationListContext c =  ctx.typeParameterDeclarationList();
			List<TypeParameter> typeParameters = // TODO: Optional ???
					(c == null) 
					? ImmutableList.of()
					: typeParameterListVisitor.visit(c);
			
			
//			ImmutableList<PartialList> functionDeclarations = ImmutableList.of();
			
			// -- Member functions
//			QName containerQName = new QName("TODO");	// TODO
			FunctionDeclarationParser.FunctionDeclarationVisitor fctVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter);
			List<FunctionDeclaration> methods = ctx.children.stream()
				.map(parseTree -> parseTree.accept(fctVisitor))
				.filter(fctDecl -> fctDecl!=null)
				.collect(Collectors.toList());
			
			FunctionDeclarationParser.FunctionDefinitionVisitor fctdefVisitor = new FunctionDeclarationParser.FunctionDefinitionVisitor(reporter);
			List<FunctionDefinition> methodDefinitions = ctx.children.stream()
				.map(parseTree -> parseTree.accept(fctdefVisitor))
				.filter(fctDef -> fctDef!=null)
				.collect(Collectors.toList());
			
			MemberValueDeclarationParser.MemberValueVisitor memberValuesVisitor = new MemberValueDeclarationParser.MemberValueVisitor(reporter);
//			FunctionDeclarationParser.FunctionDeclarationVisitor fctVisitor = new FunctionDeclarationParser.FunctionDeclarationVisitor(reporter, containerQName);
			List<AstMemberValueDeclaration> memberValues = ctx.children.stream()
				.map(parseTree -> parseTree.accept(memberValuesVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			
			AstInterfaceDeclaration interfaceDeclaration = new AstInterfaceDeclaration(reporter, name, 
					ImmutableList.copyOf(methods), 
					ImmutableList.copyOf(methodDefinitions), 
					ImmutableList.copyOf(typeParameters),
					ImmutableList.copyOf(intfList),
					ImmutableList.copyOf(memberValues));
			return interfaceDeclaration;
		}
	}

}
