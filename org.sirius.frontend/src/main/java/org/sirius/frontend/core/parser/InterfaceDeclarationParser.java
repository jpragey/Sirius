package org.sirius.frontend.core.parser;

import java.util.List;
import java.util.stream.Collectors;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstToken;
import org.sirius.frontend.ast.FunctionDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;
import org.sirius.frontend.parser.SiriusBaseVisitor;
import org.sirius.frontend.parser.SiriusParser.ImplementedInterfacesContext;
import org.sirius.frontend.parser.SiriusParser.InterfaceDeclarationContext;
import org.sirius.frontend.parser.SiriusParser.TypeParameterDeclarationListContext;

/** Visitor-based parser for the 'typeParameterDeclaration' rule.
 * 
 * @author jpragey
 *
 */
public class InterfaceDeclarationParser {
	private Reporter reporter;
	private Parsers parsers;
	
	public InterfaceDeclarationParser(Reporter reporter) {
		super();
		this.reporter = reporter;
		this.parsers = new Parsers(reporter);
	}

	public class InterfaceDeclarationVisitor extends SiriusBaseVisitor<AstInterfaceDeclaration> {

		public InterfaceDeclarationVisitor() {
			super();
		}

		@Override
		public AstInterfaceDeclaration visitInterfaceDeclaration(InterfaceDeclarationContext ctx) {
			
			AstToken name = new AstToken(ctx.interfaceName);
					
			// -- Implemented interfaces
			ImplementedInterfacesContext intfCtx = ctx.implementedInterfaces();
			List<AstToken> intfList = (intfCtx == null) ?
					List.of() :
					parsers.new ImplementClauseVisitor().visit(intfCtx);
			
			// -- type parameters
			FunctionDeclarationParser.TypeParameterListVisitor typeParameterListVisitor = new FunctionDeclarationParser.TypeParameterListVisitor(reporter);
			
			TypeParameterDeclarationListContext c =  ctx.typeParameterDeclarationList();
			List<TypeParameter> typeParameters = // TODO: Optional ???
					(c == null) 
					? List.of()
					: typeParameterListVisitor.visit(c);
			
			
			// -- Member functions
			Parsers.FunctionDeclarationVisitor fctVisitor = parsers.new FunctionDeclarationVisitor();
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
			List<AstMemberValueDeclaration> memberValues = ctx.children.stream()
				.map(parseTree -> parseTree.accept(memberValuesVisitor))
				.filter(partialList -> partialList!=null)
				.collect(Collectors.toList());
			
			
			AstInterfaceDeclaration interfaceDeclaration = new AstInterfaceDeclaration(reporter, name, 
					List.copyOf(methods), 
					List.copyOf(methodDefinitions), 
					List.copyOf(typeParameters),
					List.copyOf(intfList),
					List.copyOf(memberValues));
			return interfaceDeclaration;
		}
	}

}
