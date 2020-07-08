package org.sirius.frontend.symbols;

import org.sirius.common.core.QName;

public interface ExportedSymbol {
	
	public static class ExportedClass implements ExportedSymbol {
		private QName qname;

		public ExportedClass(QName qname) {
			super();
			this.qname = qname;
		}	
		public QName getQname() {
			return qname;
		}
	}
	public static class ExportedInterface implements ExportedSymbol {
		private QName qname;

		public ExportedInterface(QName qname) {
			super();
			this.qname = qname;
		}	
		public QName getQname() {
			return qname;
		}
	}
	public static class ExportedFunction implements ExportedSymbol {
		private QName qname;
		public ExportedFunction(QName qname) {
			super();
			this.qname = qname;
		}	
		public QName getQname() {
			return qname;
		}
	}
}
