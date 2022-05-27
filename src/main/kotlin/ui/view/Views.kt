package ui.app.ui.view

import data.Data
import data.DataModel
import javafx.beans.value.ObservableBooleanValue
import javafx.scene.control.*
import javafx.scene.input.KeyEvent
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.FileChooser
import javafx.stage.StageStyle
import tornadofx.*
import ui.controller.ViewController
import ui.css.CSSTableCell

class MasterView : View("Buchungsstapel kontieren") {
    override val root = borderpane {
        top<MenuView>()
        center<ControlView>()
    }
}

class ControlView : View() {
    private val controller: ViewController by inject()

    var inputFile: TextField by singleAssign()
    var outputFile: TextField by singleAssign()
    lateinit var messageLabel: Label

    override val root = vbox {
        style {
            padding = box(10.px)
            fontSize = 11.pt
        }
        hbox(spacing = 10) {
            style {
                padding = box(2.px)
            }
            label("Eingabe") {
                setPrefSize(80.0, 20.0);
            }
            inputFile = textfield {
                setPrefSize(220.0, 20.0)
                isEditable = false
                style {
                    backgroundColor += Color.LIGHTGRAY
                }
            }
            button("...") {
                action {
                    controller.onButtonSelectImportFilePressed()
                }
            }
            button("Buchungsimport starten") {
                enableWhen {
                    controller.inputFileOK
                }
                action {
                    controller.onButtonImportPressed()
                }
                prefWidth = 200.0
            }


        }
        hbox(spacing = 10) {
            style {
                padding = box(2.px)
            }
            label("Ausgabe") {
                setPrefSize(80.0, 20.0);
            }
            outputFile = textfield() {
                setPrefSize(220.0, 20.0)
                isEditable = false
                style {
                    backgroundColor += Color.LIGHTGRAY
                }
            }
            button("...") {
                action {
                    controller.onButtonSelectExportFilePressed()
                }
            }
            button("Buchungsexport starten") {
                enableWhen {
                    controller.outputFileOK
                }
                action {
                    controller.onButtonExportPressed()
                }
                prefWidth = 200.0
            }
        }

        vbox(spacing = 0) {
            separator() {
                prefHeight = 10.0
            }
            messageLabel = label("Hier wären Fehlermeldungen, Infos, etc.") {
                useMaxWidth = true
                prefHeight = 30.0
            }.also {
                it.style {
                    backgroundColor += Color.SEASHELL
                    padding = box(10.px)
                    fontWeight = FontWeight.BOLD
                }
            }
            separator() {
                prefHeight = 10.0
            }
        }
        borderpane {
            center<TableView>()
        }.style {


        }
    }
}

class TableView : View() {

    companion object {
        val COLUMN_HABEN_ID = "COLUMN_HABEN"
        val COLUMN_UST_ID = "COLUMN_UST"
    }

    private val controller: ViewController by inject()
    private val model: DataModel by inject()

    override val root = tableview(controller.records) {
        setSortPolicy { _ -> false }

        style {
            fontSize = 14.pt
        }
        prefWidth = 1024.0
        column("Datum", Data::datum) {
            isReorderable = false
            fixedWidth(80)
            addClass(CSSTableCell.center)
        }
        column("Beleg", Data::beleg) {
            isReorderable = false
            fixedWidth(80)
            addClass(CSSTableCell.left)
        }
        column("Soll", Data::soll) {
            isReorderable = false
            fixedWidth(80)
            addClass(CSSTableCell.right)
        }
        column("Haben", Data::haben) {
            id = COLUMN_HABEN_ID
            isReorderable = false
            fixedWidth(80)
            makeEditable()
            cellDecorator { cellvalue ->
                styleWithCheck(cellvalue, Data.Validation.HABEN_REGEX)
            }
            addClass(CSSTableCell.right)
        }
        column("Buchungstext", Data::buchungsText) {
            isReorderable = false
            remainingWidth()
            addClass(CSSTableCell.left)
        }
        column("Betrag", Data::betrag) {
            isReorderable = false
            fixedWidth(120)
            addClass(CSSTableCell.right)
        }
        column("Ust", Data::umsatzSteuer) {
            id = COLUMN_UST_ID
            isReorderable = false
            fixedWidth(100)
            makeEditable()
            cellDecorator { cellValue ->
                styleWithCheck(cellValue, Data.Validation.UST_REGEX)
            }
            addClass(CSSTableCell.right)
        }

        bindSelected(model)
        smartResize()
        enableCellEditing()
        regainFocusAfterEdit()
        onEditCommit { data ->
            save(this, data)
        }
        addEventHandler(KeyEvent.KEY_PRESSED) { keyEvent ->

            controller.onKeyPressedInTable(keyEvent, selectedCell, this)
        }
    }.also {
        it.setOnKeyPressed { keyevent ->
            log.info(keyevent.toString())
        }
    }

    private fun TableCell<Data, String>.styleWithCheck(value: String, regex: Regex) =
        style {
            fontWeight = FontWeight.BOLD
            textFill = if (!regex.matches(value)) {
                Color.RED
            } else {
                Color.DIMGRAY
            }
        }

    private fun save(data: TableColumn.CellEditEvent<Data, Any>, data1: Data) {
        if (model.isDirty) {
            log.info("dirty")
        }
        model.commit()
        controller.validate(data, data1)
        println("Saving ${model.item.umsatzSteuer} / ${model.item.haben}")
    }

}

class MenuView : View() {
    private val controller: ViewController by inject()
    override val root = menubar {
        style {
            fontSize = 12.pt
        }
        menu("Datei") {
            item("Einstellungen").action {
                controller.closeApp()
            }
            separator()
            item("Beenden").action {
                controller.closeApp()
            }
        }
        menu("Hilfe") {
            item("Version").action {
                find<VersionView>().openModal(stageStyle = StageStyle.UTILITY)
            }
            item("Anleitung").action {
                controller.closeApp()
            }
        }
    }
}


// Utils

fun selectFile(description: String): String {
    val files = chooseFile(
        mode = FileChooserMode.Single,
        filters = arrayOf(
            FileChooser.ExtensionFilter(description, "*.txt")
        )
    )
    return files[0].absolutePath;
}