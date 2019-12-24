package org.sirius.frontend.api.testimpl;

import java.util.Collections;
import java.util.List;

import org.sirius.common.core.QName;
import org.sirius.frontend.api.ClassDeclaration;
import org.sirius.frontend.api.MemberFunction;
import org.sirius.frontend.api.MemberValue;

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

}
