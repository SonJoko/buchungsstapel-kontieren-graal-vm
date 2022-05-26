package ui.css

import javafx.geometry.Pos
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.cssclass
import tornadofx.px

class CSSTableCell : Stylesheet() {

    companion object {
        val center by cssclass()
        val left by cssclass()
        val right by cssclass()
    }

    init {
        center {
            padding = box(5.px)
            alignment = Pos.BASELINE_CENTER
        }
        left {
            padding = box(5.px)
            alignment = Pos.BASELINE_LEFT
        }
        right {
            padding = box(5.px)
            alignment = Pos.BASELINE_RIGHT
        }
    }
}