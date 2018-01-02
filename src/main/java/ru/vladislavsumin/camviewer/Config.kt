package ru.vladislavsumin.camviewer

import org.json.JSONObject
import java.io.*

class Config {
    companion object {
        private const val CONFIG_PATH = "config.json"

        private var config: JSONObject = JSONObject()

        init {
            try {
                val input = FileReader(CONFIG_PATH)
                config = JSONObject(input.readText())
                input.close()
            } catch (_: FileNotFoundException) {
                createDefaultConfig()
            }
        }

        private fun createDefaultConfig() {
            config = JSONObject()
            config.put("path", "path")

            val writer = FileWriter(CONFIG_PATH)
            writer.write(config.toString())
            writer.close()
        }

        fun getString(key: String) = config.getString(key)!!
    }
}