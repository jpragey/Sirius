package org.sirius.frontend.symbols;

import java.util.Stack;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstInterfaceDeclaration;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.AstModuleDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.Partial;
import org.sirius.frontend.ast.SimpleType;
import org.sirius.frontend.ast.TypeParameter;

/** Visitor that sets the qnames throughout the AST.
 * 
 * @author jpragey
 *
 */
public class QNameSetterVisitor implements AstVisitor {

	private Stack<QName> qnameStack = new Stack<>();
	

	public QNameSetterVisitor() {
		super();
		this.qnameStack.push(new QName()); // TODO: ??? 
	}


	@Override
	public void startPackageDeclaration(AstPackageDeclaration declaration) {
		QName moduleQName = qnameStack.peek();
		QName pkgQName = moduleQName.child(declaration.getQname());
		qnameStack.push(pkgQName);
	}
	@Override
	public void endPackageDeclaration(AstPackageDeclaration declaration) {
		qnameStack.pop();
	}
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {

		QName packageQName = qnameStack.lastElement();
		classDeclaration.setPackageQName(packageQName);
		
		QName classQName = classDeclaration.getQName();
//System.out.println("startClass : " + classQName);		
		qnameStack.push(classQName);
		
		for(TypeParameter formalParameter: classDeclaration.getTypeParameters()) {
			classDeclaration.getSymbolTable().addFormalParameter(classDeclaration.getQName(), formalParameter);
		}
	}

	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		QName classQName = qnameStack.pop();
//System.out.println("endClass : " + classQName);		
	}

	@Override
	public void startInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
//		String className = classDeclaration.getName().getText();
		QName packageQName = qnameStack.lastElement();
		interfaceDeclaration.setPackageQName(packageQName);
		
		QName classQName = interfaceDeclaration.getQName();
//System.out.println("startInterface : " + classQName);		
		qnameStack.push(classQName);
		
		for(TypeParameter formalParameter: interfaceDeclaration.getTypeParameters()) {
			interfaceDeclaration.getSymbolTable().addFormalParameter(interfaceDeclaration.getQName(), formalParameter);
		}
	}
	@Override
	public void endInterfaceDeclaration(AstInterfaceDeclaration interfaceDeclaration) {
		QName classQName = qnameStack.pop();
//System.out.println("endInterface : " + classQName);		
	}
	
	@Override
	public void startModuleDeclaration(AstModuleDeclaration declaration) {
		QName moduleQName = declaration.getqName();
		qnameStack.push(moduleQName);
	}
	@Override
	public void endModuleDeclaration(AstModuleDeclaration declaration) {
		qnameStack.pop();
	}
	
	

	@Override
	public void startFunctionDefinition(FunctionDefinition functionDefinition) {
		QName containerQName = qnameStack.peek();
		functionDefinition.setContainerQName(containerQName);
		qnameStack.push(containerQName);
	}
	@Override
	public void endFunctionDefinition(FunctionDefinition functionDefinition) {
		qnameStack.pop();
	}
	
	
	
	@Override
	public void startPartial(Partial partial) {
		QName containerQName = qnameStack.peek();
		partial.setContainerQName(containerQName);
		qnameStack.push(containerQName);
	}
	@Override
	public void endPartial(Partial partialFunctionDeclaration) {
		qnameStack.pop();
	}
	
	@Override
	public void startFunctionCallExpression(AstFunctionCallExpression expression) {
	}
	
	@Override
	public void start(SimpleType simpleType) {
	}
	
	@Override
	public void startValueDeclaration(AstMemberValueDeclaration valueDeclaration) {
		QName containerQName = qnameStack.peek();
		valueDeclaration.setContainerQName(containerQName);
	}
	
}
