package ru.vladislavsumin.camviewer

class Static {
    companion object {
        val recordManager = RecordManager(Config.getString("path"))
        val player = VLCJPlayer()
    }
}
