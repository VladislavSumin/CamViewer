package ru.vladislavsumin.camviewer

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage

/**
 * Main enter point
 */
class MainWindow : Application() {
    override fun start(root: Stage) {
        val resource = javaClass.classLoader.getResource("MainWindow.fxml")
        val panel: Parent = FXMLLoader.load(resource)
        root.scene = Scene(panel)
        root.title = "Cam viewer"
        root.onCloseRequest = EventHandler {
            Platform.exit()
            System.exit(0)
        }
        root.isMaximized = true
        root.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(MainWindow::class.java)
        }
    }
}