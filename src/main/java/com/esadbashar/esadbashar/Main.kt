package com.esadbashar.esadbashar

import javafx.animation.AnimationTimer
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import kotlin.random.Random
import kotlin.system.exitProcess

data class BouncingImage(
    val imageView: ImageView,
    var velocityX: Double,
    var velocityY: Double
)

class Main : Application() {
    
    private val bouncingImages = mutableListOf<BouncingImage>()
    private lateinit var pane: Pane
    private var windowWidth = 800.0
    private var windowHeight = 600.0
    private var lastDuplicationTime = 0L
    private val duplicationInterval = 15_000_000_000L // 15 seconds in nanoseconds
    private var baseImage: Image? = null
    private var mediaPlayer: MediaPlayer? = null
    
    override fun start(primaryStage: Stage) {
        // Check Java version before starting
        if (!JavaVersionChecker.checkJavaVersion()) {
            return
        }
        
        // Get screen dimensions
        val screenBounds = javafx.stage.Screen.getPrimary().bounds
        windowWidth = screenBounds.width
        windowHeight = screenBounds.height
        
        // Create transparent window
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        
        // Create root pane
        pane = Pane()
        pane.style = "-fx-background-color: transparent;"
        
        // Load base image (with fallback if not found)
        baseImage = try {
            val imageUrl = javaClass.getResource("/com/esadbashar/esadbashar/images/eb.png")
            if (imageUrl != null) {
                Image(imageUrl.toString())
            } else {
                null
            }
        } catch (e: Exception) {
            println("Image not found, using placeholder")
            null
        }
        
        // Create first bouncing image
        createNewBouncingImage()
        
        // Create close button
        val closeButton = Label("X")
        closeButton.font = Font.font(24.0)
        closeButton.textFill = Color.RED
        closeButton.style = "-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 5px 10px;"
        closeButton.layoutX = windowWidth - 50
        closeButton.layoutY = windowHeight - 50
        closeButton.setOnMouseClicked {
            mediaPlayer?.stop()
            Platform.exit()
            exitProcess(0)
        }
        pane.children.add(closeButton)
        
        // Create transparent scene
        val scene = Scene(pane, windowWidth, windowHeight)
        scene.fill = Color.TRANSPARENT
        
        primaryStage.title = "EsadBashar"
        primaryStage.scene = scene
        primaryStage.isFullScreen = true
        primaryStage.fullScreenExitHint = "" // Hide fullscreen exit hint
        
        // Make stage non-focusable so background apps can be interacted with
        primaryStage.show()
        primaryStage.isAlwaysOnTop = true // Keep on top always
        
        // Start background music
        startBackgroundMusic()
        
        // Start bouncing animation
        startBouncingAnimation()
    }
    
    private fun createNewBouncingImage() {
        val imageView = if (baseImage != null) {
            ImageView(baseImage)
        } else {
            createPlaceholderImage()
        }
        
        // Make image non-interactive (mouse events pass through)
        imageView.isPickOnBounds = false
        imageView.isMouseTransparent = true
        
        // Set random initial position
        val imgWidth = imageView.boundsInLocal.width.coerceAtLeast(1.0)
        val imgHeight = imageView.boundsInLocal.height.coerceAtLeast(1.0)
        val maxX = (windowWidth - imgWidth).coerceAtLeast(0.0)
        val maxY = (windowHeight - imgHeight).coerceAtLeast(0.0)
        imageView.x = if (maxX > 0) Random.nextDouble(0.0, maxX) else 0.0
        imageView.y = if (maxY > 0) Random.nextDouble(0.0, maxY) else 0.0
        
        // Random velocity
        val velocityX = Random.nextDouble(2.0, 4.0) * if (Random.nextBoolean()) 1 else -1
        val velocityY = Random.nextDouble(2.0, 4.0) * if (Random.nextBoolean()) 1 else -1
        
        // Random color
        changeImageColor(imageView)
        
        val bouncingImage = BouncingImage(imageView, velocityX, velocityY)
        bouncingImages.add(bouncingImage)
        pane.children.add(imageView)
        
        println("Created new bouncing image. Total: ${bouncingImages.size}")
    }
    
