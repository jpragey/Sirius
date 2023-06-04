package org.sirius.frontend.api;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyList;
import static org.hamcrest.Matchers.empty;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.sirius.common.core.QName;
import org.sirius.frontend.apiimpl.ModuleDeclarationImpl;
import org.sirius.frontend.apiimpl.PackageDeclarationImpl;
import org.sirius.frontend.core.PhysicalPath;

public class ModuleDeclarationImplTest {

	@Test
	public void buildModuleDeclarationImpl_byBuilder_raw_hasEmptyFields() {
		ModuleDeclarationImpl underTest = new ModuleDeclarationImpl.Builder()
				.create();
		
		assertThat(underTest.getQNameString(), is(""));
		assertThat(underTest.version(), is(""));
		assertThat(underTest.physicalPath().getElements(), is(List.of()));
		assertThat(underTest.packageDeclarations(), empty());
	}

	@Test
	public void buildModuleDeclarationImpl_byBuilder_WithFields_hasCorrectFields() {
		ModuleDeclarationImpl underTest = new ModuleDeclarationImpl.Builder()
				.qName(QName.of("a", "b"))
				.version("1.0")
				.physicalPath(new PhysicalPath("org", "myCorp", "myModule"))
				.addPackageDeclarations(new PackageDeclarationImpl(
						Optional.of(QName.of("org","myPackage")), 
						List.<ClassType>of(), 
						List.<AbstractFunction>of()))
				.create();
		
		assertThat(underTest.getQNameString(), is("a.b"));
		assertThat(underTest.version(), is("1.0"));
		assertThat(underTest.physicalPath().getElements(), is(List.of("org", "myCorp", "myModule")));
		assertThat(underTest.packageDeclarations().size(), is(1));
	}
}
