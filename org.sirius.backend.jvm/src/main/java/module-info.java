module org.sirius.backend.jvm {
	requires org.apache.logging.log4j;

	requires transitive org.objectweb.asm;

	requires org.sirius.common;
	requires org.sirius.frontend;

	requires org.sirius.backend.core;
	requires org.sirius.sdk;

	exports org.sirius.backend.jvm;
}