package org.sirius.frontend.apiimpl;

import java.util.List;

import org.sirius.frontend.api.ExecutionEnvironment;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public record ExecutionEnvironmentImpl(
		Type returnType, 
		List<Statement> bodyStatements
		) 
implements ExecutionEnvironment {}
