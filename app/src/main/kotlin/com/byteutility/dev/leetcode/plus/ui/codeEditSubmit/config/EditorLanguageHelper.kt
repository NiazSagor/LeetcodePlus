package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.config

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme

/**
 * Helper class to configure CodeEditor with language-specific settings.
 */
@Suppress("MagicNumber", "TooGenericExceptionCaught")
object EditorLanguageHelper {

    /**
     * Configures the CodeEditor with language-specific settings including
     * syntax highlighting, auto-indentation, and symbol pair completion.
     *
     * @param editor The CodeEditor instance to configure
     * @param languageSlug LeetCode language slug (e.g., "python3", "java")
     * @return true if configuration succeeded, false otherwise
     */
    fun configureEditor(editor: CodeEditor, languageSlug: String): Boolean {
        return try {
            val scopeName = LanguageMapper.getTextMateScopeName(languageSlug)
            val themeRegistry = ThemeRegistry.getInstance()
            editor.colorScheme = TextMateColorScheme.create(themeRegistry)
            editor.setEditorLanguage(TextMateLanguage.create(scopeName, true))

            editor.typefaceText = Typeface.MONOSPACE
            editor.setTextSize(15f)

            editor.setLineSpacing(0f, 1.2f)

            editor.colorScheme.setColor(
                EditorColorScheme.SELECTION_INSERT,
                Color.parseColor("#498A5C")
            )

            editor.props.apply {
                autoIndent = true
                symbolPairAutoCompletion = true
            }
            editor.setHighlightBracketPair(true)
            editor.tabWidth = getTabWidthForLanguage(languageSlug)
            true
        } catch (e: Exception) {
            Log.e("EditorConfig", "Configuration failed", e)
            false
        }
    }

    /**
     * Returns the appropriate tab width for different languages.
     * Different programming languages have different conventions for indentation.
     */
    private fun getTabWidthForLanguage(languageSlug: String): Int {
        return when (languageSlug.lowercase()) {
            "python", "python3" -> 4
            "java", "kotlin", "c", "cpp", "c++", "csharp", "c#" -> 4
            "javascript", "typescript" -> 2
            "go", "golang" -> 8 // Go traditionally uses tabs
            "ruby", "php" -> 2
            "swift" -> 4
            "rust" -> 4
            else -> 4 // Default to 4 spaces
        }
    }
}
