package com.melikshotroulette.melikshotroulette.views

import com.melikshotroulette.melikshotroulette.models.GameMode
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

class CharacterSelectionView(
    private val mode: GameMode,
    private val onCharactersSelected: (List<String>) -> Unit,
    private val onBack: () -> Unit
) {
    val scene: Scene
    private val selectedCharacters = mutableListOf<String>()
    private val characters = listOf("Kedullah", "FINAL BOSS", "Nokia", "Benji", "Anne Terliği", "Amongus")
    private val characterImages = listOf("cha_1.png", "cha_2.png", "cha_3.png", "cha_4.png", "cha_5.png", "cha_6.png")
    
    private var currentPlayer = 1
    private val maxPlayers = when (mode) {
        GameMode.SINGLE_PLAYER -> 1
        GameMode.TWO_PLAYER -> 2
        GameMode.FOUR_PLAYER -> 4
    }
    private val titleLabel: Label
    private val characterBoxes = mutableListOf<VBox>()
    
    init {
        val root = BorderPane()
        root.style = "-fx-background-color: #1a1a1a;"
        
        // Top - Title
        val topBox = VBox(10.0)
        topBox.alignment = Pos.CENTER
        topBox.padding = Insets(20.0)
        
        titleLabel = Label("PLAYER 1 - SELECT YOUR CHARACTER")
        titleLabel.font = Font.font("Arial", FontWeight.BOLD, 36.0)
        titleLabel.textFill = Color.web("#ff8800")
        
        topBox.children.add(titleLabel)
        root.top = topBox
        
        // Center - Character selection (2 rows of 3)
        val centerBox = VBox(15.0)
        centerBox.alignment = Pos.CENTER
        centerBox.padding = Insets(10.0)
        
        // First row (3 characters)
        val row1 = HBox(20.0)
        row1.alignment = Pos.CENTER
        for (i in 0..2) {
            val charBox = createCharacterBox(characters[i], characterImages[i], i)
            characterBoxes.add(charBox)
            row1.children.add(charBox)
        }
        
        // Second row (3 characters)
        val row2 = HBox(20.0)
        row2.alignment = Pos.CENTER
        for (i in 3..5) {
            val charBox = createCharacterBox(characters[i], characterImages[i], i)
            characterBoxes.add(charBox)
            row2.children.add(charBox)
        }
        
        centerBox.children.addAll(row1, row2)
        root.center = centerBox
        
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
    
    private fun createCharacterBox(name: String, imagePath: String, index: Int): VBox {
        val box = VBox(10.0)
        box.alignment = Pos.CENTER
        box.padding = Insets(10.0)
        box.prefWidth = 180.0
        box.style = """
            -fx-background-color: #2a2a2a;
            -fx-background-radius: 10;
            -fx-border-color: #444444;
            -fx-border-width: 2;
            -fx-border-radius: 10;
        """.trimIndent()
        
        // Character image
        val imageView = try {
            val imageStream = javaClass.getResourceAsStream("/com/melikshotroulette/melikshotroulette/images/$imagePath")
            if (imageStream != null) {
                val image = Image(imageStream)
                ImageView(image).apply {
                    fitWidth = 140.0
                    fitHeight = 140.0
                    isPreserveRatio = true
                }
            } else {
                // Placeholder if image not found
                ImageView().apply {
                    fitWidth = 140.0
                    fitHeight = 140.0
                }
            }
        } catch (e: Exception) {
            ImageView().apply {
                fitWidth = 140.0
                fitHeight = 140.0
            }
        }
        
        // Character name
        val nameLabel = Label(name)
        nameLabel.font = Font.font("Arial", FontWeight.BOLD, 16.0)
        nameLabel.textFill = Color.web("#ffffff")
        nameLabel.style = "-fx-text-alignment: center;"
        nameLabel.maxWidth = 160.0
        nameLabel.isWrapText = true
        
        // Select button
        val selectButton = createButton("SELECT", "#ff4444")
        selectButton.prefWidth = 150.0
        selectButton.prefHeight = 35.0
        selectButton.setOnAction {
            selectCharacter(name, index)
        }
        
        box.children.addAll(imageView, nameLabel, selectButton)
        
        // Hover effect
        box.setOnMouseEntered {
            box.style = """
                -fx-background-color: #3a3a3a;
                -fx-background-radius: 15;
                -fx-border-color: #ff4444;
                -fx-border-width: 3;
                -fx-border-radius: 15;
            """.trimIndent()
        }
        
        box.setOnMouseExited {
            val borderColor = if (selectedCharacters.contains(name)) {
                getPlayerColor(selectedCharacters.indexOf(name))
            } else {
                "#444444"
            }
            box.style = """
                -fx-background-color: #2a2a2a;
                -fx-background-radius: 15;
                -fx-border-color: $borderColor;
                -fx-border-width: 2;
                -fx-border-radius: 15;
            """.trimIndent()
        }
        
        return box
    }
    
    private fun getPlayerColor(playerIndex: Int): String {
        return when (playerIndex) {
            0 -> "#ff8800" // Player 1 - Orange
            1 -> "#00aaff" // Player 2 - Blue
            2 -> "#00ff00" // Player 3 - Green
            3 -> "#ff00ff" // Player 4 - Magenta
            else -> "#444444"
        }
    }
    
    private fun getPlayerName(playerIndex: Int): String {
        return "PLAYER ${playerIndex + 1}"
    }
    
    private fun selectCharacter(character: String, index: Int) {
        // Don't allow selecting the same character twice
        if (selectedCharacters.contains(character)) {
            return
        }
        
        selectedCharacters.add(character)
        
        // Highlight selected character
        updateCharacterBoxBorder(index, getPlayerColor(selectedCharacters.size - 1))
        
        if (selectedCharacters.size < maxPlayers) {
            // Move to next player selection
            currentPlayer++
            titleLabel.text = "${getPlayerName(currentPlayer - 1)} - SELECT YOUR CHARACTER"
            titleLabel.textFill = Color.web(getPlayerColor(currentPlayer - 1))
        } else {
            // All players selected, start game
            onCharactersSelected(selectedCharacters.toList())
        }
    }
    
    private fun updateCharacterBoxBorder(index: Int, color: String) {
        val box = characterBoxes[index]
        box.style = """
            -fx-background-color: #2a2a2a;
            -fx-background-radius: 15;
            -fx-border-color: $color;
            -fx-border-width: 3;
            -fx-border-radius: 15;
        """.trimIndent()
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
