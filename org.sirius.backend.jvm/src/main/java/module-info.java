module org.sirius.backend.jvm {
	requires org.apache.logging.log4j;

	requires transitive org.sirius.common;

	requires transitive org.sirius.frontend;
//	requires org.sirius.frontend.apiimpl;
//	opens org.sirius.frontend.apiimpl;

	requires org.sirius.backend.core;

	requires transitive org.objectweb.asm;
//	requires static lombok;

	requires org.sirius.sdk;
	requires org.sirius.runtime;

	exports org.sirius.backend.jvm;
}