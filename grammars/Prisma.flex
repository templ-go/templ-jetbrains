package org.intellij.prisma.lang.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.openapi.util.text.StringUtil;
import static com.intellij.psi.TokenType.*;

import static org.intellij.prisma.lang.psi.PrismaElementTypes.*;
import static org.intellij.prisma.lang.parser.PrismaParserDefinition.*;

%%

%{
  public _TemplLexer() {
    this((java.io.Reader)null);
  }
%}

%class _TemplLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

EOL_WS           = \n | \r | \r\n
LINE_WS          = [\ \t]
WHITE_SPACE_CHAR = {EOL_WS} | {LINE_WS}
WHITE_SPACE      = {WHITE_SPACE_CHAR}+
DIGIT            = [:digit:]

NAME_START       = [a-zA-Z]
NAME_BODY        = [a-zA-Z0-9_]
IDENTIFIER       = {NAME_START} ({NAME_BODY})*

STRING_LITERAL   = \"([^\\\"\r\n]|\\[^\r\n])*\"?
RUNE_LITERAL   = \'([^\\\'\r\n]|\\[^\r\n])\'?
NUMERIC_LITERAL  = "-"? {DIGIT}+ ("." {DIGIT}+)?

LINE_COMMENT = "//" .*

%state STRING

%%

/* keywords */
<YYINITIAL> "templ"           { return symbol(sym.TEMPL); }
<YYINITIAL> "css"           { return symbol(sym.CSS); }
<YYINITIAL> "script"           { return symbol(sym.SCRIPT); }
<YYINITIAL> "func"           { return symbol(sym.FUNC); }
<YYINITIAL> "var"           { return symbol(sym.VAR); }
<YYINITIAL> "const"           { return symbol(sym.CONST); }
<YYINITIAL> "type"           { return symbol(sym.TYPE); }
<YYINITIAL> "return"           { return symbol(sym.RETURN); }
<YYINITIAL> "struct"           { return symbol(sym.STRUCT); }
<YYINITIAL> "range"           { return symbol(sym.RANGE); }

<YYINITIAL> {
    /* identifiers */
    {IDENTIFIER}                   { return symbol(sym.IDENTIFIER); }

    /* literals */
    {NUMERIC_LITERAL}            { return symbol(sym.INTEGER_LITERAL); }
    \"                           { string.setLength(0); yybegin(STRING); }
    {RUNE_LITERAL}               { return symbol(sym.RUNE_LITERAL); }

    /* operators */
    "@"                            { return symbol(sym.CALL_TEMPLATE); }
    "="                            { return symbol(sym.EQ); }
    "=="                           { return symbol(sym.EQEQ); }
    "+"                            { return symbol(sym.PLUS); }
    ">"                            { return symbol(sym.GT); }
    "<"                            { return symbol(sym.LT); }
    "%"                            { return symbol(sym.MOD); }
    "/"                            { return symbol(sym.DIV); }
    "*"                            { return symbol(sym.PROD); }

    /* comments */
    {LINE_COMMENT}                      { /* ignore */ }

    /* whitespace */
    {WHITE_SPACE}                   { /* ignore */ }
  }
<STRING> {
  \"                             { yybegin(YYINITIAL);
                                   return symbol(sym.STRING_LITERAL,
                                   string.toString()); }
  [^\n\r\"\\]+                   { string.append( yytext() ); }
  \\t                            { string.append('\t'); }
  \\n                            { string.append('\n'); }

  \\r                            { string.append('\r'); }
  \\\"                           { string.append('\"'); }
  \\                             { string.append('\\'); }
}


/* error fallback */
[^]                              { throw new Error("Illegal character <"+
                                                    yytext()+">"); }