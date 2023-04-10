package org.sirius.backend.jvm;

import java.util.HashSet;
import java.util.Optional;

import org.sirius.common.core.QName;
import org.sirius.common.error.Reporter;

/** This backend options (stateful class).
 * Stores:
 * <ul>
 * 	<li>The main function QName.
 * </ul>
 * It also marks/checks if the main function QName has been written to bytecode (with {@link #markJvmMainAsWritten(QName)}
 * and {@link #checkAllJvmMainBytecodeWritten()}).
 */
public class BackendOptions {
	private Reporter reporter;

	private Optional<QName> jvmMainFunctionQName;

	private HashSet<QName> writtenJvmMain = new HashSet<>();

	public BackendOptions(Reporter reporter, Optional<String> optJvmMainOption) {
		super();
		this.reporter = reporter;
		
		Optional<QName> qn =  optJvmMainOption.flatMap((String fctName) -> {
			
			QName qName = QName.parseDotSeparated(fctName);
			if(qName.stream().anyMatch(String::isEmpty)) {
				reporter.error("Main funcion name can't contain an empty element: " + optJvmMainOption);
				return Optional.<QName>empty();
			}

			if (qName.isEmpty()) {
				reporter.error("Invalid JVM main function option: " + optJvmMainOption);
				return Optional.<QName>empty();
			}
			
			return Optional.of(qName);
			});
		this.jvmMainFunctionQName = qn;
		
	}
	
	public void markJvmMainAsWritten(QName functionName) {
		if(writtenJvmMain.contains(functionName)) {
			reporter.error("JVM main function writtent twice: " + functionName.dotSeparated());
		} else {
			writtenJvmMain.add(functionName);
		}
	}
	/** Report an error is a JVM main from shell args has not been written as bytecode.
	 * 
	 */
	public void checkAllJvmMainBytecodeWritten() {
		jvmMainFunctionQName.ifPresent( (QName fctName) -> {
			if(! writtenJvmMain.contains(fctName)) {
				reporter.error("JVM main() specified in options (" + fctName + ") has not been written as bytecode - corresponding sirius method not found ?");
			}
		});
	}
	
	public Optional<QName> getJvmMainFunctionQName() {
		return this.jvmMainFunctionQName;
	}

}
