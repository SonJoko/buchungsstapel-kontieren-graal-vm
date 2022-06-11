import javafx.scene.image.Image
import mu.KotlinLogging
import tornadofx.App
import tornadofx.launch
import tornadofx.reloadStylesheetsOnFocus
import tornadofx.setStageIcon
import ui.css.CSSTableCell
import ui.view.MasterView


class KontierenApp : App(MasterView::class, CSSTableCell::class) {
    init {
        reloadStylesheetsOnFocus()
        val image = Image("app-icon.png")
        setStageIcon(image)
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
