package com.templ.templ.parsing

import com.intellij.testFramework.LexerTestCase

class TemplLexerTest : LexerTestCase() {
    override fun getDirPath(): String {
        return "unused"
    }

    override fun createLexer() = TemplLexer()
    override fun getTestName(lowercaseFirstLetter: Boolean) = "TemplLexerTest"

    fun testSimple() {
        doTest(
            """
            package main
            
            templ hello() {
               <h1>Hello, world!</h1>
            }
            
            templ test() {
                <div>{ "test" }</div>
            }
            
            templ test2(enabled bool) {
                if enabled {
                    <div>enabled</div>
                }
                <div>{ "test" }</div>
            }
            
            func test() bool {
               return true
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' hello() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n   <h1>Hello, world!</h1>\n')
            TemplTokenType.} ('}')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' test() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <div>')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_EXPR (' "test" ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('</div>\n')
            TemplTokenType.} ('}')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' test2(enabled bool) ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.GO_IF_START_FRAGMENT ('    if enabled {')
            TemplTokenType.HTML_FRAGMENT ('\n        <div>enabled</div>\n    ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n    <div>')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_EXPR (' "test" ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('</div>\n')
            TemplTokenType.} ('}')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n\nfunc test() bool {\n   return true\n}')
            """.trimIndent()
        )
    }

