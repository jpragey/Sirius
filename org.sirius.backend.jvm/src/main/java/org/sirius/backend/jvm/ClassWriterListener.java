package org.sirius.backend.jvm;

import org.sirius.common.core.QName;

public interface ClassWriterListener {

	public void start();
	public void addByteCode(Bytecode bytecode, QName classQName);
	public void end();

}
