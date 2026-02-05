/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

/**
 * SCIM filter expression to parameterized SQL converter.
 *
 * <p>Parses <a href="https://www.rfc-editor.org/rfc/rfc7644#section-3.4.2.2">SCIM filtering</a>
 * expressions and converts them to parameterized SQL WHERE clauses. Supports all SCIM comparison
 * operators (eq, ne, gt, lt, ge, le, co, sw, ew), logical operators (and, or, not), presence (pr),
 * and the in operator with typed values (UUID, timestamp, JSON, boolean, number, string).
 */
module ai.singlr.scimsql {
  requires org.antlr.antlr4.runtime;

  exports ai.singlr.scimsql;
}
