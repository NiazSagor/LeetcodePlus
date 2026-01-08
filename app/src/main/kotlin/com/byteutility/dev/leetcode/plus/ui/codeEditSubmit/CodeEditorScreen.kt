package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.codeeditor.SoraCodeEditor
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.codeeditor.SymbolShortcutBar
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.config.EditorLanguageHelper
import com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel.CodeEditorSubmitViewModel
import com.byteutility.dev.leetcode.plus.ui.common.OnLifecycleEvent
import io.github.rosemoe.sora.widget.CodeEditor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeEditorScreen(
    viewModel: CodeEditorSubmitViewModel,
    title: String,
    initialCode: String,
    language: String,
    questionId: String?,
    onBack: () -> Unit,
) {
    var currentCode by remember { mutableStateOf(initialCode) }
    val emeraldGreen = Color(0xFF498A5C)
    var editor: CodeEditor? by remember { mutableStateOf(null) }

    OnLifecycleEvent { _, event ->
        if (event == Lifecycle.Event.ON_STOP) {
            viewModel.saveCode(questionId!!, language, editor?.text.toString())
        }

        if (event == Lifecycle.Event.ON_DESTROY) {
            editor?.release()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            language,
                            style = MaterialTheme.typography.labelSmall,
                            color = emeraldGreen
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 4.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { /* Run Test Cases */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Run")
                    }

                    Button(
                        onClick = {
                            viewModel.submit(
                                titleSlug = title,
                                language = language,
                                code = currentCode,
                                questionId = questionId!!
                            )
                        },
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldGreen)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Submit Code", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            SymbolShortcutBar(onSymbolClick = { symbol ->
                editor?.insertText(symbol, 1)
            })
            SoraCodeEditor(
                code = currentCode,
                language = language,
                onEditorLoaded = { loadedEditor ->
                    editor = loadedEditor
                    configureEditorLanguage(loadedEditor, language)
                },
                onCodeChange = { currentCode = it }
            )
        }
    }
}

private fun configureEditorLanguage(codeEditor: CodeEditor, language: String?) {
    language?.let { lang ->
        val success = EditorLanguageHelper.configureEditor(codeEditor, lang)
        if (!success) {
            Log.i("CodeEditorSubmit", "Failed to configure editor for language: $lang")
        }
    } ?: run {
        Log.i("CodeEditorSubmit", "No language specified, using default editor configuration")
    }
}
