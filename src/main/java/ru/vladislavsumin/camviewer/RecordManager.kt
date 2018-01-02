package ru.vladislavsumin.camviewer

import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecordManager(private val path: String) {
    //TODO реализовать блокировки
    //TODO сделать загрузку в отдельном потоке
    data class Record(
            val path: File,
            val camName: String,
            val timestamp: Calendar) {
        override fun toString(): String {
            return "$camName ${SimpleDateFormat("HH:mm:ss").format(timestamp.time)}"
        }
    }

    private val list: MutableMap<String, MutableList<Record>> = HashMap()

    init {
        updateRecords()
    }

    private fun updateRecords() {
        list.clear()
        val rootDir = File(path)
        val cams = rootDir.listFiles({ file -> file.isDirectory }) ?: return
        //TODO Добавить логирование + визуальную ошибку
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
    }

    fun getSortedList(year: Int, month: Int, day: Int, cams: Set<String> = list.keys): List<Record> {
        println("year $year, month $month")
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