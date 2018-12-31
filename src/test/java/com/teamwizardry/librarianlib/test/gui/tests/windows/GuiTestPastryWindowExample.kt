package com.teamwizardry.librarianlib.test.gui.tests.windows

import com.teamwizardry.librarianlib.features.gui.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.gui.components.ComponentTextField
import com.teamwizardry.librarianlib.features.gui.layers.SpriteLayer
import com.teamwizardry.librarianlib.features.gui.layers.TextLayer
import com.teamwizardry.librarianlib.features.gui.provided.pastry.BackgroundTexture
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryBackground
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryButton
import com.teamwizardry.librarianlib.features.gui.provided.pastry.PastryTexture
import com.teamwizardry.librarianlib.features.gui.provided.pastry.windows.PastryWindow
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.kotlin.div
import com.teamwizardry.librarianlib.features.kotlin.minus
import com.teamwizardry.librarianlib.features.math.Align2d

class GuiTestPastryWindowExample: PastryWindow(200, 100) {
    val titleText = TextLayer(0, 0, 0, 12)
    val headerLayer = PastryBackground(BackgroundTexture.SLIGHT, 2, 2, 0, 0)
    val background = PastryBackground(BackgroundTexture.SLIGHT, 2, 2, 0, 0)

    val valueText = TextLayer(0, 0, 0, 12)
    val openDialogButton = PastryButton("New text", 0, 0, 60)
    val closeButton = PastryButton("X", 1, 1, 12)
    val moarButton = PastryButton("Moar", 0, 0, 40)

    init {
        titleText.anchor = vec(0.5, 0.5)
        titleText.text = "Such example, many wow"
        titleText.align = Align2d.CENTER_TOP

        valueText.anchor = vec(0.5, 0)
        valueText.align = Align2d.CENTER_TOP
        valueText.wrap = true

        openDialogButton.label.align = Align2d.CENTER
        openDialogButton.anchor = vec(0.5, 0.5)

        moarButton.anchor = vec(1, 0)

        header.add(headerLayer, titleText)
        content.add(background, valueText, openDialogButton, closeButton, moarButton)

        minSize = vec(50, 50)
        maxSize = vec(400, 300)

        setNeedsLayout()
        wireEvents()
    }

    fun wireEvents() {
        openDialogButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            GuiTestPastryWindowDialog({
                valueText.text = "Bzzzzt!"
            }, {
                valueText.text = it
            }).open()
        }
        moarButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            GuiTestPastryWindowExample().open()
        }
        closeButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            this.close()
        }
    }

    override fun layoutChildren() {
        super.layoutChildren()
        headerLayer.size = header.size - vec(4, 4)
        titleText.pos = header.size/2
        moarButton.pos = vec(header.width-1, 1)

        valueText.pos = vec(content.size.x/2, 12)
        valueText.width = content.width
        background.size = content.size - vec(4, 4)
        openDialogButton.pos = vec(content.size.x/2, content.size.y - 12)
    }
}

private class GuiTestPastryWindowDialog(
    val failureHandler: () -> Unit, val successHandler: (text: String) -> Unit
): PastryWindow(120, 75) {
    val titleText = TextLayer(0, 10, 120, 12)
    val background = PastryBackground(BackgroundTexture.SLIGHT_ROUND, 2, 2, 0, 0)

    val valueField = ComponentTextField(10, 20, 100, 12)
    val fieldBackground = SpriteLayer(PastryTexture.textfield, 8, 18, 104, 16)
    val okButton = PastryButton("OK", 5, 40, 50)
    val cancelButton = PastryButton("Cancel", 65, 40, 50)

    init {
        titleText.anchor = vec(0.5, 0.5)
        titleText.align = Align2d.CENTER_TOP
        titleText.text = "Enter text"

        okButton.label.align = Align2d.CENTER
        cancelButton.label.align = Align2d.CENTER

        background.zIndex = -1.0
        add(background)
        header.add(titleText)
        content.add(fieldBackground, valueField, okButton, cancelButton)

        titleText.pos = header.size/2
        background.size = this.size - vec(4, 4)

        wireEvents()
    }

    fun wireEvents() {
        var hasRun = false
        valueField.BUS.hook<ComponentTextField.TextSentEvent> {
            successHandler(it.content)
            hasRun = true
            this.close()
        }
        okButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            successHandler(valueField.text)
            hasRun = true
            this.close()
        }
        cancelButton.BUS.hook<GuiComponentEvents.MouseClickEvent> {
            hasRun = true
            failureHandler()
            this.close()
        }
        this.BUS.hook<LoseFocusEvent> {
            if(!hasRun) failureHandler()
            this.close()
        }
    }
}
