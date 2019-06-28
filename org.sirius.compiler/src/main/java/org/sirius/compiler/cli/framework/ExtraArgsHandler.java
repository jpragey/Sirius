package org.sirius.compiler.cli.framework;

import java.util.List;
import java.util.Optional;

@FunctionalInterface
public interface ExtraArgsHandler <OptionValues> {

	Optional<String> consumeExtraArgs(OptionValues optionValues, List<String> extraArgs);
}
