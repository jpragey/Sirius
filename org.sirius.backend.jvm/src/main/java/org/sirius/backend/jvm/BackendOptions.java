package org.sirius.backend.jvm;

import java.util.HashSet;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;

public class BackendOptions {
	private Reporter reporter;
	private Optional<String> jvmMainOption;

	private Optional<QName> jvmMainFunctionQName;
	
	public BackendOptions(Reporter reporter, Optional<String> jvmMainOption) {
		super();
		this.reporter = reporter;
		this.jvmMainOption = jvmMainOption;
		this.jvmMainFunctionQName = jvmMainOption.map(s -> toJvmMainQName(reporter, s));
	}
	
	private static QName toJvmMainQName(Reporter reporter, String jvmMainOption) {
		String [] parts = jvmMainOption.split("\\.");
		QName qn = new QName(parts);
		if(qn.isEmpty()) {
			reporter.error("Invalid JVM main option: " + jvmMainOption);
			return QName.empty;
		}
//		String simpleName = qn.getLast();
//		QName fullQN = qn.parent().map(pkgName -> pkgName.child("$package$").child(simpleName)).orElse(QName.empty);
//		return fullQN;
		return qn;
	}

	public Optional<String> getJvmMainOption() {
		return jvmMainOption;
	}
	private HashSet<String> writtenJvmMain = new HashSet<>();
	
	public void markJvmMainAsWritten(String functionName) {
		if(writtenJvmMain.contains(functionName)) {
			reporter.error("JVM main function writtent twice: " + functionName);
		} else {
			writtenJvmMain.add(functionName);
		}
	}
	/** Report an error is a JVM main from shell args has not been written as bytecode.
	 * 
	 */
	public void checkAllJvmMainBytecodeWritten() {
		jvmMainOption.ifPresent(fctName -> {
			if(! writtenJvmMain.contains(fctName)) {
				reporter.error("JVM main() specified in options (" + fctName + ") has not been written as bytecode - corresponding sirius method not found ?");
			}
		});
	}
	

	public Optional<QName> getJvmMainFunctionQName() {
		return jvmMainFunctionQName;
	}

	
	
}
