package ru.vladislavsumin.camviewer

import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * Просмотр и каталогизирование записей с камер
 */
class RecordManager(private val path: String) {
    /**
     * Содержит информацию о видеозаписи
     */
    @Suppress("MemberVisibilityCanBePrivate")
    data class Record(
            val path: File,
            val camName: String, // имя камеры
            val timestamp: Calendar) { // время, получается из названия файла

        //Используется для вывода названия записи
        override fun toString(): String {
            return "$camName ${SimpleDateFormat("HH:mm:ss").format(timestamp.time)}"
        }
    }

    interface OnDataUpdateListener {
        fun onDataUpdate()
    }

    private val log = LoggerFactory.getLogger(RecordManager::class.java)
    private val list: MutableMap<String, MutableList<Record>> = HashMap()
    private val listeners: MutableSet<OnDataUpdateListener> = HashSet()
    @Volatile
    private var update = false

    /**
     * Обновляет список записей из отдельного потока
     * @param afterDataUpdate - вызывается после обновления списка
     */
    fun updateRecordsFromNewThread(afterDataUpdate: (() -> Unit)? = null) {
        Thread({
            updateRecords()
            afterDataUpdate?.invoke()
        }, "RecordManager Update").start()
    }

    private fun updateRecords() {
        synchronized(list) {
            if (update) {
                log.debug("Files already updating")
                return
            }
            update = true
        }
        log.debug("Start update files")
        list.clear()
        val rootDir = File(path)
        val cams = rootDir.listFiles({ file -> file.isDirectory })
        if (cams == null) {
            log.warn("Files directory {} no exist", path)
            synchronized(list) { update = false }
            return
        }
        for (cam in cams) {
            val listFiles = File("${cam.absolutePath}/1/").listFiles()
            if (listFiles.isEmpty()) continue
            val listRecord = ArrayList<Record>(listFiles.size)
            for (i in listFiles.indices) {
                val time = listFiles[i].name
                val timestamp = GregorianCalendar()
                timestamp.set(
                        time.substring(0, 4).toInt(),
                        time.substring(4, 6).toInt() - 1,
                        time.substring(6, 8).toInt(),
                        time.substring(8, 10).toInt(),
                        time.substring(10, 12).toInt(),
                        time.substring(12, 14).toInt())
                listRecord.add(Record(listFiles[i], cam.name, timestamp))
            }
            list[cam.name] = listRecord
        }
        synchronized(list) { update = false }
        log.debug("Files updated")
        synchronized(listeners) {
            log.debug("Invoke onDataUpdate from {} listeners", listeners.size)
            listeners.forEach({ it.onDataUpdate() })
        }
    }

    fun getSortedList(year: Int, month: Int, day: Int, cams: Set<String> = list.keys): List<Record> {
        synchronized(list) {
            if (update) {
                log.debug("Invoke getSortedList on files update")
                return emptyList()
            }
            val data: MutableList<Record> = LinkedList()
            for (key in cams) {
                list[key]!!.stream().filter({
                    it.timestamp.get(Calendar.YEAR) == year && it.timestamp.get(Calendar.MONTH) == month &&
                            it.timestamp.get(Calendar.DAY_OF_MONTH) == day
                }).forEach({ data.add(it) })
            }
            data.sortBy { record -> record.timestamp }
            data.reverse()
            return data
        }
    }

    fun getSortedList(cams: Set<String> = list.keys): List<Record> {
        //TODO опитмизировать
        synchronized(list) {
            if (update) {
                log.debug("Invoke getSortedList on files update")
                return emptyList()
            }
            val data: MutableList<Record> = LinkedList()
            for (key in cams) {
                list[key]!!.forEach({ data.add(it) })
            }
            data.sortBy { record -> record.timestamp }
            data.reverse()
            return data
        }
    }

    fun addOnDataChangeListener(listener: () -> Unit) {
        addOnDataChangeListener(object : OnDataUpdateListener {
            override fun onDataUpdate() {
                listener.invoke()
            }
        })
    }

    fun addOnDataChangeListener(listener: OnDataUpdateListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    fun removeOnDataChangeListener(listener: OnDataUpdateListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }
}