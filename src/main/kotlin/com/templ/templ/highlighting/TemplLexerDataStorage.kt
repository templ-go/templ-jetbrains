package com.templ.templ.highlighting

import com.intellij.openapi.editor.ex.util.DataStorage
import com.intellij.openapi.editor.ex.util.ShortBasedStorage
import com.intellij.psi.tree.IElementType
import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateScope
import kotlin.math.abs


class TemplLexerDataStorage : ShortBasedStorage {
    private val tokenTypeMap: Object2IntMap<TemplElementType>
    private val tokenTypes: MutableList<TemplElementType>

    constructor() : this(Object2IntOpenHashMap<TemplElementType>(), ArrayList<TemplElementType>())
    private constructor( tokenTypeMap: Object2IntMap<TemplElementType>,
                         tokenTypes: MutableList<TemplElementType>) : super() {
        this.tokenTypeMap = tokenTypeMap
        this.tokenTypes = tokenTypes
    }

    private constructor(data:  ShortArray?,
                         tokenTypeMap: Object2IntMap<TemplElementType>,
                         tokenTypes: MutableList<TemplElementType>) : super(data) {
        this.tokenTypeMap = tokenTypeMap
        this.tokenTypes = tokenTypes
    }

    override fun packData(tokenType: IElementType, state: Int, isRestartableState: Boolean): Int {
        if (tokenType is TemplElementType) {
            synchronized(tokenTypeMap) {
                if (tokenTypeMap.containsKey(tokenType)) {
                    return tokenTypeMap.getInt(tokenType) * if (isRestartableState) 1 else -1
                }
                val data = tokenTypes.size + 1
                tokenTypes.add(tokenType)
                tokenTypeMap.put(tokenType, data)
                return if (isRestartableState) data else -data
            }
        }
        return 0
    }

    override fun unpackTokenFromData(data: Int): IElementType {
        return if (data != 0) tokenTypes[(abs(data.toDouble()) - 1).toInt()] else TemplElementType(TextMateScope.EMPTY)
    }

    override fun copy(): DataStorage {
        return TemplLexerDataStorage(myData, tokenTypeMap, tokenTypes)
    }

    override fun createStorage(): DataStorage {
        return TemplLexerDataStorage(tokenTypeMap, tokenTypes)
    }
}