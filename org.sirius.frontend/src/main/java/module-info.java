module org.sirius.frontend {
	requires org.sirius.common;
//	requires org.junit.jupiter.api;
	
	requires org.antlr.antlr4.runtime;
	requires transitive com.google.common;
	requires org.sirius.sdk;
	
	exports org.sirius.frontend.api;
	exports org.sirius.frontend.core.stdlayout; // TODO: remove ???
	exports org.sirius.frontend.core;	// TODO: remove
}