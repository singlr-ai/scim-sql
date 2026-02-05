/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

public record AttributeFilter(String name, Filter subAttribute, String prefix, Context context)
    implements Filter {

  @Override
  public String toClause() {
    if (subAttribute == null) {
      return prefix.isEmpty() ? name : prefix + "." + name;
    }
    return name + "." + subAttribute.toClause();
  }

  @Override
  public String toString() {
    if (subAttribute == null) {
      return name;
    }
    return name + "." + subAttribute.toString();
  }
}
