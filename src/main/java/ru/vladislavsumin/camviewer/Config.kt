package ru.vladislavsumin.camviewer

import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.*

/**
 * @author Vladislav Sumin
 */
class Config {
    companion object {
        private const val CONFIG_PATH = "config.json"
        private val log = LoggerFactory.getLogger(Config::class.java)
        private var config: JSONObject = JSONObject()

        init {
            log.info("Try to load config from {}", CONFIG_PATH)
            try {
                val input = FileReader(CONFIG_PATH)
                config = JSONObject(input.readText())
                input.close()
                log.info("Config loaded")
            } catch (_: FileNotFoundException) {
                log.warn("Can not load config from {}. Creating default config", CONFIG_PATH)
                createDefaultConfig()
            }
        }

        private fun createDefaultConfig() {
            config = JSONObject()
            config.put("path", "path")

            val writer = FileWriter(CONFIG_PATH)
            writer.write(config.toString())
            writer.close()
            log.info("Default config created")
        }

        /**
         * @param key - config parameter
         * @return config string value
         */
        fun getString(key: String): String = config.getString(key)!!
    }
}