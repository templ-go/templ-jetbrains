package com.templ.templ.highlighting

import com.intellij.openapi.application.PathManager
import com.intellij.psi.tree.IElementType
import com.intellij.textmate.joni.JoniRegexFactory
import com.templ.templ.TemplFileType
import org.jetbrains.plugins.textmate.language.TextMateConcurrentMapInterner
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor
import org.jetbrains.plugins.textmate.bundles.TextMateNioResourceReader
import org.jetbrains.plugins.textmate.bundles.readTextMateBundle
import org.jetbrains.plugins.textmate.language.syntax.TextMateSyntaxTableBuilder
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateHighlightingLexer
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateElementType
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateSyntaxMatcherImpl
import org.jetbrains.plugins.textmate.language.syntax.lexer.caching as cachingSyntaxMatcher
import org.jetbrains.plugins.textmate.language.syntax.selector.TextMateSelectorWeigherImpl
import org.jetbrains.plugins.textmate.language.syntax.selector.caching as cachingSelectorWeigher
import org.jetbrains.plugins.textmate.plist.JsonOrXmlOrYamlPlistReader
import org.jetbrains.plugins.textmate.plist.JsonPlistReader
import org.jetbrains.plugins.textmate.plist.XmlPlistReader
import org.jetbrains.plugins.textmate.regex.CaffeineCachingRegexProvider
import org.jetbrains.plugins.textmate.regex.RememberingLastMatchRegexFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.UncheckedIOException
import java.nio.file.Path
import java.security.MessageDigest
import java.util.HexFormat
import java.util.zip.ZipInputStream


private fun deleteFile(file: File) {
    val children = file.listFiles()
    if (children != null) {
        for (child in children) {
            deleteFile(child)
        }
    }
    file.delete()
}

@Throws(IOException::class)
private fun extract(zip: ZipInputStream, target: File) {
    try {
        while (true) {
            val entry = zip.nextEntry ?: break
            val file = File(target, entry.name)
            if (!file.toPath().normalize().startsWith(target.toPath())) {
                throw IOException("Bad zip entry")
            }
            if (entry.isDirectory) {
                file.mkdirs()
                continue
            }
            val buffer = ByteArray(4096)
            file.getParentFile().mkdirs()
            val out = BufferedOutputStream(FileOutputStream(file))
            var count: Int
            while (zip.read(buffer).also { count = it } != -1) {
                out.write(buffer, 0, count)
            }
            out.close()
        }
    } finally {
        zip.close()
    }
}

private fun getBundlePath(): Path {
    try {
        val resource = TemplFileType::class.java.classLoader.getResource("tm-bundle.zip")
            ?: error("TextMate bundle resource not found")
        val bundleHash = resource.openStream().use { input ->
            val digest = MessageDigest.getInstance("SHA-256").digest(input.readAllBytes())
            HexFormat.of().formatHex(digest)
        }
        val bundleDirectory = PathManager.getSystemDir()
            .resolve("templ")
            .resolve("textmate")
            .resolve(bundleHash)
            .toFile()
        if (!bundleDirectory.exists()) {
            deleteFile(bundleDirectory.parentFile)
            bundleDirectory.mkdirs()
            extract(ZipInputStream(resource.openStream()), bundleDirectory)
        }
        return Path.of(bundleDirectory.path)
    } catch (ex: IOException) {
        throw UncheckedIOException(ex)
    }
}

private val textMateLanguageDescriptor: TextMateLanguageDescriptor by lazy {
    try {
        val plistReader = JsonOrXmlOrYamlPlistReader(JsonPlistReader(), XmlPlistReader(), null)
        val bundle = readTextMateBundle(
            "templ",
            plistReader,
            TextMateNioResourceReader(getBundlePath()),
        )
        val syntaxBuilder = TextMateSyntaxTableBuilder(TextMateConcurrentMapInterner())
        for ((_, _, plist) in bundle.readGrammars()) {
            syntaxBuilder.addSyntax(plist.value)
        }
        syntaxBuilder.build().getLanguageDescriptor("source.templ")
    } catch (ex: Exception) {
        throw RuntimeException(ex)
    }
}

class TemplHighlightingLexer : TextMateHighlightingLexer(
    textMateLanguageDescriptor,
    TextMateSyntaxMatcherImpl(
        CaffeineCachingRegexProvider(RememberingLastMatchRegexFactory(JoniRegexFactory())),
        TextMateSelectorWeigherImpl().cachingSelectorWeigher(),
    ).cachingSyntaxMatcher(),
    20000,
) {
    override fun getTokenType(): IElementType? {
        val tt = super.getTokenType() ?: return null
        return TemplElementType((tt as TextMateElementType).scope)
    }
}
