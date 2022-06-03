import mu.KotlinLogging
import tornadofx.App
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus
import ui.css.CSSTableCell
import ui.view.MasterView


class KontierenApp : App(MasterView::class, CSSTableCell::class) {
    init {
        reloadStylesheetsOnFocus()
    }

    companion object {
        fun main(args: Array<String>) {
            val logger = KotlinLogging.logger {}
            logger.info { "Starte Anwendung..." }
            launch<KontierenApp>(args)
        }
    }
}

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    logger.info { "Starte Anwendung..." }
    launch<KontierenApp>(args)
}
