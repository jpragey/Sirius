module org.sirius.frontend {
	requires org.sirius.common;
//	requires org.junit.jupiter.api;
	
	requires org.antlr.antlr4.runtime;
	requires com.google.common;
	requires org.sirius.sdk;
	
	exports org.sirius.frontend.api;
	exports org.sirius.frontend.core;	// TODO: remove
}