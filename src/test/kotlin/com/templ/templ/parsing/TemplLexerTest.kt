package com.templ.templ.parsing

import com.intellij.testFramework.LexerTestCase

class TemplLexerTest : LexerTestCase() {
    override fun getDirPath(): String {
        return "unused"
    }

    override fun createLexer() = TemplLexer()
    override fun getTestName(lowercaseFirstLetter: Boolean) = "TemplLexerTest"

    fun testComment() {
        doTest(
            """
            // comment 
            /*
             * comment
             */
            """.trimIndent(),
            """
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
            """.trimIndent()
        )
    }

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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
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
            TemplTokenType.COMMENT ('// comment ')
            WHITE_SPACE ('\n')
            TemplTokenType.COMMENT ('/*\n * comment\n */')
            """.trimIndent()
        )
    }
}
