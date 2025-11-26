package com.melikshotroulette.melikshotroulette

import com.melikshotroulette.melikshotroulette.models.*
import com.melikshotroulette.melikshotroulette.utils.SoundManager
import com.melikshotroulette.melikshotroulette.views.CharacterSelectionView
import com.melikshotroulette.melikshotroulette.views.GameView
import com.melikshotroulette.melikshotroulette.views.MainMenuView
import javafx.animation.PauseTransition
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import javafx.stage.Stage
import javafx.util.Duration

class GameController(private val stage: Stage) {
    private var gameState: GameState? = null
    private var gameView: GameView? = null
    
    init {
        stage.title = "Melikshot Roulette 2D"
        stage.width = 1200.0
        stage.height = 700.0
        stage.isResizable = false
        stage.fullScreenExitHint = ""
        stage.isFullScreen = true
        
        // Start background music
        SoundManager.playBackgroundMusic()
        
        // Stop music when application closes
        stage.setOnCloseRequest {
            SoundManager.stopBackgroundMusic()
        }
    }
    
    fun showMainMenu() {
        val menuView = MainMenuView(
            onSinglePlayer = { showCharacterSelection(GameMode.SINGLE_PLAYER) },
            onTwoPlayer = { showCharacterSelection(GameMode.TWO_PLAYER) },
            onExit = { showExitScreen() }
        )
        stage.scene = menuView.scene
        stage.show()
    }
    
    private fun showCharacterSelection(mode: GameMode) {
        val charSelectionView = CharacterSelectionView(
            mode = mode,
            onCharactersSelected = { player1Char, player2Char ->
                startGame(mode, player1Char, player2Char)
            },
            onBack = { showMainMenu() }
        )
        stage.scene = charSelectionView.scene
        stage.isFullScreen = true
    }
    
    private fun showExitScreen() {
        try {
            val imageStream = javaClass.getResourceAsStream("/com/melikshotroulette/melikshotroulette/images/exit_bg.png")
            if (imageStream != null) {
                val image = Image(imageStream)
                val imageView = ImageView(image)
                imageView.isPreserveRatio = false
                imageView.fitWidthProperty().bind(stage.widthProperty())
                imageView.fitHeightProperty().bind(stage.heightProperty())
                
                // Create hidden exit button
                val exitButton = Button("X")
                exitButton.prefWidth = 30.0
                exitButton.prefHeight = 30.0
                exitButton.style = """
                    -fx-background-color: transparent;
                    -fx-text-fill: rgba(0, 255, 100, 0.1);
                    -fx-font-size: 16px;
                    -fx-font-weight: bold;
                    -fx-cursor: hand;
                    -fx-border-color: transparent;
                """.trimIndent()
                exitButton.setOnAction { stage.close() }
                
                // Make button more visible on hover
                exitButton.setOnMouseEntered {
                    exitButton.style = """
                        -fx-background-color: rgba(0, 255, 100, 0.05);
                        -fx-text-fill: rgba(0, 255, 100, 0.6);
                        -fx-font-size: 16px;
                        -fx-font-weight: bold;
                        -fx-cursor: hand;
                        -fx-border-color: rgba(0, 255, 100, 0.2);
                        -fx-border-radius: 5px;
                        -fx-background-radius: 5px;
                    """.trimIndent()
                }
                
                exitButton.setOnMouseExited {
                    exitButton.style = """
                        -fx-background-color: transparent;
                        -fx-text-fill: rgba(0, 255, 100, 0.1);
                        -fx-font-size: 16px;
                        -fx-font-weight: bold;
                        -fx-cursor: hand;
                        -fx-border-color: transparent;
                    """.trimIndent()
                }
                
                val root = StackPane(imageView, exitButton)
                root.alignment = Pos.CENTER
                StackPane.setAlignment(exitButton, Pos.BOTTOM_RIGHT)
                StackPane.setMargin(exitButton, Insets(0.0, 20.0, 20.0, 0.0))
                
                val exitScene = Scene(root, 1200.0, 700.0)
                stage.scene = exitScene
                stage.isFullScreen = true
                
                val pause = PauseTransition(Duration.seconds(60.0))
                pause.setOnFinished { stage.close() }
                pause.play()
            } else {
                stage.close()
            }
        } catch (e: Exception) {
            stage.close()
        }
    }
    
    private fun startGame(mode: GameMode, player1Character: String, player2Character: String) {
        gameState = GameState(mode, player1Character, player2Character)
        gameView = GameView(gameState!!, this)
        stage.scene = gameView!!.scene
        stage.isFullScreen = true
    }
    
    fun handleShoot(target: ShootTarget) {
        val state = gameState ?: return
        val view = gameView ?: return
        
        val result = state.shoot(target)
        view.showShootResult(result)
        
        val pause = PauseTransition(Duration.millis(2000.0))
        pause.setOnFinished {
            if (state.isGameOver()) {
                view.showGameOver()
            } else {
                state.nextTurn(result)
                view.updateUI()
            }
        }
        pause.play()
    }
    
    fun restartGame() {
        val mode = gameState?.mode ?: GameMode.TWO_PLAYER
        showCharacterSelection(mode)
    }
    
    fun backToMenu() {
        showMainMenu()
    }
}
