package com.templ.templ.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;import q.H.H.I;

import static com.intellij.psi.TokenType.*;
import static com.templ.templ.psi.TemplTypes.*;

%%

%{
    private int braceNestingLevel = 0;
    private int parensNestingLevel = 0;
    private int previousState = -1;

    public void yypopState() { // We only remember one level of state.
        if (previousState == -1) {
          throw new IllegalStateException("No previous state to pop");
        }
        yybegin(previousState);
        previousState = -1;
    }

    public _TemplLexer() {
        this((java.io.Reader)null);
    }
%}

%public
%class _TemplLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

WHITE_SPACE=\s+
OPTIONAL_WHITE_SPACE=\s*

%state IN_TEMPL_DECLARATION_START
%state IN_TEMPL_DECLARATION_BODY
%state IN_CSS_DECLARATION_START
%state IN_CSS_DECLARATION_PARAMS
%state IN_CSS_DECLARATION_BODY
%state IN_CSS_DECLARATION_END
%state IN_SCRIPT_DECLARATION_START
%state IN_SCRIPT_DECLARATION_BODY
%state IN_SCRIPT_DECLARATION_END
%state IN_TEMPL_BLOCK_END
%state IN_BOOL_EXPR
%state IN_EXPR
%state IN_IF_STMT
%state IN_ELSE_IF_STMT
%state IN_ELSE_STMT
%state IN_SWITCH_STMT
%state IN_DEFAULT_STMT
%state IN_FOR_STMT
%state IN_COMPONENT_IMPORT
%state IN_COMPONENT_IMPORT_PARAMS
%state IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN
%state IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START
%state IN_END_RBRACE

%%

<YYINITIAL> {
    ^ "templ" {
        yybegin(IN_TEMPL_DECLARATION_START);
        return HTML_DECL_START;
    }

    ^ "css" {
        yybegin(IN_CSS_DECLARATION_START);
        return CSS_DECL_START;
    }

    ^ "script" {
        yybegin(IN_SCRIPT_DECLARATION_START);
        return SCRIPT_DECL_START;
    }

    [^] {
        return GO_ROOT_FRAGMENT;
    }
}

<IN_TEMPL_DECLARATION_START> {
    "{" $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return LBRACE;
    }

    [^] {
        return DECL_GO_TOKEN;
    }
}

<IN_TEMPL_DECLARATION_BODY> {
    ^ {OPTIONAL_WHITE_SPACE} "if" ~"{" $ {
        return GO_IF_START_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else if" ~"{" $ {
        return GO_ELSE_IF_START_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else" {OPTIONAL_WHITE_SPACE} "{" {OPTIONAL_WHITE_SPACE} $ {
        return GO_ELSE_START_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "switch" {WHITE_SPACE} ~"{\n" {
        return GO_SWITCH_START_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "case" ~":" {OPTIONAL_WHITE_SPACE} $ {
        return GO_CASE_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "default:" {OPTIONAL_WHITE_SPACE} $ {
        return GO_DEFAULT_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "for" {WHITE_SPACE} ~"{" $ {
        return GO_FOR_START_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "@" {
        yypushback(1);
        yybegin(IN_COMPONENT_IMPORT);
        return HTML_FRAGMENT;
    }

    ">" {OPTIONAL_WHITE_SPACE} "@" {
        yypushback(1);
        yybegin(IN_COMPONENT_IMPORT);
        return HTML_FRAGMENT;
    }

    "?={" {
        yypushback(1); // IN_EXPR handles brace nesting
        yybegin(IN_BOOL_EXPR);
        return BOOL_EXPR_START;
    }

    "{" {
        yypushback(1); // IN_EXPR handles brace nesting
        yybegin(IN_EXPR);
    }

    ^ "}" {
        yybegin(YYINITIAL);
        return RBRACE;
    }


    "}" {
        return RBRACE;
    }

    [^] {
        return HTML_FRAGMENT;
    }
}

<IN_COMPONENT_IMPORT> {
    "@" {
        return COMPONENT_IMPORT_START;
    }

    [\w\.]+ "(" {
        yypushback(1);
        yybegin(IN_COMPONENT_IMPORT_PARAMS);
        return COMPONENT_REFERENCE;
    }

    [\w\.]+ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return COMPONENT_REFERENCE;
    }
}

<IN_COMPONENT_IMPORT_PARAMS> {
    "(" {
        parensNestingLevel++;
        if (parensNestingLevel == 1) {
            return LPARENTH;
        }
    }

    ")" {OPTIONAL_WHITE_SPACE} "{" {
        parensNestingLevel--;
        if (parensNestingLevel == 0) {
            yypushback(yylength());
            yybegin(IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START);
        }
    }

    ")" {
        parensNestingLevel--;
        if (parensNestingLevel == 0) {
            yypushback(1);
            yybegin(IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN);
        }
    }

    [^] {
        return GO_COMPONENT_IMPORT_PARAMS;
    }
}

<IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN> {
    ")" {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return RPARENTH;
    }
}

<IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START> {
    ")" {
        return RPARENTH;
    }

    {WHITE_SPACE} {
        return WHITE_SPACE;
    }

    "{" {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return LBRACE;
    }
}

<IN_EXPR> {
    "{" {
        braceNestingLevel++;
        if (braceNestingLevel == 1) {
            return LBRACE;
        }
    }

    "}" {
        braceNestingLevel--;
        if (braceNestingLevel == 0) {
            yybegin(IN_TEMPL_DECLARATION_BODY);
            return RBRACE;
        }
    }

    [^] {
          return GO_EXPR;
     }
}

<IN_CSS_DECLARATION_START> {
    "(" {
        yypushback(1); // IN_CSS_DECLARATION_PARAMS handles parens nesting
        yybegin(IN_CSS_DECLARATION_PARAMS);
    }

    \w+ { return CSS_CLASS_ID; }

    "{" $ {
        yybegin(IN_CSS_DECLARATION_BODY);
        return LBRACE;
    }

    {WHITE_SPACE} {
        return WHITE_SPACE;
    }
}

<IN_CSS_DECLARATION_PARAMS> {
    "(" {
        parensNestingLevel++;
        if (parensNestingLevel == 1) {
            return LPARENTH;
        }
    }

    ")" {
        parensNestingLevel--;
        if (parensNestingLevel == 0) {
            yybegin(IN_CSS_DECLARATION_START);
            return RPARENTH;
        }
    }

    [^] {
        return GO_CSS_DECL_PARAMS;
    }
}

<IN_CSS_DECLARATION_BODY> {
    ^ "}" {
        yybegin(YYINITIAL);
        return RBRACE;
    }

    [^] {
        return CSS_PROPERTIES;
    }
}

<IN_SCRIPT_DECLARATION_START> {
    "{" $ {
        yybegin(IN_SCRIPT_DECLARATION_BODY);
        return SCRIPT_FUNCTION_DECL;
    }

    [^] {
        return SCRIPT_FUNCTION_DECL;
    }
}

<IN_SCRIPT_DECLARATION_BODY> {
    ^ "}" {
        yypushback(1);
        yybegin(YYINITIAL);
        return RBRACE;
    }

    [^] {
        return SCRIPT_BODY;
    }
}

. { return BAD_CHARACTER; }

