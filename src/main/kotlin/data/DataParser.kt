package data

import mu.KotlinLogging
import org.apache.tika.parser.txt.CharsetDetector
import java.io.File
import java.nio.charset.Charset

class DataParser {
    private val logger = KotlinLogging.logger {}
    fun convertFile2Data(filePath: String): List<Data> {
        logger.info { "Starting import: $filePath" }

        val records = mutableListOf<Data>()
        val file = File(filePath)
        val encoding = detectFileEncoding(file)

        file.forEachLine(encoding) { line ->
            records.add(Data(
                datum = readDatum(line),
                beleg = readBeleg(line),
                soll = readSoll(line),
                haben = readHaben(line),
                buchungsText = readBuchungsText(line),
                betrag = readBetrag(line),
                umsatzSteuer = readUmsatzSteuer(line)
            ).also { /*data -> logger.info(data.toRecordString())*/ }
            )
        }

        logger.info { "Finished import: $filePath" }
        return records
    }

    private fun detectFileEncoding(file: File): Charset {
        val detector = CharsetDetector().setText(file.readBytes())
        return try {
            Charset.forName(detector.detect().name).also {
                logger.info { "Using input charset: $it" }
            }
        } catch (e: Exception) {
            Charsets.ISO_8859_1.also {
                logger.info { "Failed to detect inputCharset: ${e.message} using default: $it" }
            }
        }
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
        logger.info { "Starting export: $filePath in ANSI" }
        val file = File(filePath)
        data.stream().map(this::buildRow2Write).reduce { line, nextLine -> line + nextLine }
            .ifPresent { file.writeBytes(it.toByteArray(Charsets.ISO_8859_1)) }
        logger.info { "Finished export: $filePath in ANSI" }
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