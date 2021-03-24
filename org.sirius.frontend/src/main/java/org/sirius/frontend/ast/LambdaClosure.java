package org.sirius.frontend.ast;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LambdaClosure {
//	public static class ClosureEntry {
//		private AstType type;
//		private AstToken name;
//		private Optional<AstExpression> initExpr;
//		public ClosureEntry(AstType type, AstToken name, Optional<AstExpression> initExpr) {
//			super();
//			this.type = type;
//			this.name = name;
//		}
//		public ClosureEntry(AstType type, AstToken name) {
//			this(type, name, Optional.empty());
//		}
//		
//	}
	
	private List<ClosureElement> closureEntries;

	public LambdaClosure(List<ClosureElement> closureEntries) {
		super();
		this.closureEntries = closureEntries;
	}
	public LambdaClosure() {
		this(List.of());
	}

	public List<ClosureElement> getClosureEntries() {
		return closureEntries;
	}
	
	public LambdaClosure appendEntry(ClosureElement entry) {
		List<ClosureElement> newEntries = Stream.concat(closureEntries.stream(), Stream.of(entry)) .collect(Collectors.toList());
		return new LambdaClosure(newEntries);
	}
//	public LambdaClosure appendEntry(AstType type, AstToken name, Optional<AstExpression> initExpr) {
//		return appendEntry(new ClosureElement(type, name, initExpr));
//	}
	public LambdaClosure appendEntry(AstType type, AstToken name) {
		return appendEntry(new ClosureElement(type, name));
	}
	
}
