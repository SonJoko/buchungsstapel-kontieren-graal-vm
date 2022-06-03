package ui.view

import javafx.scene.text.FontWeight
import tornadofx.*
import helper.VersionHelper

class VersionView : Fragment("Versionsinformationen") {
    override val root = vbox {
        setPrefSize(300.0, 140.0)
        style {
            padding = box(10.px)
            fontSize = 11.pt
        }
        hbox(spacing = 10) {
            style {
                padding = box(2.px)
            }
            label("Name: ") {
                setPrefSize(70.0, 20.0);
            }
            label(VersionHelper.versionData.name) {
                style {
                    fontWeight = FontWeight.BOLD
                }
                setPrefSize(180.0, 20.0);
            }
        }
        hbox(spacing = 10) {
            style {
                padding = box(2.px)
            }
            label("Version: ") {
                setPrefSize(70.0, 20.0);
            }
            label(VersionHelper.versionData.version) {
                style {
                    fontWeight = FontWeight.BOLD
                }
                setPrefSize(180.0, 20.0);
            }
        }
        hbox(spacing = 10) {
            style {
                padding = box(2.px)
            }
            label("Author: ") {
                setPrefSize(70.0, 20.0);
            }
            label(VersionHelper.versionData.author) {
                style {
                    fontWeight = FontWeight.BOLD
                }
                setPrefSize(180.0, 20.0);
            }
        }
        hbox(spacing = 10) {
            style {
                padding = box(2.px)
            }
            label("Lizenz: ") {
                setPrefSize(70.0, 20.0);
            }
            label(VersionHelper.versionData.license) {
                style {
                    fontWeight = FontWeight.BOLD
                }
                setPrefSize(180.0, 20.0);
            }
        }
    }
}