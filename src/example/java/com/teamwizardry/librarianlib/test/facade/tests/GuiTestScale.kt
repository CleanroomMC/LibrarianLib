package com.teamwizardry.librarianlib.test.facade.tests

import com.teamwizardry.librarianlib.features.facade.GuiBase
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.helpers.vec
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class GuiTestScale : GuiBase() {
    init {
        main.size = vec(100, 100)

        val p = GuiComponent(0,0)
        p.scale = 2.0
        p.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            p.scale = 1/p.scale
            p.pos += vec(2, 2)
        }
        val c = ComponentRect(-10, -10, 50, 50)
        c.color = Color.RED
        val bg = ComponentRect(25, 25, 50, 50)
        bg.color = Color.GREEN
        val scissor = GuiComponent(5, 5, 30, 30)
        scissor.clipToBounds = true
        scissor.add(c)
        bg.add(scissor)
        p.add(bg)
        main.add(p)

    }
}
