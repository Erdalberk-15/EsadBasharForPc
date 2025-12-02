package com.esadbashar.esadbashar

import javafx.application.Platform
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.awt.Desktop
import java.net.URI

object JavaVersionChecker {
    
    fun checkJavaVersion(): Boolean {
        val javaVersion = System.getProperty("java.version")
        val majorVersion = extractMajorVersion(javaVersion)
        
        println("Current Java version: $javaVersion (Major: $majorVersion)")
        
        if (majorVersion < 17) {
            showJavaUpdateDialog(javaVersion)
            return false
        }
        
        return true
    }
    
    private fun extractMajorVersion(version: String): Int {
        return try {
            // Handle versions like "1.8.0_xxx" (Java 8)
            if (version.startsWith("1.")) {
                version.split(".")[1].toInt()
            } else {
                // Handle versions like "11.0.x", "17.0.x"
                version.split(".")[0].toInt()
            }
        } catch (e: Exception) {
            0
        }
    }
    
    private fun showJavaUpdateDialog(currentVersion: String) {
        Platform.runLater {
            val alert = Alert(Alert.AlertType.WARNING)
            alert.title = "Java Update Required"
            alert.headerText = "Outdated Java Version Detected"
            alert.contentText = """
                Current Java version: $currentVersion
                Required: Java 17 or higher
                
                This game requires Java 17 or newer to run properly.
                Would you like to download Java 17 now?
            """.trimIndent()
            
            val downloadButton = ButtonType("Download Java 17")
            val cancelButton = ButtonType("Cancel")
            
            alert.buttonTypes.setAll(downloadButton, cancelButton)
            
            val result = alert.showAndWait()
            
            if (result.isPresent && result.get() == downloadButton) {
                openJavaDownloadPage()
            }
            
            Platform.exit()
            System.exit(0)
        }
    }
    
    private fun openJavaDownloadPage() {
        try {
            val javaDownloadUrl = "https://www.oracle.com/java/technologies/downloads/#java17"
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI(javaDownloadUrl))
            } else {
                // Fallback for systems without Desktop support
                val os = System.getProperty("os.name").lowercase()
                val runtime = Runtime.getRuntime()
                
                when {
                    os.contains("win") -> {
                        runtime.exec(arrayOf("rundll32", "url.dll,FileProtocolHandler", javaDownloadUrl))
                    }
                    os.contains("mac") -> {
                        runtime.exec(arrayOf("open", javaDownloadUrl))
                    }
                    os.contains("nix") || os.contains("nux") -> {
                        runtime.exec(arrayOf("xdg-open", javaDownloadUrl))
                    }
                }
            }
        } catch (e: Exception) {
            println("Failed to open browser: ${e.message}")
            showManualDownloadDialog()
        }
    }
    
    private fun showManualDownloadDialog() {
        Platform.runLater {
            val alert = Alert(Alert.AlertType.INFORMATION)
            alert.title = "Manual Download Required"
            alert.headerText = "Please Download Java 17 Manually"
            alert.contentText = """
                Please visit this URL to download Java 17:
                
                https://www.oracle.com/java/technologies/downloads/#java17
                
                Or search for "Java 17 download" in your browser.
            """.trimIndent()
            
            alert.showAndWait()
        }
    }
}
