package ru.vladislavsumin.camviewer

import com.sun.jna.Memory
import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.FloatProperty
import javafx.beans.property.SimpleFloatProperty
import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.Scene
import javafx.scene.image.*
import javafx.scene.layout.*
import javafx.scene.paint.Paint
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.WindowEvent
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent
import uk.co.caprica.vlcj.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.direct.BufferFormat
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat

import java.nio.ByteBuffer


class VLCJPlayer {
    //    private static final String PATH_TO_VIDEO = "/Users/vladislavsumin/Desktop/кинцо/Misfits.S03E06.720p.WEB-DL.Rus.Eng.HDCLUB.mkv";
    private val PATH_TO_VIDEO = "/Users/vladislavsumin/doc/20170731214224.h264"

    private var imageView: ImageView? = null

    private var mediaPlayerComponent: DirectMediaPlayerComponent? = null

    private var writableImage: WritableImage? = null

    var playerHolder: Pane? = null
        private set

    private var pixelFormat: WritablePixelFormat<ByteBuffer>? = null

    private var videoSourceRatioProperty: FloatProperty? = null


    init {
        NativeDiscovery().discover()
        mediaPlayerComponent = CanvasPlayerComponent()
        playerHolder = Pane()
        playerHolder!!.setStyle("-fx-background-color: #FFFFFF;");
        AnchorPane.setBottomAnchor(playerHolder, 0.0)
        AnchorPane.setLeftAnchor(playerHolder, 0.0)
        AnchorPane.setRightAnchor(playerHolder, 0.0)
        AnchorPane.setTopAnchor(playerHolder, 0.0)
        videoSourceRatioProperty = SimpleFloatProperty(0.4f)
        pixelFormat = PixelFormat.getByteBgraPreInstance()
        initializeImageView()
        //        mediaPlayerComponent.getMediaPlayer().addMediaOptions("demux=h264");
        //        mediaPlayerComponent.getMediaPlayer().prepareMedia(PATH_TO_VIDEO);
        //        mediaPlayerComponent.getMediaPlayer().start();
        //mediaPlayerComponent!!.mediaPlayer.playMedia(PATH_TO_VIDEO, *arrayOf(":demux=h264"))

        mediaPlayerComponent!!.getMediaPlayer().rate = 0.6f
    }

    private fun initializeImageView() {
        val visualBounds = Screen.getPrimary().visualBounds
        writableImage = WritableImage(visualBounds.width.toInt(), visualBounds.height.toInt())

        imageView = ImageView(writableImage)
        playerHolder!!.children.add(imageView)

        playerHolder!!.widthProperty().addListener { observable, oldValue, newValue -> fitImageViewSize(newValue.toFloat(), playerHolder!!.height.toFloat()) }

        playerHolder!!.heightProperty().addListener { observable, oldValue, newValue -> fitImageViewSize(playerHolder!!.width.toFloat(), newValue.toFloat()) }

        videoSourceRatioProperty!!.addListener { observable, oldValue, newValue -> fitImageViewSize(playerHolder!!.width.toFloat(), playerHolder!!.height.toFloat()) }
    }

    private fun fitImageViewSize(width: Float, height: Float) {
        Platform.runLater {
            val fitHeight = videoSourceRatioProperty!!.get() * width
            if (fitHeight > height) {
                imageView!!.fitHeight = height.toDouble()
                val fitWidth = (height / videoSourceRatioProperty!!.get()).toDouble()
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

        internal var pixelWriter: PixelWriter? = null

        private val pw: PixelWriter?
            get() {
                if (pixelWriter == null) {
                    pixelWriter = writableImage!!.pixelWriter
                }
                return pixelWriter
            }

        override fun display(mediaPlayer: DirectMediaPlayer?, nativeBuffers: Array<Memory>?, bufferFormat: BufferFormat?) {
            if (writableImage == null) {
                return
            }
            Platform.runLater {
                val nativeBuffer = mediaPlayer!!.lock()[0]
                try {
                    val byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size())
                    pw!!.setPixels(0, 0, bufferFormat!!.width, bufferFormat.height, pixelFormat, byteBuffer, bufferFormat.pitches[0])
                } finally {
                    mediaPlayer.unlock()
                }
            }
        }
    }

    private inner class CanvasBufferFormatCallback : BufferFormatCallback {
        override fun getBufferFormat(sourceWidth: Int, sourceHeight: Int): BufferFormat {
            val visualBounds = Screen.getPrimary().visualBounds
            Platform.runLater { videoSourceRatioProperty!!.set(sourceHeight.toFloat() / sourceWidth.toFloat()) }
            return RV32BufferFormat(visualBounds.width.toInt(), visualBounds.height.toInt())
        }
    }

    fun play(path: String) {
        mediaPlayerComponent!!.mediaPlayer.playMedia(path, *arrayOf(":demux=h264"))
    }
}
