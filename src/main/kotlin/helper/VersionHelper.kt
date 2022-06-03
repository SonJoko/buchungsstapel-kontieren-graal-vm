package helper

import java.util.*

object VersionHelper {
    val versionData: Version by lazy {
        val input = VersionHelper.javaClass.classLoader.getResourceAsStream("version.properties")
        val prop = Properties().also { it.load(input) };
        Version(
            prop["name"].toString(),
            prop["version"].toString(),
            prop["author"].toString(),
            prop["license"].toString()
        )
    }
}

data class Version(val name: String, val version: String, val author: String, val license: String)