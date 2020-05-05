package org.sirius.frontend.symbols;

import java.util.Stack;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionCallExpression;
import org.sirius.frontend.ast.AstFunctionDeclaration;
import org.sirius.frontend.ast.AstPackageDeclaration;
import org.sirius.frontend.ast.AstVisitor;
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
		// NB: no pop()
		qnameStack.push(declaration.getQname());
	}
	
	
	@Override
	public void startClassDeclaration(AstClassDeclaration classDeclaration) {

//		String className = classDeclaration.getName().getText();
		QName packageQName = qnameStack.lastElement();
		classDeclaration.setPackageQName(packageQName);
		
//		QName classQName = packageQName.child(className);
		QName classQName = classDeclaration.getQName();
		qnameStack.push(classQName);
//		classDeclaration.setqName(classQName);
		
		for(TypeParameter formalParameter: classDeclaration.getTypeParameters()) {
			classDeclaration.getSymbolTable().addFormalParameter(classDeclaration.getQName(), formalParameter);
		}
	}

	@Override
	public void endClassDeclaration(AstClassDeclaration classDeclaration) {
		qnameStack.pop();
	}

	@Override
	public void startFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
		
		functionDeclaration.setContainerQName(qnameStack.lastElement());
		String funcName = functionDeclaration.getName().getText();
		QName funcQName = qnameStack.lastElement().child(funcName);
		qnameStack.push(funcQName);
	}

	@Override
	public void endFunctionDeclaration(AstFunctionDeclaration functionDeclaration) {
		qnameStack.pop();
	}
	
	@Override
	public void startFunctionCallExpression(AstFunctionCallExpression expression) {
	}
	
	@Override
	public void start(SimpleType simpleType) {
	}
}
