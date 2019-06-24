package org.sirius.compiler.cli.framework;

import java.util.LinkedList;
import java.util.Optional;

public class CliIterator {
		private LinkedList<String> currentCliOptions = new LinkedList<>();

		public CliIterator(String[] currentCliOptions) {
			super();

			this.currentCliOptions = new LinkedList<>();
			for(String s: currentCliOptions)
				this.currentCliOptions.add(s);
		}
		
		public void advance(Integer n) {
			for(int i=0; i<n; i++)
				this.currentCliOptions.removeFirst();
		}
		
		public Optional<String> getLookahead(int n) {
			return n >=  currentCliOptions.size() ?
					Optional.empty() :
					Optional.of(currentCliOptions.get(n));
		}
		
		public String remainingAsString() {
			return currentCliOptions.toString();
		}
		
		public int remainingSize() {
			return currentCliOptions.size();
		}
		
	}
//	static class CliException extends Exception {
//
//		public CliException(String message, Throwable cause) {
//			super(message, cause);
//		}
//
//		public CliException(String message) {
//			super(message);
//		}
//		
//	}