package com.melikshotroulette.melikshotroulette.views

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

class MainMenuView(
    onSinglePlayer: () -> Unit,
    onTwoPlayer: () -> Unit,
    onFourPlayer: () -> Unit,
    onUpdateLogs: () -> Unit,
    onExit: () -> Unit
) {
    val scene: Scene
    
    init {
        val root = VBox(30.0)
        root.alignment = Pos.CENTER
        root.style = "-fx-background-color: #1a1a1a;"
        
        val title = Label("MELIKSHOT ROULETTE")
        title.font = Font.font("Arial", FontWeight.BOLD, 48.0)
        title.textFill = Color.web("#ff4444")
        
        val subtitle = Label("2D Edition")
        subtitle.font = Font.font("Arial", FontWeight.NORMAL, 24.0)
        subtitle.textFill = Color.web("#cccccc")
        
        val singlePlayerBtn = createMenuButton("Single Player")
        singlePlayerBtn.setOnAction { onSinglePlayer() }
        
        val twoPlayerBtn = createMenuButton("Two Players")
        twoPlayerBtn.setOnAction { onTwoPlayer() }
        
        val fourPlayerBtn = createMenuButton("Four Players")
        fourPlayerBtn.setOnAction { onFourPlayer() }
        
        val updateLogsBtn = createMenuButtonWithIcon("Update Logs", "/com/melikshotroulette/melikshotroulette/images/logs.png")
        updateLogsBtn.setOnAction { onUpdateLogs() }
        
        val exitBtn = createMenuButton("Exit")
        exitBtn.setOnAction { onExit() }
        
        val description = Label("Survive the deadly game of chance.\nPull the trigger and hope for an empty chamber.")
        description.font = Font.font("Arial", 14.0)
        description.textFill = Color.web("#888888")
        description.style = "-fx-text-alignment: center;"
        
        root.children.addAll(title, subtitle, description, singlePlayerBtn, twoPlayerBtn, fourPlayerBtn, updateLogsBtn, exitBtn)
        
        scene = Scene(root, 1000.0, 700.0)
    }
    
    private fun createMenuButton(text: String): Button {
        val button = Button(text)
        button.prefWidth = 300.0
        button.prefHeight = 60.0
        button.font = Font.font("Arial", FontWeight.BOLD, 20.0)
        button.style = """
            -fx-background-color: #ff4444;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """.trimIndent()
        
        button.setOnMouseEntered {
            button.style = """
                -fx-background-color: #ff6666;
                -fx-text-fill: white;
                -fx-background-radius: 10;
                -fx-cursor: hand;
            """.trimIndent()
        }
        
        button.setOnMouseExited {
            button.style = """
                -fx-background-color: #ff4444;
                -fx-text-fill: white;
                -fx-background-radius: 10;
                -fx-cursor: hand;
            """.trimIndent()
        }
        
        return button
    }
    
    private fun createMenuButtonWithIcon(text: String, iconPath: String): Button {
        val button = Button()
        button.prefWidth = 300.0
        button.prefHeight = 60.0
        button.font = Font.font("Arial", FontWeight.BOLD, 20.0)
        
        // Create button content with icon and text
        val content = HBox(10.0)
        content.alignment = Pos.CENTER
        
        // Try to load icon
        try {
            val iconStream = javaClass.getResourceAsStream(iconPath)
            if (iconStream != null) {
                val icon = Image(iconStream)
                val iconView = ImageView(icon)
                iconView.fitWidth = 32.0
                iconView.fitHeight = 32.0
                iconView.isPreserveRatio = true
                content.children.add(iconView)
            }
        } catch (e: Exception) {
            // Icon not found, just show text
        }
        
        val label = Label(text)
        label.font = Font.font("Arial", FontWeight.BOLD, 20.0)
        label.textFill = Color.WHITE
        content.children.add(label)
        
        button.graphic = content
        button.style = """
            -fx-background-color: #ff4444;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """.trimIndent()
        
        button.setOnMouseEntered {
            button.style = """
                -fx-background-color: #ff6666;
                -fx-text-fill: white;
                -fx-background-radius: 10;
                -fx-cursor: hand;
            """.trimIndent()
        }
        
        button.setOnMouseExited {
            button.style = """
                -fx-background-color: #ff4444;
                -fx-text-fill: white;
                -fx-background-radius: 10;
                -fx-cursor: hand;
            """.trimIndent()
        }
        
        return button
    }
}
