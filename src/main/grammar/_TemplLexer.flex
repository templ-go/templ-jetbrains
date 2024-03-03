package com.templ.templ.parsing;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;
import org.bouncycastle.util.Arrays;
import com.intellij.util.containers.Stack;

import static com.intellij.psi.TokenType.*;
import static com.templ.templ.psi.TemplTypes.*;

%%

%{
    private int braceNestingLevel = 0;
    private int parensNestingLevel = 0;
    private Stack<Integer> stateStack = new Stack<>();

    public void yyPushState(int newState) {
        stateStack.push(yystate());
        yybegin(newState);
    }

    public void yyReplaceState(int newState) {
        // Since the top of the stack is the previous state we want to pop to when replacing
        // the current state, we don't need to do any stack manipulation here.
        yybegin(newState);
    }

    public void yyResetState(int newState) {
        stateStack.clear();
        stateStack.push(yystate());
        yybegin(newState);
    }

    public void yyPopState() {
        yybegin(stateStack.pop());
    }

    public int peekPreviousState() {
        return stateStack.peek();
    }

    // Resolves the default token for given state, e.g., GO_ROOT_FRAGMENT for YYNITIAL.
    public IElementType resolveStateDefaultToken(int state) {
        switch (state) {
            case YYINITIAL: return GO_ROOT_FRAGMENT;
            case IN_EXPR: return GO_EXPR;
            case IN_COMPONENT_IMPORT_PARAMS: return GO_COMPONENT_IMPORT_PARAMS;
            case IN_TEMPL_DECLARATION_START: return DECL_GO_TOKEN;
            default: throw new IllegalStateException("Unknown default token for state: " + state);
        }
    }

    public boolean isCommentEnabled() {
        return !Arrays.contains(new int[]{IN_BLOCK_COMMENT, IN_GO_STRING, IN_GO_RAW_STRING}, yystate());
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
%state IN_SCRIPT_DECLARATION_START
%state IN_SCRIPT_DECLARATION_BODY
%state IN_EXPR
%state IN_COMPONENT_IMPORT
%state IN_COMPONENT_IMPORT_PARAMS
%state IN_COMPONENT_IMPORT_PARAMS_END_WITHOUT_CHILDREN
%state IN_COMPONENT_IMPORT_CHILDREN_BLOCK_START
%state IN_BLOCK_COMMENT
%state IN_GO_STRING
%state IN_GO_RAW_STRING
%state IN_HTML_COMMENT
%state IN_HTML_TAG_OPENER

%%

"//" .* {
    if (isCommentEnabled()) {
        return LINE_COMMENT;
    }
    yypushback(yylength() - 1);
}

"/\*" {
    if (isCommentEnabled()) {
        yyPushState(IN_BLOCK_COMMENT);
        return BLOCK_COMMENT;
    }
    yypushback(yylength() - 1);
}

<IN_BLOCK_COMMENT> {
    "\*/" {
        yyPopState();
        return BLOCK_COMMENT;
    }

    [^] {
        return BLOCK_COMMENT;
    }
}

// Detect Go strings because comments aren't allowed inside them.
<YYINITIAL, IN_EXPR, IN_COMPONENT_IMPORT_PARAMS, IN_TEMPL_DECLARATION_START> {
    "\"" {
        yyPushState(IN_GO_STRING);
        return resolveStateDefaultToken(peekPreviousState());
    }

    "`" {
        yyPushState(IN_GO_RAW_STRING);
        return resolveStateDefaultToken(peekPreviousState());
    }
}

<IN_GO_STRING> {
    "\\\"" {
        // Ignore escaped quotes.
        return resolveStateDefaultToken(peekPreviousState());
    }

    "\"" {
        yyPopState();
        return resolveStateDefaultToken(yystate());
    }

    // Consume characters until string is terminated or newline is reached.
    . {
        return resolveStateDefaultToken(peekPreviousState());
    }
}

<IN_GO_RAW_STRING> {
    "`" {
        yyPopState();
        return resolveStateDefaultToken(yystate());
    }

    // Consume characters until raw string is terminated.
    [^] {
        return resolveStateDefaultToken(peekPreviousState());
    }
}

<YYINITIAL> {
    ^ "templ" {
        yyPushState(IN_TEMPL_DECLARATION_START);
        return HTML_DECL_START;
    }

    ^ "css" {
        yyPushState(IN_CSS_DECLARATION_START);
        return CSS_DECL_START;
    }

    ^ "script" {
        yyPushState(IN_SCRIPT_DECLARATION_START);
        return SCRIPT_DECL_START;
    }

    [^] {
        return GO_ROOT_FRAGMENT;
    }
}

<IN_TEMPL_DECLARATION_START> {
    "{" $ {
        yyReplaceState(IN_TEMPL_DECLARATION_BODY);
        return LBRACE;
    }

    [^] {
        return DECL_GO_TOKEN;
    }
}

<IN_TEMPL_DECLARATION_BODY, IN_HTML_TAG_OPENER> {
    ^ {OPTIONAL_WHITE_SPACE} "if" ~"{" $ {
        return GO_IF_START_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else if" ~"{" $ {
        return GO_ELSE_IF_START_FRAGMENT;
    }

    "}" {OPTIONAL_WHITE_SPACE} "else" {OPTIONAL_WHITE_SPACE} "{" {OPTIONAL_WHITE_SPACE} $ {
        return GO_ELSE_START_FRAGMENT;
    }

    "{" {
        yypushback(1); // IN_EXPR handles brace nesting
        yyPushState(IN_EXPR);
    }

    ^ "}" {
        yyResetState(YYINITIAL);
        return RBRACE;
    }

    "}" {
        return RBRACE;
    }
}

<IN_HTML_TAG_OPENER> {
    ">" {
        yyPopState();
        yypushback(1); // So that we can detect component imports "@" straight after ">".
    }

    "=\"" ~"\"" {
        // Skip over attribute value so that we don't detect keywords in it.
        return HTML_FRAGMENT;
    }

    "?={" {
        yypushback(1); // IN_EXPR handles brace nesting
        yyPushState(IN_EXPR);
        return BOOL_EXPR_START;
    }

    [^] {
        return HTML_FRAGMENT;
    }
}

<IN_TEMPL_DECLARATION_BODY> {
    "<!--" {
        yyPushState(IN_HTML_COMMENT);
        return HTML_FRAGMENT;
    }

    "<" {
        yyPushState(IN_HTML_TAG_OPENER);
        return HTML_FRAGMENT;
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
        yyPushState(IN_COMPONENT_IMPORT);
        return HTML_FRAGMENT;
    }

    ">" {OPTIONAL_WHITE_SPACE} "@" {
        yypushback(1);
        yyPushState(IN_COMPONENT_IMPORT);
        return HTML_FRAGMENT;
    }

    [^] {
        return HTML_FRAGMENT;
    }
}

<IN_HTML_COMMENT> {
    "-->" {
        yyPopState();
        return HTML_FRAGMENT;
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
        yyReplaceState(IN_COMPONENT_IMPORT_PARAMS);
        return COMPONENT_REFERENCE;
    }

    [\w\.]+ {
        yyPopState(); // IN_TEMPL_DECLARATION_BODY or IN_HTML_TAG_OPENER
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
        yyPopState(); // IN_TEMPL_DECLARATION_BODY
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
        yyPopState(); // IN_TEMPL_DECLARATION_BODY
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
            yyPopState(); // IN_TEMPL_DECLARATION_BODY or IN_HTML_TAG_OPENER
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
        yyPushState(IN_CSS_DECLARATION_PARAMS);
    }

    \w+ { return CSS_CLASS_ID; }

    "{" $ {
        yyPushState(IN_CSS_DECLARATION_BODY);
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
            yyPopState(); // IN_CSS_DECLARATION_START
            return RPARENTH;
        }
    }

    [^] {
        return GO_CSS_DECL_PARAMS;
    }
}

<IN_CSS_DECLARATION_BODY> {
    ^ "}" {
        yyResetState(YYINITIAL);
        return RBRACE;
    }

    [^] {
        return CSS_PROPERTIES;
    }
}

<IN_SCRIPT_DECLARATION_START> {
    "{" $ {
        yyReplaceState(IN_SCRIPT_DECLARATION_BODY);
        return SCRIPT_FUNCTION_DECL;
    }

    [^] {
        return SCRIPT_FUNCTION_DECL;
    }
}

<IN_SCRIPT_DECLARATION_BODY> {
    ^ "}" {
        yypushback(1);
        yyResetState(YYINITIAL);
        return RBRACE;
    }

    [^] {
        return SCRIPT_BODY;
    }
}

[^] {
    yyResetState(YYINITIAL);
    return BAD_CHARACTER;
}

