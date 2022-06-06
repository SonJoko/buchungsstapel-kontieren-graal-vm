package ui.controller

import data.Data
import data.DataModel
import data.DataParser
import javafx.beans.property.SimpleBooleanProperty
import javafx.event.Event
import javafx.scene.control.TableColumn
import javafx.scene.control.TablePosition
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.control.skin.TableViewSkin
import javafx.scene.control.skin.VirtualFlow
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.FileChooser
import tornadofx.*
import ui.app.helper.ErrorIndex
import ui.app.helper.ErrorMessages
import ui.app.helper.ErrorTableHelper
import ui.css.CSSMessage.Companion.error
import ui.css.CSSMessage.Companion.info
import ui.view.ControlView
import ui.view.TableView.Companion.COLUMN_HABEN_ID
import ui.view.TableView.Companion.COLUMN_UST_ID
import kotlin.system.exitProcess

class ViewController : Controller() {
    val controlView: ControlView by inject()

    val records = mutableListOf<Data>().asObservable()

    val inputFileOK = SimpleBooleanProperty(false)
    val outputFileOK = SimpleBooleanProperty(false)
    val tableValuesOK = SimpleBooleanProperty(true)

    val errorTableHelper = ErrorTableHelper()

    fun closeApp() {
        exitProcess(0)
    }

    fun validate(editEvent: TableColumn.CellEditEvent<Data, Any>, item: Data) {
        val keyPotentialErrorIndex = ErrorIndex(editEvent.tablePosition.row, COLUMN_HABEN_ID)
        if (editEvent.tableColumn.id == COLUMN_HABEN_ID) {
            if (!Data.Validation.HABEN_REGEX.matches(item.haben.trim())) {
                errorTableHelper.setError(
                    keyPotentialErrorIndex,
                    ErrorMessages.INVALID_COLUMN_HABEN_VALUE
                )
                showError(ErrorMessages.INVALID_COLUMN_HABEN_VALUE.message)
            } else {
                showMessage("")
                errorTableHelper.removeError(keyPotentialErrorIndex)
            }
        } else if (editEvent.tableColumn.id == COLUMN_UST_ID) {
            if (!Data.Validation.UST_REGEX.matches(item.umsatzSteuer.trim())) {
                errorTableHelper.setError(
                    keyPotentialErrorIndex,
                    ErrorMessages.INVALID_COLUMN_UST_VALUE
                )
                showError(ErrorMessages.INVALID_COLUMN_UST_VALUE.message)
            } else {
                showMessage("")
                errorTableHelper.removeError(keyPotentialErrorIndex)
            }
        }
        tableValuesOK.value = !errorTableHelper.hasErrors()
    }

    fun onKeyPressedInTable(
        keyEvent: KeyEvent,
        selectedCell: TablePosition<Data, *>?,
        tableView: TableView<Data>
    ) {
        if (keyEvent.code.isValidInput()) {
            if (keyEvent.target is TextField) { // Workaround to edit immediately
                val textField = keyEvent.target as TextField
                textField.selectAll()
                textField.text = keyEvent.text
                textField.positionCaret(1)
                return
            }
            if (selectedCell != null && selectedCell.tableColumn.isEditable) {
                tableView.edit(selectedCell.row, selectedCell.tableColumn)
                val focusedControl = tableView.scene?.focusOwner // textfield
                Event.fireEvent(
                    focusedControl,
                    keyEvent.copyFor(keyEvent.source, focusedControl)
                ) // Workaround to edit immediately
            }
        } else if (keyEvent.code.isArrowKey) {

            when (keyEvent.code) {
                KeyCode.RIGHT -> focusRightOrNextLine(selectedCell!!, tableView, keyEvent)
                KeyCode.LEFT -> focusLeftOrPreviousLine(selectedCell!!, tableView, keyEvent)
                else -> {
                    log.finest("Invalid key code received: " + keyEvent.code)
                }
            }
        }
    }


