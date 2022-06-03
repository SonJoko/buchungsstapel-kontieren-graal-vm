package ui.app.helper

class ErrorTableHelper {
    private val errors: MutableMap<ErrorIndex, ErrorMessages> = mutableMapOf()

    fun hasErrors() = errors.isNotEmpty()

    fun setError(errorIndex: ErrorIndex, errorType: ErrorMessages) {
        errors[errorIndex] = errorType
    }

    fun removeError(errorIndex: ErrorIndex) {
        errors.remove(errorIndex)
    }
}

enum class ErrorMessages(val message: String) {
    INVALID_COLUMN_HABEN_VALUE("Ungültiger Wert in Spalte HABEN."),
    INVALID_COLUMN_UST_VALUE("Ungültiger Wert in Spalte UMSATZSTEUER.")

}

data class ErrorIndex(val row: Int, val col: String)
