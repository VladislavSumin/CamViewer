package ru.vladislavsumin.camviewer

import org.json.JSONObject
import java.io.*

class Config {
    companion object {
        var config: JSONObject = JSONObject()
            private set

        init {
            try {
                val input = FileReader("config.json")
                config = JSONObject(input.readText())
                input.close()
            } catch (_: FileNotFoundException) {
                createDefault()
            }
        }

        private fun createDefault() {
            config = JSONObject()
            config.put("path", "path")
            println(config.toString())
            val writer = FileWriter("config.json")
            writer.write(config.toString())
            writer.close()
        }
    }
}