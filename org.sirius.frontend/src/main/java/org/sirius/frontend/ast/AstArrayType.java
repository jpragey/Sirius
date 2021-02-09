package org.sirius.frontend.ast;

import java.io.Serializable;
import java.util.Optional;

import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.Type;
import org.sirius.frontend.apiimpl.ArrayTypeImpl;
import org.sirius.frontend.symbols.SymbolTable;

/** java-like array
 * 
 * @author jpragey
 *
 */
public class AstArrayType implements AstType{

	private AstType elementType;
	private Optional<AstType> resolvedElementType = Optional.empty();

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
	
	@Override
	public AstType resolve() {
		// TODO: style ?
		if(resolvedElementType.isEmpty())
			resolvedElementType = Optional.of(new AstArrayType(elementType.resolve()));
		
		return resolvedElementType.get();
	}
	
	public Type getApiType() {
		Type apiType = elementType.getApiType();
		return new ArrayTypeImpl(apiType);
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

	@Override
	public void visit(AstVisitor visitor) {
		visitor.start(this);
		elementType.visit(visitor);
		resolvedElementType.ifPresent(type -> type.visit(visitor));	// TODO: ???
		visitor.end(this);		
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof AstArrayType) {
			return isExactlyA( (AstArrayType)obj );
		}
		return false;
	}
	@Override
	public int hashCode() {
		int h = 20303108 ^ resolvedElementType.get().hashCode();
		return h;
	}

	@Override
	public void verify(int featureFlags) {
		elementType.verify(featureFlags);
		verifyOptional(resolvedElementType, "resolvedElementType", featureFlags); 
	}

}
