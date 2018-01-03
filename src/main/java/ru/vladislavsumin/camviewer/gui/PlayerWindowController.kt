package ru.vladislavsumin.camviewer.gui

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
    val mediaPlayerComponent = Static.player.mediaPlayerComponent!!
    @FXML private lateinit var player: Pane
    @FXML private lateinit var slider: Slider

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        player.children.add(Static.player.playerHolder)


        slider.isDisable = true
        slider.max = 1.0

        mediaPlayerComponent.mediaPlayer.addMediaPlayerEventListener(object : MediaPlayerEventAdapter() {
            override fun positionChanged(mediaPlayer: MediaPlayer, newPosition: Float) {
                slider.value = newPosition.toDouble()
            }
        })
    }

    fun pause() {
        mediaPlayerComponent.mediaPlayer.run {
            if (isPlaying) pause()
            else play()
        }
    }
}