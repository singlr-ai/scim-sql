/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

public record NotFilter(Filter filter) implements Filter {

  @Override
  public String toClause() {
    return String.format("NOT (%s)", filter.toClause());
  }

  @Override
  public Context context() {
    return filter.context();
  }
}