    private fun startBackgroundMusic() {
        try {
            val musicUrl = javaClass.getResource("/com/esadbashar/esadbashar/sounds/eb.mp3")
            if (musicUrl != null) {
                val media = Media(musicUrl.toString())
                mediaPlayer = MediaPlayer(media)
                mediaPlayer?.cycleCount = MediaPlayer.INDEFINITE
                mediaPlayer?.play()
                println("Background music started")
            } else {
                println("Music file eb.mp3 not found")
            }
        } catch (e: Exception) {
            println("Error loading music: ${e.message}")
        }
    }
    
    private fun createPlaceholderImage(): ImageView {
        // Create a simple colored rectangle as placeholder
        val canvas = javafx.scene.canvas.Canvas(100.0, 100.0)
        val gc = canvas.graphicsContext2D
        gc.fill = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        gc.fillRect(0.0, 0.0, 100.0, 100.0)
        
        val snapshot = canvas.snapshot(null, null)
        return ImageView(snapshot)
    }
    
    private fun startBouncingAnimation() {
        object : AnimationTimer() {
            override fun handle(now: Long) {
                // Check if it's time to duplicate
                if (lastDuplicationTime == 0L) {
                    lastDuplicationTime = now
                }
                
                if (now - lastDuplicationTime >= duplicationInterval) {
                    createNewBouncingImage()
                    lastDuplicationTime = now
                }
                
                // Update all bouncing images
                for (i in bouncingImages.indices) {
                    val bouncing = bouncingImages[i]
                    val img = bouncing.imageView
                    
                    // Update position
                    img.x += bouncing.velocityX
                    img.y += bouncing.velocityY
                    
                    // Check collision with edges and bounce
                    var bounced = false
                    
                    val imgWidth = img.boundsInLocal.width
                    val imgHeight = img.boundsInLocal.height
                    
                    // Right edge
                    if (img.x + imgWidth >= windowWidth) {
                        img.x = windowWidth - imgWidth
                        bouncing.velocityX = -bouncing.velocityX
                        bounced = true
                    }
                    
                    // Left edge
                    if (img.x <= 0) {
                        img.x = 0.0
                        bouncing.velocityX = -bouncing.velocityX
                        bounced = true
                    }
                    
                    // Bottom edge
                    if (img.y + imgHeight >= windowHeight) {
                        img.y = windowHeight - imgHeight
                        bouncing.velocityY = -bouncing.velocityY
                        bounced = true
                    }
                    
                    // Top edge
                    if (img.y <= 0) {
                        img.y = 0.0
                        bouncing.velocityY = -bouncing.velocityY
                        bounced = true
                    }
                    
                    // Change color on bounce
                    if (bounced) {
                        changeImageColor(img)
                    }
                    
                    // Check collision with other images
                    for (j in i + 1 until bouncingImages.size) {
                        val other = bouncingImages[j]
                        if (checkCollision(bouncing, other)) {
                            handleCollision(bouncing, other)
                        }
                    }
                }
            }
        }.start()
    }
    
    private fun checkCollision(img1: BouncingImage, img2: BouncingImage): Boolean {
        val bounds1 = img1.imageView.boundsInParent
        val bounds2 = img2.imageView.boundsInParent
        return bounds1.intersects(bounds2)
    }
    
    private fun handleCollision(img1: BouncingImage, img2: BouncingImage) {
        // Simple elastic collision - swap velocities
        val tempVelX = img1.velocityX
        val tempVelY = img1.velocityY
        
        img1.velocityX = img2.velocityX
        img1.velocityY = img2.velocityY
        img2.velocityX = tempVelX
        img2.velocityY = tempVelY
        
        // Change colors on collision
        changeImageColor(img1.imageView)
        changeImageColor(img2.imageView)
        
        // Separate images slightly to prevent sticking
        img1.imageView.x += img1.velocityX * 2
        img1.imageView.y += img1.velocityY * 2
        img2.imageView.x += img2.velocityX * 2
        img2.imageView.y += img2.velocityY * 2
    }
    
    private fun changeImageColor(imageView: ImageView) {
        // Apply color tint effect
        val colorAdjust = javafx.scene.effect.ColorAdjust()
        colorAdjust.hue = Random.nextDouble(-1.0, 1.0)
        imageView.effect = colorAdjust
    }
}

fun main(args: Array<String>) {
    Application.launch(Main::class.java, *args)
}
