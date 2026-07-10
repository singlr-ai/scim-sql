/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.postgresql;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Module descriptor")
class ModuleDescriptorTest {

  @Test
  @DisplayName("public packages are exported, the vendored parser package is not")
  void shouldExportOnlyPublicPackages() {
    var module =
        ModuleFinder.of(Path.of("target", "classes")).find("ai.singlr.scimsql").orElseThrow();

    Set<String> exports =
        module.descriptor().exports().stream()
            .map(java.lang.module.ModuleDescriptor.Exports::source)
            .collect(java.util.stream.Collectors.toSet());

    assertTrue(exports.contains("ai.singlr.scimsql"));
    assertTrue(exports.contains("ai.singlr.postgresql"));
    assertFalse(exports.contains("ai.singlr.postgresql.parser"));
  }
}
