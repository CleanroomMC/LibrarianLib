package com.teamwizardry.librarianlib.test.container

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.BaseWrappers
import com.teamwizardry.librarianlib.features.container.builtin.SlotTypeGhost
import com.teamwizardry.librarianlib.features.facade.component.GuiComponentEvents
import com.teamwizardry.librarianlib.features.facade.components.ComponentRect
import com.teamwizardry.librarianlib.features.facade.components.ComponentSprite
import com.teamwizardry.librarianlib.features.facadecontainer.GuiContainerBase
import com.teamwizardry.librarianlib.features.facadecontainer.builtin.BaseLayouts
import com.teamwizardry.librarianlib.features.helpers.vec
import com.teamwizardry.librarianlib.features.sprite.Texture
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.util.ResourceLocation
import java.awt.Color

/**
 * Created by TheCodeWarrior
 */
class ContainerTest(player: EntityPlayer, tile: TEContainer) : ContainerBase(player) {

    val invPlayer = BaseWrappers.player(player)
    val invBlock = TestWrapper(tile)

    init {
        addSlots(invPlayer)
        addSlots(invBlock)

        transferRule().from(invPlayer.main).from(invPlayer.hotbar).deposit(invPlayer.head).filter {
            it.stack.item == Items.DIAMOND_HELMET
        }

        transferRule().from(invPlayer.main).deposit(invBlock.main)
        transferRule().from(invPlayer.hotbar).deposit(invBlock.small)
        transferRule().from(invBlock.main).deposit(invPlayer.main)
        transferRule().from(invBlock.small).deposit(invPlayer.hotbar)
    }

    companion object {
        val NAME = ResourceLocation("librarianlibtest:container")

        init {
            GuiHandler.registerBasicFacadeContainer(NAME, { player, _, tile -> ContainerTest(player, tile as TEContainer) }, { _, container -> GuiContainerTest(container) })
        }
    }
}

class TestWrapper(te: TEContainer) : InventoryWrapper(te) {
    val main = slots[0..26]
    val small = slots[27..35]

    init {
        small.forEach { it.type = SlotTypeGhost(32, true) }
    }
}

class GuiContainerTest(container: ContainerTest) : GuiContainerBase(container) {
    companion object {
        val TEXTURE = Texture(ResourceLocation("librarianlibtest:textures/gui/containerTest.png"), 256, 256)
        val bg = TEXTURE.getSprite("bg")
        val slider = TEXTURE.getSprite("slider")
    }

    init {
        val b = ComponentSprite(bg, 0, 0)
        main.add(b)
        main.size = vec(197, 166)

        val layout = BaseLayouts.player(container.invPlayer)
        b.add(layout.root)

        layout.armor.pos = vec(6, 12)
        layout.armor.isVisible = true
        layout.offhand.pos = vec(6, 84)
        layout.offhand.isVisible = true
        layout.main.pos = vec(29, 84)

        val grid = BaseLayouts.grid(container.invBlock.main, 9)
        grid.root.pos = vec(29, 12)
        b.add(grid.root)

        val s = ComponentSprite(slider, 197, 79)
        s.isVisible = false
        b.add(s)

        val miniGrid = BaseLayouts.grid(container.invBlock.small, 3)
        miniGrid.root.pos = vec(3, 5)
        s.add(miniGrid.root)

        val button = ComponentRect(178, 68, 12, 11)
        button.color = Color(0, 0, 0, 127)

        button.BUS.hook(GuiComponentEvents.MouseClickEvent::class.java) {
            s.isVisible = !s.isVisible
        }

        b.add(button)
        // CIRCLE!!!
        /*
        grid.rows[2].pos += vec(18*4.5, 40)

        var a = 0.0
        val aFrame = (2*Math.PI)/360
        val aPer = (2*Math.PI)/9
        val radius = 30

        val row = grid.slots[2]

        grid.root.BUS.hook(GuiComponent.ComponentTickEvent::class.java) {
            a += aFrame

            row.forEachIndexed { i, slot ->
                val s = Math.sin(a + aPer*i)
                val c = Math.cos(a + aPer*i)
                slot.pos = vec(c*radius, s*radius) - vec(8, 8)
            }
        }
        */
    }
}

