package org.sirius.frontend.apiimpl;

import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.AbstractFunction;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.InterfaceDeclaration;
import org.sirius.frontend.api.PackageDeclaration;
import org.sirius.frontend.api.TopLevelValue;

public class PackageDeclarationImpl implements PackageDeclaration {
	private QName qName;
	private List<ClassDeclaration> classDeclarations;
	private List<InterfaceDeclaration> interfaceDeclarations;
	private List<TopLevelValue> values;
	private List<AbstractFunction> functions;
	
	public PackageDeclarationImpl(QName qName, List<ClassDeclaration> classDeclarations,
			List<org.sirius.frontend.api.InterfaceDeclaration> interfaceDeclarations, List<TopLevelValue> values,
			List<AbstractFunction> functions) {
		super();
		this.qName = qName;
		this.classDeclarations = classDeclarations;
		this.interfaceDeclarations = interfaceDeclarations;
		this.values = values;
		this.functions = functions;
	}

	@Override
	public List<ClassDeclaration> getClasses() {
		return classDeclarations;
	}

	@Override
	public List<InterfaceDeclaration> getInterfaces() {
		return interfaceDeclarations;
	}

	@Override
	public List<TopLevelValue> getValues() {
		return values;
	}

	@Override
	public List<AbstractFunction> getFunctions() {
		return functions;
	}

	@Override
	public QName getQName() {
		return qName;
	}
	@Override
	public String toString() {
		return "Pack: \"" + qName + "\"";
	}

}