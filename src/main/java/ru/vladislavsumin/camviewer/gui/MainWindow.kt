package ru.vladislavsumin.camviewer.gui

import javafx.application.Application
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import org.slf4j.LoggerFactory

/**
 * Main enter point
 */
class MainWindow : Application() {
    override fun start(root: Stage) {
        Thread.currentThread().name = "GUI"

        val resource = javaClass.classLoader.getResource("MainWindow.fxml")
        val panel: Parent = FXMLLoader.load(resource)
        root.scene = Scene(panel)

        root.title = "Cam viewer"
        root.onCloseRequest = EventHandler {
            log.info("onCloseRequest received")
            Platform.exit()
            System.exit(0)
        }
        root.isMaximized = true

        root.show()
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainWindow::class.java)

        @JvmStatic
        fun main(args: Array<String>) {
            log.info("==== MAIN ENTER POINT ====")

            //TODO подумать над этим
            Thread.setDefaultUncaughtExceptionHandler({ t, e ->
                log.error("GLOBAL FALL on thread ${t.name}", e)
                Platform.exit()
                System.exit(0)
            })

            launch(MainWindow::class.java)
        }
    }
}