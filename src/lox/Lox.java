package lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Lox {
  private static final Interpreter interpreter = new Interpreter();
  static boolean parseSilently = false;
  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  // For scripts, indicate an error in the exit code.
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));

    Parser parser = setupParser(new String(bytes, Charset.defaultCharset()));
    List<Stmt> statements = parser.parse();
    if (hadError) System.exit(65);

    Resolver resolver = new Resolver(interpreter);
    resolver.resolve(statements);
    if (hadError) System.exit(65);

    interpreter.interpret(statements);
    if (hadRuntimeError) System.exit(70);
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    while (true) {
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) break;

      Parser parser = setupParser(line);
      parseSilently = true;
      List<Stmt> statements = parser.parse();
      parseSilently = false;

      if (hadError) {
        parser = setupParser(line);
        Expr expression = parser.expression();
        statements = Arrays.asList(new Stmt.Print(expression));
      }

      if (!hadError) {
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
      }

      if (!hadError) {
        interpreter.interpret(statements);

        hadRuntimeError = false;
      }

      hadError = false;
    }

    System.out.println("\nexit");
  }

  private static Parser setupParser(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    return new Parser(tokens);
  }

  private static void report(int line, String where, String message) {
    hadError = true;

    if (!parseSilently) {
      System.err.println("[line " + line + "] Error" + where + ": " + message);
    }
  }

  static void error(int line, String message) {
    report(line, "", message);
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }
}
