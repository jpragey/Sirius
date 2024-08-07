module org.sirius.backend.jvm {
	requires org.apache.logging.log4j;

//	requires org.sirius.frontend.apiimpl;
//	opens org.sirius.frontend.apiimpl;

	requires org.sirius.backend.core;

//	requires static lombok;

	requires org.sirius.sdk;

	exports org.sirius.backend.jvm;
}