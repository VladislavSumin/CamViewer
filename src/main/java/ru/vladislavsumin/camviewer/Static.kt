package ru.vladislavsumin.camviewer

class Static {
    companion object {
        val recordManager = RecordManager("/Volumes/Data 2TB/Mod/")
        val player = VLCJPlayer()
    }
}
