package org.sirius.frontend.core;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sirius.common.core.QName;
import org.sirius.common.core.Token;
import org.sirius.common.core.TokenLocation;

public  class PhysicalResourceQName {
	private List<String> stringElements;
	private List<? extends Token> elements;

	public PhysicalResourceQName(/** '/' separated source path */ String resourcePath) {
		super();
		this.stringElements = Arrays.asList(resourcePath.split("/"));
		this.elements = stringElements.stream().map(s -> new Token() {

			@Override
			public String getText() {
				return s;
			}

			@Override
			public Optional<TokenLocation> getTokenLocation() {
				return Optional.empty();
			}
			
		}).collect(Collectors.toList());
	}

//	@Override
//	public List<? extends Token> getElements() {
//		return elements;
//	}
//
//	@Override
//	public List<String> getStringElements() {
//		return stringElements;
//	}
	public QName toQName() {
		return new QName(stringElements);
	}
}
