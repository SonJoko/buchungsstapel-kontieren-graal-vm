package data

import mu.KotlinLogging
import java.io.File

class DataParser {
    private val logger = KotlinLogging.logger {}
    fun convertFile2Data(filePath: String): List<Data> {
        val records = mutableListOf<Data>()
        val file = File(filePath)
        file.forEachLine { line ->
            records.add(Data(
                datum = readDatum(line),
                beleg = readBeleg(line),
                soll = readSoll(line),
                haben = readHaben(line),
                buchungsText = readBuchungsText(line),
                betrag = readBetrag(line),
                umsatzSteuer = readUmsatzSteuer(line)
            ).also { data -> logger.info(data.toRecordString()) }
            )
        }
        return records
    }

    private fun readDatum(line: String) = readFromIndex(line, 0, 4)
    private fun readBeleg(line: String) = readFromIndex(line, 5, 8)
    private fun readSoll(line: String) = readFromIndex(line, 9, 13)
    private fun readHaben(line: String) = readFromIndex(line, 14, 18)
    private fun readBuchungsText(line: String) = readFromIndex(line, 19, 43)
    private fun readBetrag(line: String) = readFromIndex(line, 44, 54)
    private fun readUmsatzSteuer(line: String) = readFromIndex(line, 55, 59)
    private fun readFromIndex(value: String, start: Int, end: Int) = value.substring(IntRange(start, end)).trim()

    fun convertData2File(data: List<Data>, filePath: String) {
        val file = File(filePath)
        data.stream().map(this::buildRow2Write).reduce { line, nextLine -> line + nextLine }
            .ifPresent { file.writeText(it) }
    }

    private fun buildRow2Write(data: Data): String {
        return with(data) {
            String.format(
                "%5s%-4s%5s%5s%-25s%11s%5s%n",
                datum,
                beleg,
                sollForExport(),
                habenForExport(),
                buchungsText,
                betragForExport(),
                umsatzSteuer
            )
        }
    }
}