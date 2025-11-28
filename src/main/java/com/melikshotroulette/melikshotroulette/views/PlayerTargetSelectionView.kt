package com.melikshotroulette.melikshotroulette.views

import com.melikshotroulette.melikshotroulette.models.GameState
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.FontWeight

class PlayerTargetSelectionView(
    private val gameState: GameState,
    private val onPlayerSelected: (Int) -> Unit,
    private val onCancel: () -> Unit
) {
    val scene: Scene
    
    init {
        val root = BorderPane()
        root.style = "-fx-background-color: #1a1a1a;"
        
        // Top - Title
        val topBox = VBox(10.0)
        topBox.alignment = Pos.CENTER
        topBox.padding = Insets(20.0)
        
        val title = Label("SELECT TARGET")
        title.font = Font.font("Arial", FontWeight.BOLD, 42.0)
        title.textFill = Color.web("#ff4444")
        
        val subtitle = Label("${gameState.getCurrentPlayer().name}, choose who to shoot:")
        subtitle.font = Font.font("Arial", FontWeight.NORMAL, 20.0)
        subtitle.textFill = Color.web("#cccccc")
        
        topBox.children.addAll(title, subtitle)
        root.top = topBox
        
        // Center - Player selection boxes
        val centerContainer = VBox(20.0)
        centerContainer.alignment = Pos.CENTER
        centerContainer.padding = Insets(20.0)
        
        val playersGrid = if (gameState.players.size == 4) {
            // 2x2 grid for 4 players
            createFourPlayerGrid()
        } else {
            // Single row for 2 players
            createTwoPlayerRow()
        }
        
        centerContainer.children.add(playersGrid)
        root.center = centerContainer
        
        // Bottom - Cancel button
        val bottomBox = HBox(20.0)
        bottomBox.alignment = Pos.CENTER
        bottomBox.padding = Insets(20.0)
        
        val cancelButton = createButton("Cancel", "#666666")
        cancelButton.setOnAction { onCancel() }
        
        bottomBox.children.add(cancelButton)
        root.bottom = bottomBox
        
        scene = Scene(root, 1200.0, 700.0)
    }
    
    private fun createFourPlayerGrid(): GridPane {
        val grid = GridPane()
        grid.hgap = 30.0
        grid.vgap = 30.0
        grid.alignment = Pos.CENTER
        
        val currentPlayerIndex = gameState.currentPlayerIndex
        var row = 0
        var col = 0
        
        gameState.players.forEachIndexed { index, player ->
            if (index != currentPlayerIndex && player.lives > 0) {
                val playerBox = createPlayerBox(index, player.name)
                grid.add(playerBox, col, row)
                
                col++
                if (col >= 2) {
                    col = 0
                    row++
                }
            }
        }
        
        return grid
    }
    
    private fun createTwoPlayerRow(): HBox {
        val hbox = HBox(30.0)
        hbox.alignment = Pos.CENTER
        
        val currentPlayerIndex = gameState.currentPlayerIndex
        
        gameState.players.forEachIndexed { index, player ->
            if (index != currentPlayerIndex && player.lives > 0) {
                val playerBox = createPlayerBox(index, player.name)
                hbox.children.add(playerBox)
            }
        }
        
        return hbox
    }
    
    private fun createPlayerBox(playerIndex: Int, playerName: String): VBox {
        val box = VBox(15.0)
        box.alignment = Pos.CENTER
        box.padding = Insets(30.0)
        box.style = """
            -fx-background-color: #2a2a2a;
            -fx-background-radius: 15;
            -fx-border-color: #ff4444;
            -fx-border-width: 3;
            -fx-border-radius: 15;
            -fx-cursor: hand;
        """.trimIndent()
        box.prefWidth = 300.0
        box.prefHeight = 350.0
        
        // Player name
        val nameLabel = Label(playerName)
        nameLabel.font = Font.font("Arial", FontWeight.BOLD, 28.0)
        nameLabel.textFill = Color.web("#ff8800")
        nameLabel.isWrapText = true
        nameLabel.maxWidth = 280.0
        
        // Player lives
        val player = gameState.players[playerIndex]
        val livesLabel = Label("🖤".repeat(player.lives) + "💔".repeat(player.maxLives - player.lives))
        livesLabel.font = Font.font("Arial", FontWeight.BOLD, 24.0)
        livesLabel.style = "-fx-letter-spacing: 3px;"
        
        // Character image
        val charImage = createCharacterImage(playerName, 150.0)
        
        // Shoot button
        val shootButton = createButton("SHOOT", "#ff0000")
        shootButton.prefWidth = 200.0
        shootButton.setOnAction { onPlayerSelected(playerIndex) }
        
        box.children.addAll(nameLabel, livesLabel, charImage, shootButton)
        
        // Hover effect
        box.setOnMouseEntered {
            box.style = """
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 15;
                -fx-border-color: #ff6666;
                -fx-border-width: 3;
                -fx-border-radius: 15;
                -fx-cursor: hand;
            """.trimIndent()
        }
        
        box.setOnMouseExited {
            box.style = """
                -fx-background-color: #2a2a2a;
                -fx-background-radius: 15;
                -fx-border-color: #ff4444;
                -fx-border-width: 3;
                -fx-border-radius: 15;
                -fx-cursor: hand;
            """.trimIndent()
        }
        
        box.setOnMouseClicked {
            onPlayerSelected(playerIndex)
        }
        
        return box
    }
    
    private fun createCharacterImage(characterName: String, size: Double): StackPane {
        val container = StackPane()
        container.prefWidth = size
        container.prefHeight = size
        
        val imagePath = when (characterName) {
            "Kedullah" -> "/com/melikshotroulette/melikshotroulette/images/cha_1.png"
            "FINAL BOSS" -> "/com/melikshotroulette/melikshotroulette/images/cha_2.png"
            "Nokia" -> "/com/melikshotroulette/melikshotroulette/images/cha_3.png"
            "Benji" -> "/com/melikshotroulette/melikshotroulette/images/cha_4.png"
            "Anne Terliği" -> "/com/melikshotroulette/melikshotroulette/images/cha_5.png"
            "Amongus" -> "/com/melikshotroulette/melikshotroulette/images/cha_6.png"
            else -> null
        }
        
        if (imagePath != null) {
            try {
                val imageStream = javaClass.getResourceAsStream(imagePath)
                if (imageStream != null) {
                    val image = Image(imageStream)
                    val imageView = ImageView(image)
                    imageView.fitWidth = size
                    imageView.fitHeight = size
                    imageView.isPreserveRatio = true
                    
                    container.style = """
                        -fx-background-color: #1a1a1a;
                        -fx-border-color: #666666;
                        -fx-border-width: 2;
                        -fx-border-radius: 10;
                        -fx-background-radius: 10;
                        -fx-padding: 5;
                    """.trimIndent()
                    
                    container.children.add(imageView)
                }
            } catch (e: Exception) {
                // Image not found, show placeholder
                val placeholder = Label("?")
                placeholder.font = Font.font("Arial", FontWeight.BOLD, 60.0)
                placeholder.textFill = Color.web("#666666")
                container.children.add(placeholder)
            }
        } else {
            val placeholder = Label("?")
            placeholder.font = Font.font("Arial", FontWeight.BOLD, 60.0)
            placeholder.textFill = Color.web("#666666")
            container.children.add(placeholder)
        }
        
        return container
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
