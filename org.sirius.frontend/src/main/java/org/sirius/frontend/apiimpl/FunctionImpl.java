package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.FunctionParameter;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.ast.AstFunctionParameter;

/** Function implementation
 * 
 * @author jpragey
 *
 */
public record FunctionImpl(
		QName qName,
		List<FunctionParameter> parameters,
		Type returnType,
		List<Statement> bodyStatements,
		boolean member
		) implements AbstractFunction 
{
	@Override
	public String toString() {
		return "API function " + qName.dotSeparated() + "(" + parameters.size() + " args)";
	}
	
	@Override
	public Optional<QName> getClassOrInterfaceContainerQName() {
		return  member? qName.parent() : Optional.empty();
	}
}