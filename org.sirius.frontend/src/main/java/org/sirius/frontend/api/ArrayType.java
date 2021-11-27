package org.sirius.frontend.api;

/** Simple array ( someType[] )
 * 
 * @author jpragey
 *
 */
public interface ArrayType extends Type {
	Type elementType();
}
