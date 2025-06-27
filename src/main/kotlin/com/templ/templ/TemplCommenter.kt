package com.templ.templ

import com.intellij.lang.Commenter
import org.jetbrains.annotations.Nullable


internal class TemplCommenter : Commenter {
    override fun getLineCommentPrefix(): String {
        return "//"
    }

    override fun getBlockCommentPrefix(): String {
        return "/*"
    }

    @Nullable
    override fun getBlockCommentSuffix(): String {
        return "*/"
    }

    @Nullable
    override fun getCommentedBlockCommentPrefix(): String? {
        return null
    }

    @Nullable
    override fun getCommentedBlockCommentSuffix(): String? {
        return null
    }
}
