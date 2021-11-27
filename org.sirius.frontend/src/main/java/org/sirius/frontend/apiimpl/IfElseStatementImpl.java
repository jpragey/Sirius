package org.sirius.frontend.apiimpl;

import java.util.Optional;

import org.sirius.frontend.api.Expression;
import org.sirius.frontend.api.IfElseStatement;
import org.sirius.frontend.api.Statement;

public record IfElseStatementImpl (
		Expression expression, 
		Statement ifStatement, 
		Optional<Statement> elseStatement
		) 
implements IfElseStatement 
{
}