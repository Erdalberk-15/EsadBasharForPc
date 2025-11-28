package com.melikshotroulette.melikshotroulette.views

import com.melikshotroulette.melikshotroulette.GameController
import com.melikshotroulette.melikshotroulette.models.*
import com.melikshotroulette.melikshotroulette.utils.SoundManager
import javafx.animation.*
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.Duration

class FourPlayerGameView(private val gameState: GameState, private val controller: GameController) {
    val scene: Scene
    private val root: BorderPane
    private val playerBoxes = mutableListOf<VBox>()
    private val playerLifeLabels = mutableListOf<Label>()
    private val playerItemsGrids = mutableListOf<GridPane>()
    private val centerBox: VBox
    private val chamberInfoLabel: Label
    private val shotgunLabel: Label
    private val shootSelfButton: Button
    private val shootOpponentButton: Button
    private val resultLabel: Label
    private val currentPlayerLabel: Label
    
    init {
        root = BorderPane()
        root.style = "-fx-background-color: #1a1a1a;"
        
        // Create a main container with proper layering
        val mainContainer = StackPane()
        
        // Background layer - all player boxes
        val playersLayer = Pane()
        
        // Top: Player 1 (left) and Player 2 (right)
        val topBox = HBox()
        topBox.padding = javafx.geometry.Insets(10.0)
        topBox.prefWidth = 1200.0
        val player1Box = createPlayerBox(0, "#ff8800")
        val player2Box = createPlayerBox(1, "#00aaff")
        val topSpacer = Region()
        HBox.setHgrow(topSpacer, Priority.ALWAYS)
        topBox.children.addAll(player1Box, topSpacer, player2Box)
        topBox.layoutY = 0.0
        
        // Bottom: Player 3 (left) and Player 4 (right)
        val bottomBox = HBox()
        bottomBox.padding = javafx.geometry.Insets(10.0)
        bottomBox.prefWidth = 1200.0
        val player3Box = createPlayerBox(2, "#00ff00")
        val player4Box = createPlayerBox(3, "#ff00ff")
        val bottomSpacer = Region()
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS)
        bottomBox.children.addAll(player3Box, bottomSpacer, player4Box)
        bottomBox.layoutY = 450.0 // Position at bottom
        
        playersLayer.children.addAll(topBox, bottomBox)
        
        // Center - Gun and controls (moved up for 4-player)
        centerBox = VBox(30.0)
        centerBox.alignment = Pos.CENTER
        centerBox.padding = javafx.geometry.Insets(20.0)
        centerBox.isPickOnBounds = false // Allow clicks to pass through to players below
        centerBox.maxWidth = 800.0
        
        val title = Label("MELIKSHOT ROULETTE")
        title.font = Font.font("Arial", FontWeight.BOLD, 36.0)
        title.textFill = Color.web("#ff4444")
        
        chamberInfoLabel = Label()
        chamberInfoLabel.font = Font.font("Arial", FontWeight.BOLD, 18.0)
        chamberInfoLabel.textFill = Color.web("#ffaa00")
        
        shotgunLabel = Label("🔫")
        shotgunLabel.font = Font.font(80.0)
        shotgunLabel.style = "-fx-rotate: 0;"
        
        currentPlayerLabel = Label()
        currentPlayerLabel.font = Font.font("Arial", FontWeight.BOLD, 24.0)
        currentPlayerLabel.textFill = Color.web("#00ff00")
        
        resultLabel = Label("")
        resultLabel.font = Font.font("Arial", FontWeight.BOLD, 22.0)
        
        val buttonBox = HBox(20.0)
        buttonBox.alignment = Pos.CENTER
        
        shootSelfButton = createButton("Shoot Self", "#ff4444")
        shootSelfButton.prefWidth = 250.0
        shootSelfButton.prefHeight = 50.0
        shootSelfButton.setOnAction { controller.handleShoot(ShootTarget.SELF) }
        
        shootOpponentButton = createButton("Shoot Opponent", "#ff8800")
        shootOpponentButton.prefWidth = 250.0
        shootOpponentButton.prefHeight = 50.0
        shootOpponentButton.setOnAction { controller.showPlayerTargetSelection() }
        
        buttonBox.children.addAll(shootSelfButton, shootOpponentButton)
        
        val menuButton = createButton("Back to Menu", "#666666")
        menuButton.prefWidth = 250.0
        menuButton.prefHeight = 50.0
        menuButton.setOnAction { controller.backToMenu() }
        
        // Make labels and buttons mouse transparent where appropriate
        title.isMouseTransparent = true
        chamberInfoLabel.isMouseTransparent = true
        shotgunLabel.isMouseTransparent = true
        currentPlayerLabel.isMouseTransparent = true
        resultLabel.isMouseTransparent = true
        
        centerBox.children.addAll(title, chamberInfoLabel, shotgunLabel, currentPlayerLabel, resultLabel, buttonBox, menuButton)
        
        // Stack the layers
        mainContainer.children.addAll(playersLayer, centerBox)
        StackPane.setAlignment(centerBox, Pos.CENTER)
        
