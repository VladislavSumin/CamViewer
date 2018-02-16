package ru.vladislavsumin.camviewer

class Static {
    companion object {
        val recordManager = RecordManager(Config.getString("path"))
        val savedRecordManager = RecordManager(Config.getString("save_path"))
        val player = VLCJPlayer()
    }
}
