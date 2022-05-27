package ui.app

import mu.KotlinLogging
import tornadofx.App
import tornadofx.launch
import ui.app.ui.view.MasterView
import ui.css.CSSTableCell


class KontierenApp : App(MasterView::class, CSSTableCell::class)

fun main(args: Array<String>) {
    val logger = KotlinLogging.logger {}
    logger.info { "asd" }
    launch<KontierenApp>(args)
}

// TODO:

// Schriftgröße anpassen
// Version