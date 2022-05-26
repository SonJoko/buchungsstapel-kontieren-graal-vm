package ui.controller

import data.Data
import javafx.scene.control.TableColumn
import javafx.scene.control.TablePosition
import javafx.scene.control.TableView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.*
import ui.app.ui.view.ControlView
import ui.app.ui.view.TableView.Companion.COLUMN_HABEN_ID
import ui.app.ui.view.TableView.Companion.COLUMN_UST_ID
import kotlin.system.exitProcess

class ViewController : Controller() {
    val controlView: ControlView by inject()

    val records = listOf(
        Data("07.09", "1", "1400", "1030", "Buchungstext blablabla", "920.00", "19.00"),
        Data("11.11", "2", "4000", "0070", "Buchungstext blablabla", "77770.00", "07.00"),
        Data("07.09", "3", "1400", "1234", "Buchungstext blablabla", "-22420.00", "47.11")
    ).asObservable()

    fun closeApp() {
        exitProcess(0)
    }

    fun validate(data: TableColumn.CellEditEvent<Data, Any>, item: Data?) {
        controlView.messageLabel.text = if (data.tableColumn.id == COLUMN_HABEN_ID) {
            COLUMN_HABEN_ID
        } else if (data.tableColumn.id == COLUMN_UST_ID) {
            COLUMN_UST_ID
        } else {
            ""
        }
    }

    fun onKeyPressedInTable(
        keyEvent: KeyEvent,
        selectedCell: TablePosition<Data, *>?,
        tableView: TableView<Data>
    ) {
        if (keyEvent.code.isValidInput()) {
            if (selectedCell != null && selectedCell.tableColumn.isEditable) {
                tableView.edit(selectedCell.row, selectedCell.tableColumn)
            }
        } else if (keyEvent.code.isArrowKey) {

            when (keyEvent.code) {
                KeyCode.RIGHT -> focusRightOrNextLine(selectedCell!!, tableView, keyEvent)
                KeyCode.LEFT -> focusLeftOrPreviousLine(selectedCell!!, tableView, keyEvent)
                else -> {
                    log.warning("Invalid key code received: " + keyEvent.code)
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
        }else {
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
            tableView.focusModel.focus(it)
            tableView.selectionModel.clearAndSelect(it.row, it.tableColumn)
            keyEvent.consume()
        }
    }

    // KeyCode.isValidInput
    fun KeyCode.isValidInput(): Boolean {
        return isDigitKey || isLetterKey || isKeypadKey
    }
}