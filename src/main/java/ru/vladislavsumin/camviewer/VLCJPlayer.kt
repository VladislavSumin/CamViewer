package ru.vladislavsumin.camviewer

import com.sun.jna.Memory
import javafx.application.Platform
import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.scene.image.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.stage.Screen
import org.slf4j.LoggerFactory
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent
import uk.co.caprica.vlcj.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.direct.BufferFormat
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat
import java.nio.ByteBuffer


class VLCJPlayer {
    private val log = LoggerFactory.getLogger(VLCJPlayer::class.java)
    private var imageView: ImageView? = null
    var mediaPlayerComponent: DirectMediaPlayerComponent
    private var writableImage: WritableImage? = null
    var playerHolder: Pane
        private set
    private var pixelFormat: WritablePixelFormat<ByteBuffer>
    private var videoSourceRatioProperty: FloatProperty

    init {
        log.debug("Start native discovery")
        NativeDiscovery().discover()
        log.debug("Finish native discovery")
        mediaPlayerComponent = CanvasPlayerComponent()
        playerHolder = Pane()
        //TODO Убрать playerHolder
        AnchorPane.setBottomAnchor(playerHolder, 0.0)
        AnchorPane.setLeftAnchor(playerHolder, 0.0)
        AnchorPane.setRightAnchor(playerHolder, 0.0)
        AnchorPane.setTopAnchor(playerHolder, 0.0)
        videoSourceRatioProperty = SimpleFloatProperty(0.4f)
        pixelFormat = PixelFormat.getByteBgraPreInstance()
        initializeImageView()
        mediaPlayerComponent.mediaPlayer.rate = 0.6f
        log.debug("Finish player load")
    }

    private fun initializeImageView() {
        val visualBounds = Screen.getPrimary().visualBounds
        writableImage = WritableImage(visualBounds.width.toInt(), visualBounds.height.toInt())

        imageView = ImageView(writableImage)
        playerHolder.children.add(imageView)

        playerHolder.widthProperty()
                .addListener { _, _, newValue -> fitImageViewSize(newValue.toFloat(), playerHolder.height.toFloat()) }

        playerHolder.heightProperty()
                .addListener { _, _, newValue -> fitImageViewSize(playerHolder.width.toFloat(), newValue.toFloat()) }

        videoSourceRatioProperty
                .addListener { _, _, _ -> fitImageViewSize(playerHolder.width.toFloat(), playerHolder.height.toFloat()) }
    }

    private fun fitImageViewSize(width: Float, height: Float) {
        Platform.runLater {
            val fitHeight = videoSourceRatioProperty.get() * width
            if (fitHeight > height) {
                imageView!!.fitHeight = height.toDouble()
                val fitWidth = (height / videoSourceRatioProperty.get()).toDouble()
                imageView!!.fitWidth = fitWidth
                imageView!!.x = (width - fitWidth) / 2
                imageView!!.y = 0.0
            } else {
                imageView!!.fitWidth = width.toDouble()
                imageView!!.fitHeight = fitHeight.toDouble()
                imageView!!.y = ((height - fitHeight) / 2).toDouble()
                imageView!!.x = 0.0
            }
        }
    }

    private inner class CanvasPlayerComponent : DirectMediaPlayerComponent(CanvasBufferFormatCallback()) {

        private val pw: PixelWriter by lazy(LazyThreadSafetyMode.NONE) { writableImage!!.pixelWriter!! }

        override fun display(mediaPlayer: DirectMediaPlayer?, nativeBuffers: Array<Memory>?, bufferFormat: BufferFormat?) {
            if (writableImage == null) return
            Platform.runLater {
                val nativeBuffer = mediaPlayer!!.lock()[0]
                try {
                    val byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size())
                    pw.setPixels(0, 0, bufferFormat!!.width, bufferFormat.height, pixelFormat, byteBuffer, bufferFormat.pitches[0])
                } finally {
                    mediaPlayer.unlock()
                }
            }
        }
    }

    private inner class CanvasBufferFormatCallback : BufferFormatCallback {
        override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
            val visualBounds = Screen.getPrimary().visualBounds
            Platform.runLater { videoSourceRatioProperty.set(sourceHeight.toFloat() / sourceWidth.toFloat()) }
            return RV32BufferFormat(visualBounds.width.toInt(), visualBounds.height.toInt())
        }
    }

    fun play(path: String) {
        mediaPlayerComponent.mediaPlayer.playMedia(path, ":demux=h264")
    }
}
