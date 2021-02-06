package org.sirius.backend.jvm;

import java.util.List;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Visitor;

class CodeTreeBuilder implements Visitor {
	private Reporter reporter;
	private JvmModule nodeModule;
	
	public CodeTreeBuilder(Reporter reporter) {
		super();
		this.reporter = reporter;
	}


	@Override
	public void start(ModuleDeclaration declaration) {
		this.nodeModule = new JvmModule(reporter, declaration);
	}

	public void createByteCode(List<ClassWriterListener> listeners) {
		nodeModule.createByteCode(listeners);
	}

	@Override
	public void end(ModuleDeclaration declaration) {
	}

}
