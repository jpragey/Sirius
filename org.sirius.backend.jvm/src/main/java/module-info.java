module org.sirius.backend.jvm {
	requires org.sirius.common;
	requires org.sirius.frontend;
	requires org.sirius.backend.core;
	requires org.sirius.sdk;
	
	requires org.junit.jupiter.api;
	requires org.objectweb.asm;

	exports org.sirius.backend.jvm;
}