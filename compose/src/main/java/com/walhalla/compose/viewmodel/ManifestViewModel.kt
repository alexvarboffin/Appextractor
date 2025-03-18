package com.walhalla.compose.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.walhalla.appextractor.model.PackageMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Document
import java.io.StringWriter
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class ManifestViewModel(application: Application) : AndroidViewModel(application) {
    private val _manifestContent = MutableStateFlow<String?>(null)
    val manifestContent: StateFlow<String?> = _manifestContent.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadManifest(app: PackageMeta) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val context = getApplication<Application>()
                val packageManager = context.packageManager
                val apkFile = File(app.sourceDir ?: throw Exception("Source directory not found"))

                val manifestXml = extractManifest(apkFile)
                _manifestContent.value = formatXml(manifestXml)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load manifest"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun extractManifest(apkFile: File): Document {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        
        // Используем AAPT для извлечения манифеста
        val process = Runtime.getRuntime().exec(
            arrayOf(
                "aapt",
                "dump",
                "xmltree",
                apkFile.absolutePath,
                "AndroidManifest.xml"
            )
        )
        
        val manifestXml = process.inputStream.bufferedReader().use { it.readText() }
        return builder.parse(manifestXml)
    }

    private fun formatXml(document: Document): String {
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty("indent", "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        
        val writer = StringWriter()
        transformer.transform(DOMSource(document), StreamResult(writer))
        return writer.toString()
    }
} 