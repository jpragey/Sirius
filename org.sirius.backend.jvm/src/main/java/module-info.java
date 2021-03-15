module org.sirius.backend.jvm {
	requires transitive org.sirius.common;
	requires transitive org.sirius.frontend;
	requires org.sirius.backend.core;
	
	requires transitive org.objectweb.asm;
	requires org.sirius.sdk;

	exports org.sirius.backend.jvm;
}