package org.sirius.frontend.api;

public interface Visitor {

	public default void start(ClassDeclaration declaration) {}
	public default void end(ClassDeclaration declaration) {}

	public default void start(InterfaceDeclaration declaration) {}
	public default void end(InterfaceDeclaration declaration) {}
	
	public default void start(MemberFunction declaration) {}
	public default void end(MemberFunction declaration) {}
	
	public default void start(MemberValue declaration) {}
	public default void end(MemberValue declaration) {}
	
	public default void start(ModuleDeclaration declaration) {}
	public default void end(ModuleDeclaration declaration) {}
	
	public default void start(PackageDeclaration declaration) {}
	public default void end(PackageDeclaration declaration) {}
	
	public default void start(TopLevelFunction declaration) {}
	public default void end(TopLevelFunction declaration) {}
	
	public default void start(TopLevelValue declaration) {}
	public default void end(TopLevelValue declaration) {}
	
}
