/**
 * 
 * @author jpragey
 *
 */
module org.sirius.frontend {
	requires org.sirius.common;
	requires org.antlr.antlr4.runtime;
	requires org.sirius.sdk;
	
	exports org.sirius.frontend.api;
	exports org.sirius.frontend.core;	// TODO: remove

	// TODO: remove (currently used for test only)
	exports org.sirius.frontend.apiimpl;
}