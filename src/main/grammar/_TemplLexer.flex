package com.templ.templ.parsing;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.*;
import static com.templ.templ.psi.TemplTypes.*;

%%

%{
    private boolean atEndOfFile = false;
    private int braceNestingLevel = 0;

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

EOL=\R
WHITE_SPACE=\s+
OPTIONAL_WHITE_SPACE=\s*

COMMENT=("//".*|"/"\*[^]*?\*"/")

%state IN_TEMPL_DECLARATION_START
%state IN_TEMPL_DECLARATION_BODY
%state IN_TEMPL_DECLARATION_END
%state IN_CSS_DECLARATION_START
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
%state IN_CASE_STMT
%state IN_DEFAULT_STMT
%state IN_FOR_STMT
%state IN_INLINE_COMPONENT
%state IN_CHILDREN_BLOCK_START
%state IN_END_RBRACE

%%
<YYINITIAL> {
    ^ "templ" {
        yypushback(5); // reverse back to start of "templ"
        yybegin(IN_TEMPL_DECLARATION_START);
        return GO_ROOT_FRAGMENT;
    }

    ^ "css" {
        yypushback(3); // reverse back to start of "templ"
        yybegin(IN_CSS_DECLARATION_START);
        return GO_ROOT_FRAGMENT;
    }

    ^ "script" {
        yypushback(6); // reverse back to start of "templ"
        yybegin(IN_SCRIPT_DECLARATION_START);
        return GO_ROOT_FRAGMENT;
    }

    <<EOF>> {
        // Without this atEndFile, we get an infinite loop that leads to OOM.
        if (this.atEndOfFile) {
            return null;
        } else {
            this.atEndOfFile = true;
            return GO_ROOT_FRAGMENT;
        }
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_TEMPL_DECLARATION_START> {
    ^ "templ" { return HTML_DECL_START; }

    "{" $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return DECL_GO_TOKEN;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_TEMPL_DECLARATION_BODY> {
    ^ {OPTIONAL_WHITE_SPACE} "if" {
        yypushback(2);
        yybegin(IN_IF_STMT);
        return HTML_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else if" {
        yypushback(yylength());
        yybegin(IN_ELSE_IF_STMT);
        return HTML_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else" {
        yypushback(yylength());
        yybegin(IN_ELSE_STMT);
        return HTML_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "switch" {
        yypushback(6);
        yybegin(IN_SWITCH_STMT);
        return HTML_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "case" {
        yypushback(4);
        yybegin(IN_CASE_STMT);
        return HTML_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "default:" {
        yypushback(8);
        yybegin(IN_DEFAULT_STMT);
        return HTML_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "for" {
        yypushback(3);
        yybegin(IN_FOR_STMT);
        return HTML_FRAGMENT;
    }

    ^ {OPTIONAL_WHITE_SPACE} "@" {
        yypushback(1);
        yybegin(IN_INLINE_COMPONENT);
        return HTML_FRAGMENT;
    }

    "?={" {
        yypushback(3);
        yybegin(IN_BOOL_EXPR);
        return HTML_FRAGMENT;
    }

    "{" {
        yypushback(1);
        yybegin(IN_EXPR);
        return HTML_FRAGMENT;
    }

    ^ "}" {
        yypushback(1);
        yybegin(IN_TEMPL_DECLARATION_END);
        return HTML_FRAGMENT;
    }

    "}" {
        yypushback(1);
        yybegin(IN_TEMPL_BLOCK_END);
        return HTML_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_TEMPL_BLOCK_END> {
    "}" {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return RBRACE;
    }
}

<IN_IF_STMT> {
    "{" {OPTIONAL_WHITE_SPACE} $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_IF_START_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_ELSE_IF_STMT> {
    "{" {OPTIONAL_WHITE_SPACE} $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_ELSE_IF_START_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_ELSE_STMT> {
    "{" {OPTIONAL_WHITE_SPACE} $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_ELSE_START_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_SWITCH_STMT> {
    "{" {OPTIONAL_WHITE_SPACE} $ {
        return GO_SWITCH_START_FRAGMENT;
    }

    // We exit IN_SWITCH_STMT state only when we see a case or default statement so that we don't emit HTML fragments.
    ^ {OPTIONAL_WHITE_SPACE} "case" {
        yypushback(4);
        yybegin(IN_CASE_STMT);
    }

    ^ {OPTIONAL_WHITE_SPACE} "default:" {
        yypushback(8);
        yybegin(IN_DEFAULT_STMT);
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_CASE_STMT> {
    ":" {OPTIONAL_WHITE_SPACE} $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_CASE_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_DEFAULT_STMT> {
    "default:" {OPTIONAL_WHITE_SPACE} $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_DEFAULT_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_FOR_STMT> {
    "{" {OPTIONAL_WHITE_SPACE} $ {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_FOR_START_FRAGMENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_INLINE_COMPONENT> {
    "@" {
        return INLINE_COMPONENT_START;
    }

    "{" {OPTIONAL_WHITE_SPACE} $ {
        yypushback(1);
        yybegin(IN_CHILDREN_BLOCK_START);
        return GO_INLINE_COMPONENT;
    }

    \n {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return GO_INLINE_COMPONENT;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_CHILDREN_BLOCK_START> {
    "{" {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return LBRACE;
    }
}

<IN_TEMPL_DECLARATION_END> {
    "}" {
        yybegin(YYINITIAL);
        return RBRACE;
    }
}

<IN_BOOL_EXPR> {
    "?=" {
        yybegin(IN_EXPR);
        return BOOL_EXPR_START;
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
            yypushback(1);
            yybegin(IN_END_RBRACE);
            return GO_EXPR;
        }
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_END_RBRACE> {
    "}" {
        yybegin(IN_TEMPL_DECLARATION_BODY);
        return RBRACE;
    }
}

<IN_CSS_DECLARATION_START> {
    ^ "css" { return CSS_DECL_START; }

    "(" { return LPARENTH; }

    ")" { return RPARENTH; }

    \w+ { return CSS_CLASS_ID; }

    "{" $ {
        yybegin(IN_CSS_DECLARATION_BODY);
        return LBRACE;
    }

    {WHITE_SPACE} {
        return WHITE_SPACE;
    }
}

<IN_CSS_DECLARATION_BODY> {
    ^ "}" {
        yypushback(1);
        yybegin(IN_CSS_DECLARATION_END);
        return CSS_PROPERTIES;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_CSS_DECLARATION_END> {
    "}" {
        yybegin(YYINITIAL);
        return RBRACE;
    }
}

<IN_SCRIPT_DECLARATION_START> {
    ^ "script" { return SCRIPT_DECL_START; }

    "{" $ {
        yybegin(IN_SCRIPT_DECLARATION_BODY);
        return SCRIPT_FUNCTION_DECL;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_SCRIPT_DECLARATION_BODY> {
    ^ "}" {
        yypushback(1);
        yybegin(IN_SCRIPT_DECLARATION_END);
        return SCRIPT_BODY;
    }

    [^] { /* capture characters until we emit a token */ }
}

<IN_SCRIPT_DECLARATION_END> {
    "}" {
        yybegin(YYINITIAL);
        return RBRACE;
    }
}

. { return BAD_CHARACTER; }

