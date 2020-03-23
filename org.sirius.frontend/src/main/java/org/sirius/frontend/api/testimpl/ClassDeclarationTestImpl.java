package org.sirius.frontend.api.testimpl;

import java.util.Collections;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;
import org.sirius.frontend.api.Type;

public class ClassDeclarationTestImpl implements ClassDeclaration {

	private QName qname;
	private List<MemberValue> values;
	private List<MemberFunction> functions;
	
	public ClassDeclarationTestImpl(QName qname, List<MemberValue> values, List<MemberFunction> functions) {
		super();
		this.qname = qname;
		this.values = values;
		this.functions = functions;
	}

	public ClassDeclarationTestImpl(QName qname) {
		super();
		this.qname = qname;
		this.values = Collections.emptyList();
		this.functions = Collections.emptyList();
	}

	@Override
	public List<MemberValue> getValues() {
		return values;
	}

	@Override
	public List<MemberFunction> getFunctions() {
		return functions;
	}

	@Override
	public QName getQName() {
		return qname;
	}

	@Override
	public String toString() {
		return "ClassDeclarationTestImpl : " + qname.dotSeparated();
	}

	@Override
	public boolean isAncestorOrSame(Type type) {
		throw new UnsupportedOperationException("isAncestorOrSame not supported for type " + this.getClass());
	}

}
