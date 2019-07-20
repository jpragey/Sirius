package org.sirius.frontend.ast;

import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.Type;

/** java-like array
 * 
 * @author jpragey
 *
 */
public class AstArrayType implements AstType {

	private AstType elementType;

	public AstArrayType(AstType elementType) {
		super();
		this.elementType = elementType;
	}

	public AstType getElementType() {
		return elementType;
	}

	@Override
	public String messageStr() {
		return elementType.messageStr() + "[]";
	}
	
	public Type getApiType() {
		Type apiType = elementType.getApiType();
		return new ArrayType() {
			
			@Override
			public Type getElementType() {
				return apiType;
			}
		};
	}

	@Override
	public boolean isExactlyA(AstType type) {
		if(type instanceof AstArrayType) {
			AstArrayType otherType = (AstArrayType)type;
			return this.elementType.isExactlyA(otherType);
		}
		return false;
	}

	@Override
	public boolean isAncestorOrSameAs(AstType type) {
		return isExactlyA(type);
	}

	@Override
	public boolean isStrictDescendantOf(AstType type) {
		return false;
	}

}
