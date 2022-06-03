package data

import javafx.beans.property.SimpleStringProperty
import tornadofx.ItemViewModel
import tornadofx.getValue
import tornadofx.setValue

class Data(
    datum: String,
    beleg: String,
    soll: String,
    haben: String,
    buchungsText: String,
    betrag: String,
    umsatzSteuer: String
) {

    val datumProperty = SimpleStringProperty(this, "datum", datum)
    var datum by datumProperty

    val belegProperty = SimpleStringProperty(beleg)
    var beleg by belegProperty

    val sollProperty = SimpleStringProperty(soll)
    var soll by sollProperty

    val habenProperty = SimpleStringProperty(haben)
    var haben by habenProperty

    val buchungsTextProperty = SimpleStringProperty(buchungsText)
    var buchungsText by buchungsTextProperty

    val betragProperty = SimpleStringProperty(betrag)
    var betrag by betragProperty

    val umsatzSteuerProperty = SimpleStringProperty(umsatzSteuer)
    var umsatzSteuer by umsatzSteuerProperty

    fun sollForExport() = if (betrag.contains("-")) haben else soll
    fun habenForExport() = if (betrag.contains("-")) soll else haben
    fun betragForExport() = if (betrag.contains("-")) betrag.replace("-", "") else betrag

    object Validation {
        val HABEN_REGEX = "000[1-9]|00[1-9]\\d|0[1-9]\\d{2}|[1-9]\\d{3}".toRegex()
        val UST_REGEX = "\\d{2}\\.\\d{2}".toRegex()
    }

    fun toRecordString(): String {
        return datum.toString() + beleg.toString() + soll.toString() + haben.toString() + buchungsText.toString() + betrag.toString() + umsatzSteuer.toString()
    }
}

class DataModel() : ItemViewModel<Data>() {
    val datum = bind(Data::datumProperty)
    val beleg = bind(Data::belegProperty)
    val soll = bind(Data::sollProperty)
    val haben = bind(Data::habenProperty)
    val buchungsText = bind(Data::buchungsTextProperty)
    val betrag = bind(Data::betragProperty)
    val umsatzSteuer = bind(Data::umsatzSteuerProperty)
}