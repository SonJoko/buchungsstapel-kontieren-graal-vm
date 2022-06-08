package data

import mu.KotlinLogging
import java.io.File

class DataParser {
    private val logger = KotlinLogging.logger {}
    fun convertFile2Data(filePath: String): List<Data> {
        val records = mutableListOf<Data>()
        val file = File(filePath)
        file.forEachLine(Charsets.ISO_8859_1) { line ->
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
    private fun readBeleg(line: String) = readFromIndex(line, 5, 10)
    private fun readSoll(line: String) = readFromIndex(line, 11, 15)
    private fun readHaben(line: String) = readFromIndex(line, 16, 20)
    private fun readBuchungsText(line: String) = readFromIndex(line, 21, 45)
    private fun readBetrag(line: String) = readFromIndex(line, 46, 56)
    private fun readUmsatzSteuer(line: String) = readFromIndex(line, 57, 61)
    private fun readFromIndex(value: String, start: Int, end: Int) = value.substring(IntRange(start, end)).trim()

    fun convertData2File(data: List<Data>, filePath: String) {
        val file = File(filePath)
        data.stream().map(this::buildRow2Write).reduce { line, nextLine -> line + nextLine }
            .ifPresent { file.writeText(it, Charsets.ISO_8859_1) }
    }

    private fun buildRow2Write(data: Data): String {
        return with(data) {
            String.format(
                "%5s%-6s%5s%5s%-25s%11s%5s%n",
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