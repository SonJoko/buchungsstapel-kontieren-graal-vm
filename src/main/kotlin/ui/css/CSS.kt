package ui.css

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*

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

class CSSMessage {

    companion object {
        fun info(): InlineCss.() -> Unit = {
            backgroundColor += Color.SEASHELL
            fontWeight = FontWeight.BOLD
            textFill = Color.DARKGREEN
            padding = box(10.px)
            fillWidth = true
        }
        fun error(): InlineCss.() -> Unit = {
            backgroundColor += Color.SEASHELL
            fontWeight = FontWeight.BOLD
            textFill = Color.RED
            padding = box(10.px)
            fillWidth = true
        }
    }

}