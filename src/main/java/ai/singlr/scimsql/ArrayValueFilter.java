/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

import java.util.List;
import java.util.stream.Collectors;

public record ArrayValueFilter(List<Filter> values, Context context) implements Filter {

  @Override
  public String toClause() {
    return values.stream().map(Filter::toClause).collect(Collectors.joining(", "));
  }
}
