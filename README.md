# scim-sql

SCIM filter expression to parameterized SQL converter for **PostgreSQL**. Parses [RFC 7644](https://datatracker.ietf.org/doc/html/rfc7644#section-3.4.2.2) filter strings and produces SQL WHERE clauses with named parameters — safe from injection by design.

The generated SQL uses PostgreSQL-specific syntax for typed values (`CAST(… AS UUID)`, `CAST(… AS timestamptz)`, `CAST(… AS jsonb)`, `@>` for JSON containment). Standard comparisons (`=`, `!=`, `LIKE`, `IN`, `IS NOT NULL`) are portable across databases. The `compareFilterBuilder` extension point allows overriding SQL generation for other databases.

## Quick Start

Add the dependency:

```xml
<dependency>
    <groupId>ai.singlr</groupId>
    <artifactId>scim-sql</artifactId>
    <version>1.0.0</version>
</dependency>
```

Parse a SCIM filter into SQL:

```java
var engine = new ScimEngine();
var filter = engine.parseFilter("userName eq \"john\"", "p", null);

filter.toClause();
// → "user_name = :p_userName1"

filter.context().indexedParams();
// → {p_userName1=john}
```

The `prefix` parameter namespaces all generated parameter keys, making it safe to combine multiple parsed filters in a single query.

## Supported Operators

| SCIM Operator | SQL Output | Example |
|--------------|------------|---------|
| `eq` | `=` | `name eq "John"` → `name = :name1` |
| `ne` | `!=` | `name ne "John"` → `name != :name1` |
| `gt` | `>` | `age gt 21` → `age > :age1` |
| `lt` | `<` | `age lt 65` → `age < :age1` |
| `ge` | `>=` | `age ge 18` → `age >= :age1` |
| `le` | `<=` | `age le 99` → `age <= :age1` |
| `co` | `LIKE '%…%'` | `name co "oh"` → `LOWER(name) LIKE '%' \|\| LOWER(:name1) \|\| '%'` |
| `sw` | `LIKE '…%'` | `name sw "J"` → `LOWER(name) LIKE LOWER(:name1) \|\| '%'` |
| `ew` | `LIKE '%…'` | `name ew "n"` → `LOWER(name) LIKE '%' \|\| LOWER(:name1)` |
| `pr` | `IS NOT NULL` | `name pr` → `name IS NOT NULL` |
| `in` | `IN (…)` | `status in ["active", "pending"]` → `status IN (:status1, :status2)` |

## Logical Operators

Combine filters with `and`, `or`, `not`, and parentheses:

```java
engine.parseFilter("name eq \"John\" and age gt 21", "p", null).toClause();
// → "name = :p_name1 AND age > :p_age1"

engine.parseFilter("not (active eq true)", "p", null).toClause();
// → "NOT (active = :p_active1)"

engine.parseFilter("(a eq 1 or b eq 2) and c eq 3", "p", null).toClause();
// → "(a = :p_a1 OR b = :p_b1) AND c = :p_c1"
```

## Typed Value Prefixes

Values can carry type hints that produce SQL `CAST` expressions. Prefix the value inside the quotes:

| Prefix | Type | SQL Cast | Example Value |
|--------|------|----------|---------------|
| `#` | UUID | `CAST(… AS UUID)` | `"#550e8400-e29b-41d4-a716-446655440000"` |
| `@` | Timestamp | `CAST(… AS timestamptz)` | `"@2026-01-15T10:30:00Z"` |
| `$` | JSON | `CAST(… AS jsonb)` | `"${"key":"value"}"` |

```java
engine.parseFilter("id eq \"#550e8400-e29b-41d4-a716-446655440000\"", "p", null).toClause();
// → "id = CAST(:p_id1 AS UUID)"

engine.parseFilter("metadata eq \"${\"role\":\"admin\"}\"", "p", null).toClause();
// → "metadata @> CAST(:p_metadata1 AS jsonb)"
```

JSON equality uses PostgreSQL's `@>` (contains) operator instead of `=`.

## Attribute Name Conversion

CamelCase attribute names are automatically converted to snake_case column names:

- `userName` → `user_name`
- `createdAtUtc` → `created_at_utc`

Dotted paths (e.g. `name.familyName`) are preserved as-is in the clause and flattened to underscores in parameter keys.

## Parameter Binding

`Context` collects all parameter bindings as the filter tree is evaluated:

```java
var filter = engine.parseFilter("name eq \"John\" and age gt 21", "p", null);
var clause = filter.toClause();
var params = filter.context().indexedParams();

// Use with JDBC named parameters, JOOQ, or any query builder:
// clause  = "name = :p_name1 AND age > :p_age1"
// params  = {p_name1=John, p_age1=21}
```

Use `context().isValid(Set.of("name", "age"))` to allowlist which attributes callers are permitted to filter on.

## Custom Filter Builders

Override SQL generation for specific comparisons by passing a `compareFilterBuilder` function:

```java
var filter = engine.parseFilter(
    "tags eq \"admin\"",
    "p",
    cf -> new ComparisonFilter.ListFilter(cf)  // wraps as ListFilter
);
```

This lets you intercept `ComparisonFilter` instances and return a subclass with custom `toClause()` or `paramKey()` behavior.

## Building

Requires JDK 25+ and Maven.

```bash
mvn package
```

## Code Formatting

Uses [google-java-format](https://github.com/google/google-java-format) via Spotless (2-space indentation, no tabs).

```bash
mvn spotless:apply   # auto-format
mvn spotless:check   # verify (runs on build)
```

## License

[MIT](LICENSE)
