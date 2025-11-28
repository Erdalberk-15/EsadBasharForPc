package com.melikshotroulette.melikshotroulette

import com.melikshotroulette.melikshotroulette.models.*
import com.melikshotroulette.melikshotroulette.utils.SoundManager
import com.melikshotroulette.melikshotroulette.views.*
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
    private var fourPlayerGameView: FourPlayerGameView? = null
    private var previousScene: Scene? = null
    
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
            onFourPlayer = { showCharacterSelection(GameMode.FOUR_PLAYER) },
            onUpdateLogs = { showUpdateLogs() },
            onExit = { showExitScreen() }
        )
        stage.scene = menuView.scene
        stage.show()
    }
    
    private fun showUpdateLogs() {
        val updateLogsView = UpdateLogsView(
            onBack = { showMainMenu() }
        )
        stage.scene = updateLogsView.scene
        stage.isFullScreen = true
    }
    
    private fun showCharacterSelection(mode: GameMode) {
        val charSelectionView = CharacterSelectionView(
            mode = mode,
            onCharactersSelected = { characters ->
                when (characters.size) {
                    1 -> startGame(mode, characters[0], "AI")
                    2 -> startGame(mode, characters[0], characters[1])
                    4 -> startGame(mode, characters[0], characters[1], characters[2], characters[3])
                    else -> startGame(mode, characters[0], "AI")
                }
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
    
    private fun startGame(
        mode: GameMode, 
        player1Character: String, 
        player2Character: String,
        player3Character: String = "Player 3",
        player4Character: String = "Player 4"
    ) {
        gameState = GameState(mode, player1Character, player2Character, player3Character, player4Character)
        
        if (mode == GameMode.FOUR_PLAYER) {
            fourPlayerGameView = FourPlayerGameView(gameState!!, this)
            stage.scene = fourPlayerGameView!!.scene
        } else {
            gameView = GameView(gameState!!, this)
            stage.scene = gameView!!.scene
        }
        
        stage.isFullScreen = true
    }
    
    fun handleShoot(target: ShootTarget) {
        val state = gameState ?: return
        
        val result = state.shoot(target)
        
        // Show result in appropriate view
        if (state.mode == GameMode.FOUR_PLAYER) {
            fourPlayerGameView?.showShootResult(result)
        } else {
            gameView?.showShootResult(result)
        }
        
        val pause = PauseTransition(Duration.millis(2000.0))
        pause.setOnFinished {
            if (state.isGameOver()) {
                if (state.mode == GameMode.FOUR_PLAYER) {
                    fourPlayerGameView?.showGameOver()
                } else {
                    gameView?.showGameOver()
                }
            } else {
                state.nextTurn(result)
                if (state.mode == GameMode.FOUR_PLAYER) {
                    fourPlayerGameView?.updateUI()
                } else {
                    gameView?.updateUI()
                }
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
    
    fun showPlayerTargetSelection() {
        val state = gameState ?: return
        
        // Save current scene
        previousScene = stage.scene
        
        val targetSelectionView = PlayerTargetSelectionView(
            gameState = state,
            onPlayerSelected = { targetIndex ->
                // Set target and return to game
                state.targetPlayerIndex = targetIndex
                stage.scene = previousScene
                stage.isFullScreen = true
                handleShoot(ShootTarget.OPPONENT)
            },
            onCancel = {
                // Return to game without shooting
                stage.scene = previousScene
                stage.isFullScreen = true
            }
        )
        
        stage.scene = targetSelectionView.scene
        stage.isFullScreen = true
    }
}
