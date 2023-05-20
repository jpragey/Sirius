package org.sirius.frontend.apiimpl;

import java.util.List;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassType;
import org.sirius.frontend.api.PackageDeclaration;

public record PackageDeclarationImpl(
		Optional<QName> qName, 
		List<ClassType> classDeclarations,
		List<AbstractFunction> functions
) implements PackageDeclaration 
{

	@Override
	public List<ClassType> getClasses() {
		return classDeclarations;
	}


	@Override
	public List<AbstractFunction> getFunctions() {
		return functions;
	}

	@Override
	public String toString() {
		return "Pack: \"" + qName + "\"";
	}

}