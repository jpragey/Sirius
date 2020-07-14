package org.sirius.compiler.core;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

public class VersionTest {

	@Test
	public void testVersionIsReadInResource() throws IOException {
		String version = new Version().getVersion().trim();
		assertThat(version, is("0.0.1-SNAPSHOT"));	// not ${sirius.version}
	}
}
