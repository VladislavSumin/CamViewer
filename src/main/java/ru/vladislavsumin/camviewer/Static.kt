package ru.vladislavsumin.camviewer

class Static {
    companion object {
        val recordManager = RecordManager(Config.config.getString("path"))
        val player = VLCJPlayer()
    }
}