    fun testIfElse() {
        doTest(
            """
            package main
            
            templ hello() {
               if true {
                   <h1>if</h1>
               } else if false {
                   <h1>else if</h1>
               } else {
                   <h1>else</h1>
               }
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' hello() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.GO_IF_START_FRAGMENT ('   if true {')
            TemplTokenType.HTML_FRAGMENT ('\n       <h1>if</h1>\n   ')
            TemplTokenType.GO_ELSE_IF_START_FRAGMENT ('} else if false {')
            TemplTokenType.HTML_FRAGMENT ('\n       <h1>else if</h1>\n   ')
            TemplTokenType.GO_ELSE_START_FRAGMENT ('} else {')
            TemplTokenType.HTML_FRAGMENT ('\n       <h1>else</h1>\n   ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testSwitch() {
        doTest(
            """
            package main
            
            templ hello(type string) {
                switch type {
                    case "case1":
                         <h1>case 1</h1>
                    case "case2":
                         <h1>case 2</h1>
                    default:
                         <h1>default</h1>
                    }
                }
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' hello(type string) ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.GO_SWITCH_START_FRAGMENT ('    switch type {\n')
            TemplTokenType.GO_CASE_FRAGMENT ('        case "case1":')
            TemplTokenType.HTML_FRAGMENT ('\n             <h1>case 1</h1>\n')
            TemplTokenType.GO_CASE_FRAGMENT ('        case "case2":')
            TemplTokenType.HTML_FRAGMENT ('\n             <h1>case 2</h1>\n')
            TemplTokenType.GO_DEFAULT_FRAGMENT ('        default:')
            TemplTokenType.HTML_FRAGMENT ('\n             <h1>default</h1>\n        ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n    ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testFor() {
        doTest(
            """
            package main
            
            templ nameList(items []Item) {
              <ul>
              for _, item := range items {
                <li>{ item.Name }</li>
              }
              </ul>
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' nameList(items []Item) ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n  <ul>\n')
            TemplTokenType.GO_FOR_START_FRAGMENT ('  for _, item := range items {')
            TemplTokenType.HTML_FRAGMENT ('\n    <li>')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_EXPR (' item.Name ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('</li>\n  ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n  </ul>\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testChildren() {
        doTest(
            """
            package main

            templ test4() {
                @test3("hello") {
                    <span>hello</span> 
                }
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' test4() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('test3')
            TemplTokenType.( ('(')
            TemplTokenType.GO_COMPONENT_IMPORT_PARAMS ('"hello"')
            TemplTokenType.) (')')
            TemplTokenType.COMPONENT_CHILDREN_START (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n        <span>hello</span> \n    ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testComplex() {
        doTest(
            """
            package main

            templ test3() {
                <h1>asdf</h1>
                @hello("case2")
                @test2() {
                    <button>
                        Click me
                    </button>
                }
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' test3() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <h1>asdf</h1>\n    ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('hello')
            TemplTokenType.( ('(')
            TemplTokenType.GO_COMPONENT_IMPORT_PARAMS ('"case2"')
            TemplTokenType.) (')')
            TemplTokenType.HTML_FRAGMENT ('\n    ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('test2')
            TemplTokenType.( ('(')
            TemplTokenType.) (')')
            TemplTokenType.COMPONENT_CHILDREN_START (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n        <button>\n            Click me\n        </button>\n    ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testNesting() {
        doTest(
            """
            package main
            
            import fmt

            templ wrapper(index int) {
                <div id={ fmt.Sprint(index) }>
                    { children... }
                </div>
            }

            templ template() {
                @wrapper(1) {
                    <div>hello</div>
                    @wrapper(2) {

                         @wrapper(3) {
                             child3
                             @wrapper(4)
                         }
                    }
                }
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\nimport fmt\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' wrapper(index int) ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <div id=')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_EXPR (' fmt.Sprint(index) ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('>\n        ')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_EXPR (' children... ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n    </div>\n')
            TemplTokenType.} ('}')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' template() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('wrapper')
            TemplTokenType.( ('(')
            TemplTokenType.GO_COMPONENT_IMPORT_PARAMS ('1')
            TemplTokenType.) (')')
            TemplTokenType.COMPONENT_CHILDREN_START (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n        <div>hello</div>\n        ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('wrapper')
            TemplTokenType.( ('(')
            TemplTokenType.GO_COMPONENT_IMPORT_PARAMS ('2')
            TemplTokenType.) (')')
            TemplTokenType.COMPONENT_CHILDREN_START (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n\n             ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('wrapper')
            TemplTokenType.( ('(')
            TemplTokenType.GO_COMPONENT_IMPORT_PARAMS ('3')
            TemplTokenType.) (')')
            TemplTokenType.COMPONENT_CHILDREN_START (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n                 child3\n                 ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('wrapper')
            TemplTokenType.( ('(')
            TemplTokenType.GO_COMPONENT_IMPORT_PARAMS ('4')
            TemplTokenType.) (')')
            TemplTokenType.HTML_FRAGMENT ('\n             ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n        ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n    ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testCss() {
        doTest(
            """
            package main
            
            css cssComponentGreen() {
                color: { red };
            }
            
            css loading(percent int) {
                width: { fmt.Sprintf("%d%%", percent) };
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.css ('css')
            WHITE_SPACE (' ')
            TemplTokenType.CSS_CLASS_ID ('cssComponentGreen')
            TemplTokenType.( ('(')
            TemplTokenType.) (')')
            WHITE_SPACE (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.CSS_PROPERTIES ('\n    color: { red };\n')
            TemplTokenType.} ('}')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n\n')
            TemplTokenType.css ('css')
            WHITE_SPACE (' ')
            TemplTokenType.CSS_CLASS_ID ('loading')
            TemplTokenType.( ('(')
            TemplTokenType.GO_CSS_DECL_PARAMS ('percent int')
            TemplTokenType.) (')')
            WHITE_SPACE (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.CSS_PROPERTIES ('\n    width: { fmt.Sprintf("%d%%", percent) };\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testComments() {
        doTest(
            """
            package main
            // line comment
            /*
            /* block comment
             */
            var str = "/* comment */ // \" ignored"
            var rawStr = `
            // line comment
            /* block comment */
            `
            templ test() {
                <!-- html comment -->
                // line comment
                /* block comment */
                <div>normal html</div>
            }
            
            templ ComplexCommentTest() {
                <!--
                This is a comment
                if (true) {
                   <div>hello</div>
                }
                -->
                <div
                    aria-label="test>"
                    class={
                        "a", /* templ.KV("b"}, false) */ "c",
                        templ.KV("d", true),
                    }
                    @click="alert('hello')"
                 >
                 /* Test3 */
                 Test4
                </div>
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n')
            TemplTokenType.LINE_COMMENT ('// line comment')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n')
            TemplTokenType.BLOCK_COMMENT ('/*\n/* block comment\n */')
            TemplTokenType.GO_ROOT_FRAGMENT ('\nvar str = "/* comment */ // \" ignored"\nvar rawStr = `\n// line comment\n/* block comment */\n`\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' test() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <!-- html comment -->\n    ')
            TemplTokenType.LINE_COMMENT ('// line comment')
            TemplTokenType.HTML_FRAGMENT ('\n    ')
            TemplTokenType.BLOCK_COMMENT ('/* block comment */')
            TemplTokenType.HTML_FRAGMENT ('\n    <div>normal html</div>\n')
            TemplTokenType.} ('}')
            TemplTokenType.GO_ROOT_FRAGMENT ('\n\n')
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' ComplexCommentTest() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <!--\n    This is a comment\n    if (true) {\n       <div>hello</div>\n    }\n    -->\n    <div\n        aria-label="test>"\n        class=')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_EXPR ('\n            "a", ')
            TemplTokenType.BLOCK_COMMENT ('/* templ.KV("b"}, false) */')
            TemplTokenType.GO_EXPR (' "c",\n            templ.KV("d", true),\n        ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n        @click="alert('hello')"\n     >\n     ')
            TemplTokenType.BLOCK_COMMENT ('/* Test3 */')
            TemplTokenType.HTML_FRAGMENT ('\n     Test4\n    </div>\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testScript() {
        doTest(
            """
            package main

            script test1() {
                console.log("hello")
            }
            """.trimIndent(),
            """
            TemplTokenType.GO_ROOT_FRAGMENT ('package main\n\n')
            TemplTokenType.script ('script')
            TemplTokenType.SCRIPT_FUNCTION_DECL (' test1() {')
            TemplTokenType.SCRIPT_BODY ('\n    console.log("hello")\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testInlineDataModel() {
        doTest(
            """
            templ Message() {
                <div>
                    @Data{
                        message: fmt.Sprintf("%s", "You can implement methods on a type."),
                    }.Method()
                </div>
            }
            """.trimIndent(),
            """
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' Message() ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <div>\n        ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('Data')
            TemplTokenType.{ ('{')
            TemplTokenType.GO_COMPONENT_STRUCT_LITERAL ('\n            message: fmt.Sprintf("%s", "You can implement methods on a type."),\n        ')
            TemplTokenType.} ('}')
            TemplTokenType.COMPONENT_REFERENCE ('.Method')
            TemplTokenType.( ('(')
            TemplTokenType.) (')')
            TemplTokenType.HTML_FRAGMENT ('\n    </div>\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

    fun testComponentParameterWithChildren() {
        doTest(
            """
            templ test(comp templ.Component) {
                <div>
                    @comp {
                        <div>Children</div>
                    }
                </div>
            }
            """.trimIndent(),
            """
            TemplTokenType.templ ('templ')
            TemplTokenType.DECL_GO_TOKEN (' test(comp templ.Component) ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n    <div>\n        ')
            TemplTokenType.@ ('@')
            TemplTokenType.COMPONENT_REFERENCE ('comp')
            TemplTokenType.COMPONENT_CHILDREN_START (' ')
            TemplTokenType.{ ('{')
            TemplTokenType.HTML_FRAGMENT ('\n            <div>Children</div>\n        ')
            TemplTokenType.} ('}')
            TemplTokenType.HTML_FRAGMENT ('\n    </div>\n')
            TemplTokenType.} ('}')
            """.trimIndent()
        )
    }

}
