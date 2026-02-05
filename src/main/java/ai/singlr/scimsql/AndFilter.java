/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

public record AndFilter(Filter left, Filter right) implements Filter {

  @Override
  public String toClause() {
    return String.format("%s AND %s", left.toClause(), right.toClause());
  }

  @Override
  public Context context() {
    return left.context();
  }
}
