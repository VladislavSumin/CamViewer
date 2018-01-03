package ru.vladislavsumin.camviewer.gui

import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import ru.vladislavsumin.camviewer.Static
import java.net.URL
import java.util.*


class MainWindowController : Initializable {
    @FXML private lateinit var files: Pane
    @FXML private lateinit var player: Pane


    override fun initialize(location: URL?, resources: ResourceBundle?) {
        val resource = javaClass.classLoader.getResource("FilesWindow.fxml")
        val filesPanel: Parent = FXMLLoader.load(resource)

        val resource2 = javaClass.classLoader.getResource("PlayerWindow.fxml")
        val playerPanel: Parent = FXMLLoader.load(resource2)

        AnchorPane.setBottomAnchor(playerPanel, 0.0)
        AnchorPane.setLeftAnchor(playerPanel, 0.0)
        AnchorPane.setRightAnchor(playerPanel, 0.0)
        AnchorPane.setTopAnchor(playerPanel, 0.0)

        player.children.add(playerPanel)
        files.children.add(filesPanel)
    }
}