# Templ Jetbrains

<!-- Plugin description -->
Support for the [Templ Programming Language](https://templ.guide/)

## Features
- Basic LSP Support
- HTML editing support
<!-- Plugin description end -->

## Prerequisites
- [gopls](https://pkg.go.dev/golang.org/x/tools/gopls)
- [temple](https://templ.guide/quick-start/installation/)

## Tasks

### bundle-vscode

Directory: ./grammars

```
zip -rqq ../tm-bundle.zip *
mv ../tm-bundle.zip ../src/main/resources/
```

### Fix PSI parsing and lexing issues

This project uses [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit) for code generating the parser and lexer. A good starting point to understand how this works is following the Grammar-Kit documentation and the official [Custom Language Support Tutorial](https://plugins.jetbrains.com/docs/intellij/custom-language-support-tutorial.html). Another useful thing to know is that the HTML support is implemented with [TemplFileViewProvider](./src/main/kotlin/com/templ/templ/file/TemplFileViewProvider.kt) implementing the TemplateLanguageFileViewProvider following the [custom templating language plugin tutorial](https://intellij-support.jetbrains.com/hc/en-us/community/posts/206765105-Tutorial-Custom-templating-language-plugin). We use `HTML_FRAGMENT` Lexer tokens for the HTML parts of the templ files.

If this plugin does not parse Templ files correctly, a good starting point is to look at the output of "View PSI Structure of Current File..."-action to see how the file is parsed. If the Lexical token is incorrect at the point of the parsing error, you need to fix the lexer.

1. Edit the [_TemplLexer.flex](./src/main/grammar/_TemplLexer.flex) file. You might find the [JFlex manual](https://jflex.de/manual.html) useful.
2. Run JFlex Generator action to generate the _TemplLexer.java file.
3. Start the IDE with the `Run Plugin` configuration to see if the lexer works as expected. You can also run the [lexer test](./src/test/kotlin/com/templ/templ/parsing/TemplLexerTest.kt) and preferably add a new test case for the bug.

If the lexer token is correct, you need to modify the grammar which generates the parser code.

1. Edit the [TemplParser.bnf](./src/main/grammar/Templ.bnf) file.
2. Run the `Generate Parser Code` action to generate the parser code. This also generates the PSI classes but does not remove the old ones, so you might want to remove the `./src/main/gen/com/templ/templ/psi` directory before running this action.
3. Start the IDE with the `Run Plugin` configuration to see if the parser works as expected. You can also run the [parser test](./src/test/kotlin/com/templ/templ/parsing/TemplParsingTest.kt) and preferably add a new test case for the bug.
