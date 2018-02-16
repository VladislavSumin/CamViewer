package ru.vladislavsumin.camviewer.gui


import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.ListView
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
    @FXML
    private lateinit var changeMode: Button

    private enum class Mode {
        Current {
            override val recordManager: RecordManager
                get() = Static.recordManager
            override val useCalendar: Boolean
                get() = true
        },
        Saved {
            override val recordManager: RecordManager
                get() = Static.savedRecordManager
            override val useCalendar: Boolean
                get() = false
        };

        abstract val recordManager: RecordManager;
        abstract val useCalendar: Boolean
    }

    private var currentMode: Mode = Mode.Current

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
        if (currentMode.useCalendar)
            items.addAll(currentMode.recordManager.getSortedList(year, mont, day))
        else
            items.addAll(currentMode.recordManager.getSortedList())
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
        //TODO добавить второй апдейт
        Static.recordManager.updateRecordsFromNewThread {
            Platform.runLater {
                update.text = "Update"
                update.isDisable = false
            }
        }
    }

    fun changeMode() {
        if (currentMode == Mode.Saved) {
            currentMode = Mode.Current
            changeMode.text = "Saved"
        } else {
            currentMode = Mode.Saved
            changeMode.text = "Current"
        }
        date.visibleProperty().value = currentMode.useCalendar
        updateFiles(date.value.year, date.value.monthValue - 1, date.value.dayOfMonth)
    }
}
