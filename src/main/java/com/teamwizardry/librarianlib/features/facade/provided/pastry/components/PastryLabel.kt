package com.teamwizardry.librarianlib.features.facade.provided.pastry.components

import com.teamwizardry.librarianlib.features.facade.layers.TextLayer
import com.teamwizardry.librarianlib.features.helpers.rect
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.math.Margins2d
import com.teamwizardry.librarianlib.features.math.Rect2d
import net.minecraft.client.gui.FontRenderer

class PastryLabel: TextLayer {
    constructor(posX: Int, posY: Int, width: Int, height: Int): super(posX, posY, width, height) {
        margins = Margins2d(2.0, 2.0, 0.0, 0.0)
    }
    constructor(posX: Int, posY: Int, text: String): this(posX, posY, 0, 0) {
        this.text = text
        this.fitToText()
    }
    constructor(text: String): this(0, 0, text)
    constructor(posX: Int, posY: Int): this(posX, posY, "")
    constructor(): this(0, 0, "")

    companion object {
        @JvmStatic
        @JvmOverloads
        fun stringSize(text: String, wrap: Int? = null, font: FontRenderer): Rect2d
            = rect(0, 0, font.getStringWidth(text), font.FONT_HEIGHT)
           // = TextLayer.stringSize(AttributedString.fromMC(text), wrap, font).expand(vec(0, 4))

//        @JvmStatic
//        @JvmOverloads
//        fun stringSize(text: AttributedString, wrap: Int? = null, font: Bitfont = Fonts.classic): Rect2d
//            = TextLayer.stringSize(text, wrap, font).expand(vec(0, 4))
    }
}