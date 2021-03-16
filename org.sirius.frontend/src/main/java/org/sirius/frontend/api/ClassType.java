package org.sirius.frontend.api;

import java.util.List;

import org.sirius.common.core.QName;
/** Class or interface
 * 
 * @author jpragey
 *
 */
public interface ClassType extends Type {

	List<MemberValue> getMemberValues();
	List<AbstractFunction> getFunctions();

	QName getQName();
	
	default void visitContent(Visitor visitor) {
		
		for(MemberValue mv: getMemberValues()) {
			mv.visitMe(visitor);
		}
		
		for(AbstractFunction fct: getFunctions()) {
			fct.visitMe(visitor);
		}
	}

}
