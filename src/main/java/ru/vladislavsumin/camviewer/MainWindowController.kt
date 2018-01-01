package ru.vladislavsumin.camviewer

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import java.net.URL
import java.util.*


class MainWindowController : Initializable {
    @FXML private lateinit var files: Pane
    @FXML private lateinit var player: Pane


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val resource = javaClass.classLoader.getResource("FilesWindow.fxml")
        val filesPanel: Parent = FXMLLoader.load(resource)

        player.children.add(Static.player.playerHolder)
        files.children.add(filesPanel)
    }
}