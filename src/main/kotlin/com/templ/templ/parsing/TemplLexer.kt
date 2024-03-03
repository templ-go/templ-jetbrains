package com.templ.templ.parsing

import com.intellij.lexer.FlexAdapter
import com.intellij.lexer.MergingLexerAdapter
import com.intellij.psi.tree.TokenSet
import com.templ.templ.psi.TemplTypes

class TemplLexer : MergingLexerAdapter(
    FlexAdapter(_TemplLexer(null)),
    // Consecutive tokens of these types are merged to a single token.
    TokenSet.create(
        TemplTypes.HTML_FRAGMENT,
        TemplTypes.GO_ROOT_FRAGMENT,
        TemplTypes.DECL_GO_TOKEN,
        TemplTypes.GO_COMPONENT_IMPORT_PARAMS,
        TemplTypes.GO_EXPR,
        TemplTypes.GO_CSS_DECL_PARAMS,
        TemplTypes.CSS_PROPERTIES,
        TemplTypes.SCRIPT_FUNCTION_DECL,
        TemplTypes.SCRIPT_BODY,
        TemplTypes.BLOCK_COMMENT
    )
)
