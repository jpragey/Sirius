package org.sirius.frontend.apiimpl;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.core.PhysicalPath;


public record ModuleDeclarationImpl(
		QName qName, 
		String version, 
		PhysicalPath physicalPath, 
		List<PackageDeclaration> packageDeclarations) 
	implements ModuleDeclaration 
{
	@Override
	public String toString() {
		return "\"" + qName().toString() + "\"";
	}

}
