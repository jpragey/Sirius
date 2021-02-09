package org.sirius.frontend.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.antlr.v4.runtime.Token;

public class ModuleImportEquivalents implements Verifiable {

	private class EquivalentInfo {
		public AstToken token;
		public String trimmedValue;
		public EquivalentInfo(AstToken token) {
			super();
			this.token = token;
			String s = token.getText();
			this.trimmedValue = s.substring(1,  s.length()-1).trim();
		}
		
	}
	private Map<String, EquivalentInfo> equivalentsMap = new HashMap<>();

//	@Override
//	public void verify() {
//	}
	
	public Map<String, EquivalentInfo> getEquivalentsMap() {
		return equivalentsMap;
	}

	public Optional<AstToken> get(String key) {
		EquivalentInfo ei = equivalentsMap.get(key);
		if(ei == null)
			return Optional.empty();
		return Optional.of(ei.token);
	}
	public Optional<String> getTrimmed(String key) {
		EquivalentInfo ei = equivalentsMap.get(key);
		if(ei == null)
			return Optional.empty();
		return Optional.of(ei.trimmedValue);
	}
	
	public void put(String key, AstToken value) {
		equivalentsMap.put(key, new EquivalentInfo(value));
	}
	public void put(AstToken key, AstToken value) {
		put(key.getText(), value);
	}
	public void put(Token key, Token value) {
		put(key.getText(), new AstToken(value));
	}

	@Override
	public void verify(int featureFlags) {// Nothing to do
	}
	
	
}