        root.center = mainContainer
        
        scene = Scene(root, 1200.0, 700.0)
        updateUI()
    }
    
    private fun createPlayerBox(playerIndex: Int, color: String): VBox {
        val box = VBox(8.0)
        box.alignment = Pos.TOP_CENTER
        box.padding = javafx.geometry.Insets(8.0)
        box.prefWidth = 220.0
        box.maxWidth = 220.0
        box.isPickOnBounds = false // Allow clicks to pass through to children
        
        val player = gameState.players[playerIndex]
        
        val title = Label(player.name)
        title.font = Font.font("Arial", FontWeight.BOLD, 14.0)
        title.textFill = Color.web(color)
        title.maxWidth = 200.0
        title.isWrapText = true
        title.isMouseTransparent = true
        
        val lifeLabel = Label()
        lifeLabel.font = Font.font("Arial", FontWeight.BOLD, 16.0)
        lifeLabel.textFill = Color.web("#ff4444")
        lifeLabel.style = "-fx-letter-spacing: 2px;"
        lifeLabel.isMouseTransparent = true
        playerLifeLabels.add(lifeLabel)
        
        val itemsGrid = createItemsGrid(playerIndex)
        playerItemsGrids.add(itemsGrid)
        
        // Character image (smaller for 4 players)
        val charImage = createCharacterImage(player.name, 80.0)
        charImage.isMouseTransparent = true
        
        val itemsLabel = Label("Items:")
        itemsLabel.font = Font.font("Arial", FontWeight.BOLD, 10.0)
        itemsLabel.textFill = Color.web("#cccccc")
        itemsLabel.isMouseTransparent = true
        
        box.children.addAll(title, lifeLabel, itemsLabel, itemsGrid, charImage)
        
        playerBoxes.add(box)
        return box
    }
    
    private fun createItemsGrid(playerIndex: Int): GridPane {
        val grid = GridPane()
        grid.hgap = 5.0
        grid.vgap = 5.0
        grid.alignment = Pos.CENTER
        grid.isPickOnBounds = false // Allow clicks to pass through empty areas
        
        for (i in 0..7) {
            val slot = createItemSlot(i, playerIndex)
            grid.add(slot, i % 4, i / 4)
        }
        
        return grid
    }
    
    private fun createItemSlot(index: Int, playerIndex: Int): VBox {
        val container = VBox(3.0)
        container.alignment = Pos.CENTER
        container.prefWidth = 35.0
        
        val slot = StackPane()
        slot.prefWidth = 35.0
        slot.prefHeight = 35.0
        
        val bg = Rectangle(35.0, 35.0)
        bg.fill = Color.web("#333333")
        bg.stroke = Color.web("#666666")
        bg.strokeWidth = 1.0
        bg.arcWidth = 5.0
        bg.arcHeight = 5.0
        
        val iconLabel = Label("")
        iconLabel.font = Font.font("Arial", FontWeight.BOLD, 10.0)
        iconLabel.textFill = Color.web("#ffffff")
        
        slot.children.addAll(bg, iconLabel)
        
        val descLabel = Label("")
        descLabel.font = Font.font("Arial", FontWeight.NORMAL, 8.0)
        descLabel.textFill = Color.web("#aaaaaa")
        descLabel.maxWidth = 35.0
        descLabel.isWrapText = true
        
        container.children.addAll(slot, descLabel)
        container.userData = Pair(index, playerIndex)
        
        // Add click handler to both slot and container for better clickability
        val clickHandler = {
            if (gameState.currentPlayerIndex == playerIndex) {
                val player = gameState.players[playerIndex]
                val item = player.items.getOrNull(index)
                if (item != null) {
                    if (player.useItem(index, gameState)) {
                        updateUI()
                    }
                }
            }
        }
        
        slot.setOnMouseClicked { clickHandler() }
        container.setOnMouseClicked { clickHandler() }
        
        // Make sure the container is mouse transparent when not current player
        container.isPickOnBounds = true
        
        return container
    }
    
    private fun createCharacterImage(characterName: String, size: Double): VBox {
        val container = VBox(5.0)
        container.alignment = Pos.CENTER
        
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
                    
                    val imageContainer = StackPane(imageView)
                    imageContainer.style = """
                        -fx-background-color: #2a2a2a;
                        -fx-border-color: #666666;
                        -fx-border-width: 1;
                        -fx-border-radius: 5;
                        -fx-background-radius: 5;
                        -fx-padding: 3;
                    """.trimIndent()
                    
                    container.children.add(imageContainer)
                }
            } catch (e: Exception) {
                // Image not found
            }
        }
        
        return container
    }
    
    fun updateUI() {
        // Update all players
        gameState.players.forEachIndexed { index, player ->
            playerLifeLabels[index].text = "🖤".repeat(player.lives) + "💔".repeat(player.maxLives - player.lives)
            updateItemsGrid(playerItemsGrids[index], player, index)
            
            // Highlight current player
            if (index == gameState.currentPlayerIndex) {
                playerBoxes[index].style = "-fx-background-color: rgba(255, 255, 0, 0.2); -fx-background-radius: 10;"
            } else {
                playerBoxes[index].style = ""
            }
        }
        
        // Update chamber info
        val loaded = gameState.getLoadedCount()
        val empty = gameState.getEmptyCount()
        val remaining = gameState.getRemainingChambers()
        chamberInfoLabel.text = "Chambers: $remaining left | Loaded: $loaded | Empty: $empty"
        
        // Update current player label
        currentPlayerLabel.text = "${gameState.getCurrentPlayer().name}'s Turn"
        
        resultLabel.text = ""
        shootSelfButton.isDisable = false
        shootOpponentButton.isDisable = false
    }
    
    private fun updateItemsGrid(grid: GridPane, player: Player, playerIndex: Int) {
        val isCurrentPlayer = playerIndex == gameState.currentPlayerIndex
        
        grid.children.forEach { node ->
            if (node is VBox) {
                val userData = node.userData as? Pair<*, *>
                val index = userData?.first as? Int ?: return@forEach
                val item = player.items.getOrNull(index)
                val slot = node.children[0] as StackPane
                val iconLabel = slot.children[1] as Label
                val descLabel = node.children[1] as Label
                
                if (item != null) {
                    iconLabel.text = getItemIcon(item.type)
                    descLabel.text = getItemShortDesc(item.type)
                    
                    if (isCurrentPlayer) {
                        (slot.children[0] as Rectangle).fill = Color.web("#555555")
                        slot.style = "-fx-cursor: hand;"
                        slot.opacity = 1.0
                    } else {
                        (slot.children[0] as Rectangle).fill = Color.web("#444444")
                        slot.style = "-fx-cursor: not-allowed;"
                        slot.opacity = 0.5
                    }
                } else {
                    iconLabel.text = ""
                    descLabel.text = ""
                    (slot.children[0] as Rectangle).fill = Color.web("#333333")
                    slot.style = ""
                    slot.opacity = 1.0
                }
            }
        }
    }
    
    private fun getItemIcon(type: ItemType): String {
        return when (type) {
            ItemType.BULLET_ORDER_SWITCHER -> "🔄"
            ItemType.DOUBLE_TRIGGER -> "⚡"
            ItemType.CIGARETTE -> "🚬"
            ItemType.DOUBLE_DAMAGE -> "💥"
        }
    }
    
    private fun getItemShortDesc(type: ItemType): String {
        return when (type) {
            ItemType.BULLET_ORDER_SWITCHER -> "Shuffle"
            ItemType.DOUBLE_TRIGGER -> "2x Shot"
            ItemType.CIGARETTE -> "+1 Life"
            ItemType.DOUBLE_DAMAGE -> "2x DMG"
        }
    }
    
    fun showShootResult(result: ShootResult) {
        shootSelfButton.isDisable = true
        shootOpponentButton.isDisable = true
        
        if (result == ShootResult.SELF_HIT || result == ShootResult.OPPONENT_HIT) {
            SoundManager.playGunshot()
        }
        
        when (result) {
            ShootResult.SELF_SAFE -> {
                resultLabel.text = "CLICK! Empty - Safe!"
                resultLabel.textFill = Color.web("#00ff00")
            }
            ShootResult.SELF_HIT -> {
                resultLabel.text = "BANG! Hit yourself!"
                resultLabel.textFill = Color.web("#ff0000")
            }
            ShootResult.OPPONENT_SAFE -> {
                resultLabel.text = "CLICK! Empty - Safe!"
                resultLabel.textFill = Color.web("#ffaa00")
            }
            ShootResult.OPPONENT_HIT -> {
                resultLabel.text = "BANG! Hit opponent!"
                resultLabel.textFill = Color.web("#ff8800")
            }
        }
    }
    
    fun showGameOver() {
        val winner = gameState.getWinner()
        val overlay = StackPane()
        overlay.style = "-fx-background-color: rgba(0, 0, 0, 0.9);"
        
        val gameOverBox = VBox(30.0)
        gameOverBox.alignment = Pos.CENTER
        
        val gameOverLabel = Label("GAME OVER")
        gameOverLabel.font = Font.font("Arial", FontWeight.BOLD, 50.0)
        gameOverLabel.textFill = Color.web("#ff4444")
        
        val winnerLabel = Label(if (winner != null) "${winner.name} WINS!" else "DRAW!")
        winnerLabel.font = Font.font("Arial", FontWeight.BOLD, 35.0)
        winnerLabel.textFill = Color.web("#ffaa00")
        
        val restartButton = createButton("Play Again", "#ff4444")
        restartButton.setOnAction { controller.restartGame() }
        
        val menuButton = createButton("Main Menu", "#666666")
        menuButton.setOnAction { controller.backToMenu() }
        
        gameOverBox.children.addAll(gameOverLabel, winnerLabel, restartButton, menuButton)
        overlay.children.add(gameOverBox)
        
        root.children.add(overlay)
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
        
        button.setOnMouseEntered { button.opacity = 0.8 }
        button.setOnMouseExited { button.opacity = 1.0 }
        
        return button
    }
}