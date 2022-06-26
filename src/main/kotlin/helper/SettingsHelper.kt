package helper

import mu.KotlinLogging
import java.io.File
import java.util.*

object SettingsHelper {
    private val logger = KotlinLogging.logger {}
    private const val settingsFile = "config/settings.properties"

    fun readSettings(): Settings {
        val file = File(settingsFile)

        return try {
            logger.info { "Loading application settings" }
            val prop = Properties().also { it.load(file.inputStream()) };
            Settings(
                prop["inputDefaultPath"].toString(),
                prop["exportDefaultPath"].toString()
            )

        } catch (e: Exception) {
            logger.info { "Loading application settings failed. Using default settings." }
            Settings(
                "", ""
            )
        }
    }

    fun writeSettings(settings: Settings) {

        logger.info { "Saving application settings $settings" }
        val currentSettings = readSettings()
        try {
            val file = File(settingsFile)
            val prop = Properties()
            prop["inputDefaultPath"] = if (settings.inputDefaultPath.isNotEmpty())
                settings.inputDefaultPath
            else
                currentSettings.inputDefaultPath
            prop["exportDefaultPath"] = if (settings.exportDefaultPath.isNotEmpty())
                settings.exportDefaultPath
            else
                currentSettings.exportDefaultPath
            prop.store(file.outputStream(), settingsFile)
        } catch (e: Exception) {
            logger.info { "Could not store application settings $settings: ${e.message}" }
        }
    }
}

data class Settings(val inputDefaultPath: String, val exportDefaultPath: String)

