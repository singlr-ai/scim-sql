/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

public record PresentFilter(Filter attribute) implements Filter {

  @Override
  public String toClause() {
    return "%s IS NOT NULL".formatted(Filter.camelToSnake(attribute.toClause()));
  }

  @Override
  public Context context() {
    return attribute.context();
  }
}
