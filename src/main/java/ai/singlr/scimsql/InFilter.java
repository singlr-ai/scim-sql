/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

public record InFilter(Filter attribute, ArrayValueFilter arrayValue, Context context)
    implements Filter {

  @Override
  public String toClause() {
    var key = Filter.camelToSnake(attribute.toClause());
    var inClause = context.processArray(attribute, arrayValue.values(), this::paramKey);
    return key + " IN (" + inClause + ")";
  }

  public String paramKey(String indexedKey) {
    return ":" + indexedKey;
  }
}
