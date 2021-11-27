package org.sirius.frontend.apiimpl;

import org.sirius.frontend.api.ArrayType;
import org.sirius.frontend.api.Type;

public record ArrayTypeImpl(Type elementType) implements ArrayType {
}
