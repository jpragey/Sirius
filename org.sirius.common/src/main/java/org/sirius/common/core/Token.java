package org.sirius.common.core;

import java.util.Optional;

public interface Token {

	/** Get the text of the token.
	 * 
	 * @return
	 */
	public String getText();
	
	public Optional<TokenLocation> getTokenLocation();
}
