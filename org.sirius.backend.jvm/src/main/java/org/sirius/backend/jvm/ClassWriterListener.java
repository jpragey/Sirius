package org.sirius.backend.jvm;

import org.sirius.frontend.api.ModuleDeclaration;

public
interface ClassWriterListener {

	public void start(ModuleDeclaration moduleDeclaration);
	public void addByteCode(Bytecode bytecode);
	public void end();

}
