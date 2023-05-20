package org.sirius.backend.jvm;

import java.util.List;
import java.util.Optional;

import org.sirius.common.error.Reporter;
import org.sirius.frontend.api.ModuleDeclaration;
import org.sirius.frontend.api.Visitor;

class CodeTreeBuilder implements Visitor {
	private Reporter reporter;
	private JvmModule moduleNode;
	private BackendOptions backendOptions;

	public CodeTreeBuilder(Reporter reporter, BackendOptions backendOptions) {
		super();
		this.reporter = reporter;
		this.backendOptions = backendOptions;
	}


	@Override
	public void start(ModuleDeclaration declaration) {
		this.moduleNode = new JvmModule(reporter, declaration, backendOptions);
	}

	public JvmModule createByteCode(List<ClassWriterListener> listeners) {
		moduleNode.createByteCode(listeners);
		return moduleNode;
	}

	@Override
	public void end(ModuleDeclaration declaration) {
	}

}