    private fun focusLeftOrPreviousLine(
        selectedCell: TablePosition<Data, *>,
        tableView: TableView<Data>,
        keyEvent: KeyEvent
    ) {
        val pos = if (selectedCell.tableColumn.id == COLUMN_HABEN_ID && isFirstEditableCell(selectedCell)) {
            selectedCell
        } else if (selectedCell.tableColumn.id == COLUMN_UST_ID) {
            TablePosition(tableView, selectedCell.row, tableView.columns[3]);
        } else if (selectedCell.tableColumn.id == COLUMN_HABEN_ID) {
            TablePosition(tableView, selectedCell.row - 1, tableView.columns[6]);
        } else {
            null
        }
        selectCell(pos, tableView, keyEvent)
    }

    private fun isFirstEditableCell(selectedCell: TablePosition<Data, *>) =
        selectedCell.row == 0

    private fun isLastEditableCell(selectedCell: TablePosition<Data, *>) =
        selectedCell.tableView.items.size - 1 == selectedCell.row

    private fun focusRightOrNextLine(
        selectedCell: TablePosition<Data, *>,
        tableView: TableView<Data>,
        keyEvent: KeyEvent
    ) {
        val pos = if (selectedCell.tableColumn.id == COLUMN_HABEN_ID) {
            TablePosition(tableView, selectedCell.row, tableView.columns[6]);
        } else if (selectedCell.tableColumn.id == COLUMN_UST_ID && isLastEditableCell(selectedCell)) {
            selectedCell
        } else if (selectedCell.tableColumn.id == COLUMN_UST_ID) {
            TablePosition(tableView, selectedCell.row + 1, tableView.columns[3]);
        } else {
            null
        }
        selectCell(pos, tableView, keyEvent)
    }

    private fun selectCell(
        pos: TablePosition<Data, out Any>?,
        tableView: TableView<Data>,
        keyEvent: KeyEvent
    ) {
        pos?.let {
            tableView.selectionModel.select(it.row)
            tableView.selectionModel.clearAndSelect(it.row, it.tableColumn)
            tableView.focusModel.focus(it)

            ((tableView.skin as TableViewSkin<*>).children[1] as VirtualFlow<*>).scrollTo(it.row) // to trigger scrolling if row is not visible
            keyEvent.consume()
        }
    }

    private fun KeyCode.isValidInput(): Boolean {
        return isDigitKey || isLetterKey || isKeypadKey
    }

    private fun selectFile(description: String): String {
        val files = chooseFile(
            mode = FileChooserMode.Single,
            filters = arrayOf(
                FileChooser.ExtensionFilter(description, "*.txt")
            )
        )
        return if (files.isNotEmpty()) files[0].absolutePath else "";
    }

    fun onButtonSelectImportFilePressed() {
        val filePath = selectFile("Dateiformat")
        if (filePath.isNotEmpty()) {
            inputFileOK.value = true
            controlView.inputFile.text = filePath
        }
    }

    fun onButtonImportPressed() {
        records.clear()
        records.addAll(DataParser().convertFile2Data(controlView.inputFile.text))
        showMessage("Datenimport abgeschlossen.")
    }

    fun onButtonSelectExportFilePressed() {
        val filePath = selectFile("Dateiformat")
        if (filePath.isNotEmpty()) {
            outputFileOK.value = true
            controlView.outputFile.text = filePath
        }
    }

    fun onButtonExportPressed() {
        if (records.isEmpty()) {
            showError("Keine Daten für Stapelverarbeitung vorhanden.")
            return
        }
        DataParser().convertData2File(records, controlView.outputFile.text)
        showMessage("Stapelverarbeitung erfolgreich abgeschlossen.")
    }

    private fun showMessage(message: String) {
        controlView.messageLabel.style(false, info())
        controlView.messageLabel.text = message
    }

    private fun showError(message: String) {
        controlView.messageLabel.style(false, error())
        controlView.messageLabel.text = message
    }

    fun save(cellEditEvent: TableColumn.CellEditEvent<Data, Any>, data: Data, model: DataModel) {
        this.validate(cellEditEvent, data)
        model.haben.set(model.haben.get().trim())
        model.umsatzSteuer.set(model.umsatzSteuer.get().trim())
        model.commit()
        cellEditEvent.tableView.refresh()
    }


}
