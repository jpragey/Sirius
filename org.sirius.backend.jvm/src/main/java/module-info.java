module org.sirius.backend.jvm {
	requires transitive org.sirius.common;
	requires transitive org.sirius.frontend;
	requires org.sirius.backend.core;
	requires org.sirius.sdk;
	
	//requires org.junit.jupiter.api;
	requires transitive org.objectweb.asm;

	exports org.sirius.backend.jvm;
}