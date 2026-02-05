/*
 * Copyright (c) 2026 Singular
 * SPDX-License-Identifier: MIT
 */

package ai.singlr.scimsql;

import java.util.function.Function;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;

public class ScimEngine {

  public Filter parseFilter(
      String filterExpression,
      String prefix,
      Function<ComparisonFilter, ComparisonFilter> compareFilterBuilder) {
    try {
      CharStream input = CharStreams.fromString(filterExpression);
      ScimLexer lexer = new ScimLexer(input);
      ScimParser parser = getScimParser(lexer);

      ScimParser.QueryContext tree = parser.query();
      return new ScimEvaluator(prefix, compareFilterBuilder).visit(tree);
    } catch (ParseCancellationException e) {
      throw new IllegalArgumentException("Failed to parse filter: " + e.getMessage());
    }
  }

  private static ScimParser getScimParser(ScimLexer lexer) {
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    ScimParser parser = new ScimParser(tokens);

    parser.removeErrorListeners();
    parser.addErrorListener(
        new BaseErrorListener() {
          @Override
          public void syntaxError(
              Recognizer<?, ?> recognizer,
              Object offendingSymbol,
              int line,
              int charPositionInLine,
              String msg,
              RecognitionException e) {
            throw new ParseCancellationException(
                "Invalid filter syntax at position " + charPositionInLine + ": " + msg);
          }
        });
    return parser;
  }
}
