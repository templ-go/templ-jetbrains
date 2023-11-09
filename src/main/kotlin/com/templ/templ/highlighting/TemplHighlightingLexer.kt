package com.templ.templ.highlighting

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.util.containers.Interner
import com.templ.templ.TemplFileType
import org.jetbrains.plugins.textmate.language.TextMateLanguageDescriptor
import org.jetbrains.plugins.textmate.language.syntax.TextMateSyntaxTable
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateHighlightingLexer
import java.io.*
import java.nio.file.Path
import java.util.zip.ZipInputStream
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.textmate.bundles.readTextMateBundle
import org.jetbrains.plugins.textmate.language.syntax.lexer.TextMateElementType


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
    println("EXTRACT")
    try {
        while (true) {
            val entry = zip.nextEntry ?: break
            val file = File(target, entry.getName())
            if (!file.toPath().normalize().startsWith(target.toPath())) {
                throw IOException("Bad zip entry")
            }
            if (entry.isDirectory()) {
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
        val plugin = PluginManagerCore.getPlugin(PluginId.getId("com.templ.templ"))
        val version = plugin?.version ?: "devel"
        val bundleDirectory = File(plugin?.pluginPath.toString() + "/bundles/" + version)
        if (!bundleDirectory.exists()) {
            deleteFile(bundleDirectory.getParentFile())
            bundleDirectory.mkdirs()
            val resource = TemplFileType::class.java.classLoader.getResourceAsStream("tm-bundle.zip")

            extract(ZipInputStream(resource!!), bundleDirectory)
        }
        return Path.of(bundleDirectory.path)
    } catch (ex: IOException) {
        throw UncheckedIOException(ex)
    }
}
fun getTextMateLanguageDescriptor(): TextMateLanguageDescriptor {
    try {
        val bundle = readTextMateBundle(getBundlePath())
        val syntax = TextMateSyntaxTable();
        val interner = Interner.createWeakInterner<CharSequence>();
        val grammars = bundle.readGrammars()
        for (g in grammars) {
            println("SYNTAX LOADED: "+syntax.loadSyntax(g.plist.value, interner));
        }
        return TextMateLanguageDescriptor("source.templ", syntax.getSyntax("source.templ"))
    } catch (ex: Exception) {
        throw RuntimeException(ex)
    }
}

class TemplHighlightingLexer() : TextMateHighlightingLexer(getTextMateLanguageDescriptor(), 20000) {
    override fun getTokenType(): IElementType? {
        val tt = super.getTokenType() ?: return null
        return TemplElementType((tt as TextMateElementType).scope)
    }
}