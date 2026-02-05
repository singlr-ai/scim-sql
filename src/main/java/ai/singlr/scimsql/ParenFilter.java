/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

public record ParenFilter(Filter inner) implements Filter {

  @Override
  public Context context() {
    return inner.context();
  }

  @Override
  public String toClause() {
    return "(%s)".formatted(inner.toClause());
  }
}
