package org.sirius.frontend.ast;

import org.sirius.common.core.QName;

public class AstClassQName {
	private QName packageQName;
	private String className;
	
	
	
	public AstClassQName(QName packageQName, String className) {
		super();
		this.packageQName = packageQName;
		this.className = className;
	}
	public QName getPackageQName() {
		return packageQName;
	}
	public String getClassName() {
		return className;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((packageQName == null) ? 0 : packageQName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AstClassQName other = (AstClassQName) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (packageQName == null) {
			if (other.packageQName != null)
				return false;
		} else if (!packageQName.equals(other.packageQName))
			return false;
		return true;
	}
	
	
}
