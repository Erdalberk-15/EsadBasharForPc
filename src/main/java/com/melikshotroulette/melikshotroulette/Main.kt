package com.melikshotroulette.melikshotroulette

import javafx.application.Application
import javafx.stage.Stage

class Main : Application() {
    override fun start(primaryStage: Stage) {
        // Check Java version before starting the game
        if (!JavaVersionChecker.checkJavaVersion()) {
            return
        }
        
        val gameController = GameController(primaryStage)
        gameController.showMainMenu()
    }
}

fun main() {
    // Early check before JavaFX initialization
    val javaVersion = System.getProperty("java.version")
    val majorVersion = try {
        if (javaVersion.startsWith("1.")) {
            javaVersion.split(".")[1].toInt()
        } else {
            javaVersion.split(".")[0].toInt()
        }
    } catch (e: Exception) {
        0
    }
    
    if (majorVersion < 17) {
        println("Java version $javaVersion is too old. Java 17 or higher is required.")
        println("Please download Java 17 from: https://www.oracle.com/java/technologies/downloads/#java17")
        
        // Try to open browser
        try {
            val os = System.getProperty("os.name").lowercase()
            val url = "https://www.oracle.com/java/technologies/downloads/#java17"
            val runtime = Runtime.getRuntime()
            
            when {
                os.contains("win") -> {
                    runtime.exec(arrayOf("rundll32", "url.dll,FileProtocolHandler", url))
                }
                os.contains("mac") -> {
                    runtime.exec(arrayOf("open", url))
                }
                os.contains("nix") || os.contains("nux") -> {
                    runtime.exec(arrayOf("xdg-open", url))
                }
            }
        } catch (e: Exception) {
            // Ignore browser open errors
        }
        
        System.exit(1)
        return
    }
    
    Application.launch(Main::class.java)
}
