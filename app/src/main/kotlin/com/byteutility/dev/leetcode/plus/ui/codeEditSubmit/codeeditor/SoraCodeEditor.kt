package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.codeeditor

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.viewinterop.AndroidView
import io.github.rosemoe.sora.widget.CodeEditor

@Composable
fun SoraCodeEditor(
    code: String,
    modifier: Modifier = Modifier,
    onCodeChange: (String) -> Unit,
    onEditorLoaded: (CodeEditor) -> Unit
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            CodeEditor(context).apply {
                onEditorLoaded(this)
                setUndoEnabled(true)
                subscribeAlways(io.github.rosemoe.sora.event.ContentChangeEvent::class.java) {
                    onCodeChange(text.toString())
                }

                subscribeAlways(io.github.rosemoe.sora.event.SelectionChangeEvent::class.java) {
                    onCodeChange(text.toString())
                }
            }
        },
        update = { view ->
            // Only update text if it's actually different to avoid cursor reset
            if (view.text.toString() != code) {
                view.setText(code)
                onCodeChange(code)
            }
        }
    )
}

fun CodeEditor.getTextFieldValue(): TextFieldValue {
    val editorText = this.text // This is of type Content

    // In Sora, cursor.left and cursor.right are absolute character offsets
    // If your version uses older cursor properties, use these:
    val selectionStart = this.cursor.left
    val selectionEnd = this.cursor.right

    // Ensure we don't exceed the text length to avoid crashes
    val safeStart = selectionStart.coerceIn(0, editorText.length)
    val safeEnd = selectionEnd.coerceIn(0, editorText.length)

    return TextFieldValue(
        text = editorText.toString(),
        selection = TextRange(safeStart, safeEnd)
    )
}
