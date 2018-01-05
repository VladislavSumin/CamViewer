package ru.vladislavsumin.camviewer.gui

import javafx.beans.value.ChangeListener
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Slider
import javafx.scene.layout.Pane
import ru.vladislavsumin.camviewer.Static
import uk.co.caprica.vlcj.player.MediaPlayer
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter
import java.net.URL
import java.util.*

class PlayerWindowController : Initializable {
    private val mediaPlayer = Static.player.mediaPlayerComponent.mediaPlayer
    @FXML private lateinit var player: Pane
    @FXML private lateinit var slider: Slider

    private val sliderListener = ChangeListener<Number> { _, _, newValue ->
        mediaPlayer.position = newValue.toFloat()
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        //TODO Посмотреть причину долгой загрузки
        player.children.add(Static.player.playerHolder)

        slider.max = 1.0
        slider.valueProperty().addListener(sliderListener)

        mediaPlayer.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun positionChanged(mediaPlayer: MediaPlayer, newPosition: Float) {
                //TODO этот мегакостыль
                slider.valueProperty().removeListener(sliderListener)
                slider.value = newPosition.toDouble()
                slider.valueProperty().addListener(sliderListener)
            }
        })
    }

    fun pause() {
        mediaPlayer.run {
            if (isPlaying) pause()
            else play()
        }
    }
}