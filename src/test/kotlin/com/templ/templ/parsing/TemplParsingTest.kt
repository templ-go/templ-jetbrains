package com.templ.templ.parsing

import com.intellij.testFramework.ParsingTestCase

class TemplParsingTest: ParsingTestCase("", "templ", TemplParserDefinition()) {
    fun testParsingTestBasicFeatures() {
        doTest(true)
    }

    fun testParsingTestComplexAttributes() {
        doTest(true)
    }

    fun testParsingTestCssUsage() {
        doTest(true)
    }

    override fun getTestDataPath(): String {
        return "src/test/testData"
    }

    override fun skipSpaces(): Boolean {
        return false
    }

    override fun includeRanges(): Boolean {
        return true
    }
}
