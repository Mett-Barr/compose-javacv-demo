package com.test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.StackPane
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bytedeco.javacv.FFmpegFrameGrabber
import org.bytedeco.javacv.Frame
import org.bytedeco.javacv.JavaFXFrameConverter
import org.bytedeco.javacv.VideoInputFrameGrabber

@Composable
fun JavaFXWebcamTest() {
    SwingPanel(
        factory = {
            JFXPanel().apply {
                Platform.runLater {
                    val imageView = ImageView().apply {
                        isPreserveRatio = true  // 保持影像比例
                    }

                    val root = StackPane(imageView).apply {
                        alignment = Pos.CENTER  // 確保影像在中央顯示
                    }

                    val scene = Scene(root, 1920.0, 1080.0) // 設定場景大小，根據需求調整
                    this.scene = scene

                    // 在場景設置後綁定寬度和高度
                    scene.widthProperty().addListener { _, _, newValue ->
                        imageView.fitWidth = newValue.toDouble()
                    }
                    scene.heightProperty().addListener { _, _, newValue ->
                        imageView.fitHeight = newValue.toDouble()
                    }

                    CoroutineScope(Dispatchers.Default).launch {
                        getCameraFrameJavaFX() { image ->
                            Platform.runLater {
                                imageView.image = image
                            }
                        }
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

fun getFrameJavaFX(url: String, newFrame: (Image) -> Unit) {
    val grabber = FFmpegFrameGrabber(url)
    grabber.format = "rtsp"  // 设置输入格式为 rtsp
    val converter = JavaFXFrameConverter()

    try {
        grabber.start()
        var frame: Frame?

        while (true) {
            frame = grabber.grab()
            if (frame == null) break
            val image = converter.convert(frame)
            newFrame(image)
        }
        grabber.stop()
        grabber.release()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun getCameraFrameJavaFX(newFrame: (Image) -> Unit) {
    val grabber =
        VideoInputFrameGrabber.createDefault(0).apply { start() }
//        VideoInputFrameGrabber.createDefault(VideoInputFrameGrabber.getDeviceDescriptions().first()).apply { start() }
    val converter = JavaFXFrameConverter()

    try {
        grabber.start()
        var frame: Frame?

        while (true) {
            frame = grabber.grab()
            if (frame == null) break
            val image = converter.convert(frame)
            newFrame(image)
        }
        grabber.stop()
        grabber.release()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}