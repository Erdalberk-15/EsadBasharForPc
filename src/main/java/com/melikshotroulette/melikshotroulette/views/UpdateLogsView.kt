package com.melikshotroulette.melikshotroulette.views

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

class UpdateLogsView(private val onBack: () -> Unit) {
    val scene: Scene
    
    private data class UpdateLog(
        val version: String,
        val date: String,
        val changes: List<String>
    )
    
    private val updates = listOf(
        UpdateLog(
            version = "v1.0.1",
            date = "November 25, 2024",
            changes = listOf(
                "Added 3 new characters: Benji, Anne Terliği, Amongus",
                "Fixed single-player mode crash bug",
                "Added background music with volume control",
                "Added gunshot sound effects",
                "Improved character selection screen layout",
                "Added console logging for debugging"
            )
        ),
        UpdateLog(
            version = "v1.0.0",
            date = "November 24, 2024",
            changes = listOf(
                "Initial release of Melikshot Roulette 2D",
                "Two game modes: Single Player and Multiplayer",
                "Character selection with 3 characters",
                "4 unique items: Shuffle, 2x Shot, +1 Life, 2x Damage",
                "Fullscreen mode support",
                "Custom exit screen with hidden button",
                "Health system with visual hearts",
                "Round-based gameplay with chamber mechanics"
            )
        )
    )
    
    init {
        val root = BorderPane()
        root.style = "-fx-background-color: #1a1a1a;"
        
        // Top - Title
        val topBox = VBox(10.0)
        topBox.alignment = Pos.CENTER
        topBox.padding = Insets(20.0)
        
        val title = Label("UPDATE LOGS")
        title.font = Font.font("Arial", FontWeight.BOLD, 42.0)
        title.textFill = Color.web("#ff4444")
        
        topBox.children.add(title)
        root.top = topBox
        
        // Center - Scrollable update logs
        val scrollPane = ScrollPane()
        scrollPane.isFitToWidth = true
        scrollPane.style = "-fx-background: #1a1a1a; -fx-background-color: #1a1a1a;"
        
        val logsContainer = VBox(20.0)
        logsContainer.padding = Insets(20.0, 40.0, 20.0, 40.0)
        logsContainer.alignment = Pos.TOP_CENTER
        
        // Add each update log
        updates.forEach { update ->
            val updateBox = createUpdateBox(update)
            logsContainer.children.add(updateBox)
        }
        
        scrollPane.content = logsContainer
        root.center = scrollPane
        
        // Bottom - Back button
        val bottomBox = HBox(20.0)
        bottomBox.alignment = Pos.CENTER
        bottomBox.padding = Insets(20.0)
        
        val backButton = createButton("Back to Menu", "#666666")
        backButton.setOnAction { onBack() }
        
        bottomBox.children.add(backButton)
        root.bottom = bottomBox
        
        scene = Scene(root, 1200.0, 700.0)
    }
    
    private fun createUpdateBox(update: UpdateLog): VBox {
        val box = VBox(15.0)
        box.padding = Insets(20.0)
        box.style = """
            -fx-background-color: #2a2a2a;
            -fx-background-radius: 15;
            -fx-border-color: #ff4444;
            -fx-border-width: 2;
            -fx-border-radius: 15;
        """.trimIndent()
        box.maxWidth = 900.0
        
        // Version and date header
        val headerBox = HBox(20.0)
        headerBox.alignment = Pos.CENTER_LEFT
        
        val versionLabel = Label(update.version)
        versionLabel.font = Font.font("Arial", FontWeight.BOLD, 28.0)
        versionLabel.textFill = Color.web("#ff8800")
        
        val dateLabel = Label(update.date)
        dateLabel.font = Font.font("Arial", FontWeight.NORMAL, 18.0)
        dateLabel.textFill = Color.web("#888888")
        
        headerBox.children.addAll(versionLabel, dateLabel)
        
        // Changes list
        val changesBox = VBox(10.0)
        changesBox.padding = Insets(10.0, 0.0, 0.0, 20.0)
        
        update.changes.forEach { change ->
            val changeLabel = Label("• $change")
            changeLabel.font = Font.font("Arial", FontWeight.NORMAL, 16.0)
            changeLabel.textFill = Color.web("#cccccc")
            changeLabel.isWrapText = true
            changeLabel.maxWidth = 850.0
            changesBox.children.add(changeLabel)
        }
        
        box.children.addAll(headerBox, changesBox)
        return box
    }
    
    private fun createButton(text: String, color: String): Button {
        val button = Button(text)
        button.prefWidth = 250.0
        button.prefHeight = 50.0
        button.font = Font.font("Arial", FontWeight.BOLD, 18.0)
        button.style = """
            -fx-background-color: $color;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-cursor: hand;
        """.trimIndent()
        
        button.setOnMouseEntered {
            button.opacity = 0.8
        }
        
        button.setOnMouseExited {
            button.opacity = 1.0
        }
        
        return button
    }
}
