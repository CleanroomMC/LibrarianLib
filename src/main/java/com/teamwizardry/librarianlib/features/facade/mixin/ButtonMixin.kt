package com.teamwizardry.librarianlib.features.facade.mixin

import com.teamwizardry.librarianlib.core.LibrarianLog
import com.teamwizardry.librarianlib.features.eventbus.Event
import com.teamwizardry.librarianlib.features.eventbus.EventCancelable
import com.teamwizardry.librarianlib.features.facade.EnumMouseButton
import com.teamwizardry.librarianlib.features.facade.component.GuiComponent
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.component.GuiLayerEvents
import com.teamwizardry.librarianlib.features.math.Vec2d

class ButtonMixin(val component: GuiComponent, init: Runnable) {

    constructor(component: GuiComponent, init: () -> Unit) : this(component, Runnable(init))

    class ButtonClickEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val button: EnumMouseButton) : EventCancelable()
    class ButtonStateChangeEvent(@JvmField val component: GuiComponent, val mousePos: Vec2d, val state: EnumButtonState, var newState: EnumButtonState) : Event()

    var state = EnumButtonState.NORMAL

    init {
        if (!component.hasTag(TAG))
            apply()
        else
            LibrarianLog.warn("Component already has button mixin!")
        init.run()
    }

    private fun apply() {

        component.addTag(TAG)

        component.BUS.hook(GuiLayerEvents.PreDrawEvent::class.java) { event ->
            val newState = if (!component.hasTag("enabled")) EnumButtonState.DISABLED
            else if (component.mouseOver) EnumButtonState.HOVER
            else EnumButtonState.NORMAL

            state = component.BUS.fire(ButtonStateChangeEvent(component, component.mousePos, state, newState)).newState
        }

        component.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) { event ->
            if (state != EnumButtonState.DISABLED)
                component.BUS.fire(ButtonClickEvent(component, component.mousePos, event.button))
            state != EnumButtonState.DISABLED
        }
    }

    enum class EnumButtonState {
        NORMAL, DISABLED, HOVER
    }

    companion object {
        val TAG = "HasButtonMixin"
    }
}
