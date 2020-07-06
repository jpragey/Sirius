package org.sirius.frontend.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.Token;

public class ModuleImportEquivalents {

	private Map<String, AstToken> equivalentsMap = new HashMap<>();

	public Map<String, AstToken> getEquivalentsMap() {
		return equivalentsMap;
	}

	public Optional<AstToken> get(String key) {
		return Optional.ofNullable(equivalentsMap.get(key));
	}
	public void put(String key, AstToken value) {
		equivalentsMap.put(key, value);
	}
	public void put(Token key, Token value) {
		equivalentsMap.put(key.getText(), new AstToken(value));
	}
	
	
}
