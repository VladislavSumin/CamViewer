package ru.vladislavsumin.camviewer.gui


import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.ListView
import javafx.scene.input.MouseEvent
import ru.vladislavsumin.camviewer.RecordManager
import ru.vladislavsumin.camviewer.Static
import java.net.URL
import java.time.LocalDate
import java.util.*


class FilesWindowController : Initializable {

    @FXML
    private lateinit var fileList: ListView<RecordManager.Record>
    @FXML
    private lateinit var date: DatePicker
    @FXML
    private lateinit var update: Button

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        date.value = LocalDate.now()
        updateFiles(date.value.year, date.value.monthValue - 1, date.value.dayOfMonth)
        date.valueProperty().addListener({ _, _, newValue ->
            updateFiles(newValue.year, newValue.monthValue - 1, newValue.dayOfMonth)
        })

        fileList.selectionModel.selectedItemProperty().addListener({ _, _, newValue ->
            if (newValue == null) return@addListener
            val path: String = newValue.path.absolutePath
            println(path)
            Static.player.play(path)
        })

        Static.recordManager.addOnDataChangeListener {
            Platform.runLater {
                updateFiles(date.value.year, date.value.monthValue - 1, date.value.dayOfMonth)
            }
        }
        update()
    }

    private fun updateFiles(year: Int, mont: Int, day: Int) {
        val items = FXCollections.observableArrayList<RecordManager.Record>()
        items.addAll(Static.recordManager.getSortedList(year, mont, day))
        fileList.items = items
    }

    fun prevDate() {
        date.value = date.value.minusDays(1)
    }

    fun nextDate() {
        date.value = date.value.plusDays(1)
    }

    fun update() {
        update.text = "Updating..."
        update.isDisable = true
        Static.recordManager.updateRecordsFromNewThread {
            Platform.runLater {
                update.text = "Update"
                update.isDisable = false
            }
        }
    }

    fun changeMode(mouseEvent: MouseEvent) {
        //TODO THIS
    }
}
