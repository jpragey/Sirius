package org.sirius.common.error;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Optional;

import org.sirius.common.core.Token;
import org.sirius.common.core.TokenLocation;

public class ShellReporter implements Reporter {
	
	private int errorCnt = 0;
	
//	@Override
//	public void message(String message) {
//		System.out.println("INFO: " + message);
//	}
//
//	@Override
//	public void warning(String message) {
//		System.out.println("WARN: " + message);
//	}
//
//	@Override
//	public void error(String message, Optional<Exception> exception) {
//		errorCnt ++;
//		System.err.println("ERROR: " + message);
//	}

	@Override
	public int getErrorCount() {
		return errorCnt;
	}

	@Override
	public void message(Severity severity, String message, Optional<Token> token, Optional<Exception> exception) {
		StringBuilder sb = new StringBuilder();
		
		if(token.isPresent()) {
			Token tk = token.get();
			tk.getTokenLocation().ifPresent(location -> {
				sb.append(location.getSourceName());
				sb.append(":");
				sb.append(location.getLine());
				sb.append(":");
				sb.append(location.getCharPositionInLine());
				sb.append(":");
			});
		}
		
		sb.append(severity.name);

		sb.append(":");

		sb.append(message);
		
		PrintStream printStream = severity == Severity.ERROR ? System.err : System.out;
		printStream.println(sb.toString());
		
		if(exception.isPresent()) {
			Exception e = exception.get();
			e.printStackTrace(System.err);
		}
	}

}
