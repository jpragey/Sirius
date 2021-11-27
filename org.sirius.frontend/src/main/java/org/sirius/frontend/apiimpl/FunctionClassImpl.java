package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.FunctionClass;
import org.sirius.frontend.api.Statement;
import org.sirius.frontend.api.Type;

public record FunctionClassImpl(QName qName, Type returnType, List<Statement> bodyStatements) implements FunctionClass 
{
}
