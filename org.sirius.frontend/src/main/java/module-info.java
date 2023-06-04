/**
 * 
 * @author jpragey
 *
 */
module org.sirius.frontend {
	requires transitive org.sirius.common;
//	requires org.junit.jupiter.api;
	
	requires org.antlr.antlr4.runtime;
	requires org.sirius.sdk;
	
//	exports org.sirius.frontend;
	exports org.sirius.frontend.api;
//	exports org.sirius.frontend.apiimpl to org.sirius.backend.jvm;	// TODO: remove (used for test only ? move frontend api to its own module ?)
	exports org.sirius.frontend.apiimpl;	// TODO: remove (used for test only ? move frontend api to its own module ?)
	
//	opens org.sirius.frontend.apiimpl;
	
//	exports org.sirius.frontend.core.stdlayout; // TODO: remove ???
	exports org.sirius.frontend.core;	// TODO: remove
}