package org.sirius.frontend.symbols;

import java.util.Optional;
import java.util.function.Consumer;

import org.sirius.common.core.QName;
import org.sirius.frontend.ast.AstClassDeclaration;
import org.sirius.frontend.ast.AstFunctionParameter;
import org.sirius.frontend.ast.AstLocalVariableStatement;
import org.sirius.frontend.ast.AstMemberValueDeclaration;
import org.sirius.frontend.ast.FunctionDefinition;
import org.sirius.frontend.ast.TypeParameter;

public interface SymbolTable {
	Optional<Symbol> lookupBySimpleName(String simpleName);
	
	Optional<Symbol> lookupByQName(QName symbolQName);
	
	Optional<AstClassDeclaration> lookupClassDeclaration(String simpleName);
	
	Optional<AstFunctionParameter> lookupFunctionArgument(String simpleName);

	Optional<FunctionDefinition> lookupPartialList(String simpleName);	// TODO:rename
	
	Optional<AstLocalVariableStatement> lookupLocalVariable(String simpleName);

	Optional<AstMemberValueDeclaration> lookupValue(String simpleName);

	
	void addFormalParameter(QName containerQName, TypeParameter formalParameter);	// TODO: remove ???

	/** For debugging */
	void dump(String prefix, Consumer<String> print);

	String getDbgName();
	void setName(String name);

}
