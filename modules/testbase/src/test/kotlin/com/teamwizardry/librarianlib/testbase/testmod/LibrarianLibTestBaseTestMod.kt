package com.teamwizardry.librarianlib.testbase.testmod

import com.teamwizardry.librarianlib.math.vec
import com.teamwizardry.librarianlib.testbase.LibrarianLibTestBaseModule
import com.teamwizardry.librarianlib.testbase.TestMod
import com.teamwizardry.librarianlib.testbase.objects.TestBlock
import com.teamwizardry.librarianlib.testbase.objects.TestBlockConfig
import com.teamwizardry.librarianlib.testbase.objects.TestItem
import com.teamwizardry.librarianlib.testbase.objects.TestTileEntity
import net.minecraft.tileentity.ITickableTileEntity
import net.minecraft.tileentity.TileEntityType
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Mod("librarianlib-testbase-test")
object LibrarianLibTestBaseTestMod: TestMod(LibrarianLibTestBaseModule) {
    init {
        +TestItem(TestItemConfig("right_click", "Right Click") {
            common {
                rightClick { chat("[Common] rightClick") }
                rightClickAir { chat("[Common] rightClickAir") }
                rightClickBlock { chat("[Common] rightClickBlock") }
                rightClickEntity { chat("[Common] rightClickEntity") }
                rightClickHold { chat("[Common] rightClickHold") }
                rightClickRelease { chat("[Common] rightClickRelease") }
            }
            client {
                rightClick { chat("[Client] rightClick") }
                rightClickAir { chat("[Client] rightClickAir") }
                rightClickBlock { chat("[Client] rightClickBlock") }
                rightClickEntity { chat("[Client] rightClickEntity") }
                rightClickHold { chat("[Client] rightClickHold") }
                rightClickRelease { chat("[Client] rightClickRelease") }
            }
            server {
                rightClick { chat("[Server] rightClick") }
                rightClickAir { chat("[Server] rightClickAir") }
                rightClickBlock { chat("[Server] rightClickBlock") }
                rightClickEntity { chat("[Server] rightClickEntity") }
                rightClickHold { chat("[Server] rightClickHold") }
                rightClickRelease { chat("[Server] rightClickRelease") }
            }
        })
        +TestItem(TestItemConfig("left_click", "Left Click") {
            common {
                leftClickBlock { chat("[Common] leftClickBlock") }
                leftClickEntity { chat("[Common] leftClickEntity") }
            }
            client {
                leftClickBlock { chat("[Client] leftClickBlock") }
                leftClickEntity { chat("[Client] leftClickEntity") }
            }
            server {
                leftClickBlock { chat("[Server] leftClickBlock") }
                leftClickEntity { chat("[Server] leftClickEntity") }
            }
        })
        +TestItem(TestItemConfig("inventory_tick", "Inventory Tick") {
            common {
                inventoryTick { sneaking { chat("[Common] inventoryTick") } }
                tickInHand { sneaking { chat("[Common] tickInHand") } }
            }
            client {
                inventoryTick { sneaking { chat("[Client] inventoryTick") } }
                tickInHand { sneaking { chat("[Client] tickInHand") } }
            }
            server {
                inventoryTick { sneaking { chat("[Server] inventoryTick") } }
                tickInHand { sneaking { chat("[Server] tickInHand") } }
            }
        })

        +TestBlock(TestBlockConfig("normal_solid", "Normal Solid Block") {
        })
        +TestBlock(TestBlockConfig("normal_transparent", "Normal Transparent Block") {
            transparent = true
        })

        +TestBlock(TestBlockConfig("directional_solid", "Directional Solid Block") {
            directional = true
        })
        +TestBlock(TestBlockConfig("directional_transparent", "Directional Transparent Block") {
            directional = true
            transparent = true
        })

        +TestBlock(TestBlockConfig("right_click", "Right Click Block") {
            client {
                rightClick { chat("[Client] rightClick") }
            }
            common {
                rightClick { chat("[Common] rightClick") }
            }
            server {
                rightClick { chat("[Server] rightClick") }
            }
        })

        +TestBlock(TestBlockConfig("simple_tile", "Simple Tile Entity") {
            class Tile(tileEntityTypeIn: TileEntityType<*>): TestTileEntity(tileEntityTypeIn), ITickableTileEntity {
                var ticks: Int = 0

                override fun tick() {
                    ticks++
                }
            }

            tile(::Tile)

            rightClick.client {
                val tile = world.getTileEntity(pos) as? Tile ?: return@client
                player.sendMessage(StringTextComponent("[Client] ${tile.ticks} ticks"))
            }

            rightClick.server {
                val tile = world.getTileEntity(pos) as? Tile ?: return@server
                player.sendMessage(StringTextComponent("[Server] ${tile.ticks} ticks"))
            }
        })

        +TestEntityConfig("cube_renderer", "Cube Renderer") {

        }

        +TestScreenConfig("blank", "Empty Screen") {

        }

        +TestScreenConfig("screen_size_zero", "Zero-sized screen") {
            description = "(0, 0) should be located at the center of the screen"
            draw {
                fill(0, 0, 10, 10, 0xFFFF00FFu)
            }
        }

        +TestScreenConfig("screen_size_nonzero", "Non-zero sized screen") {
            description = "The (20, 20) size means (10, 10) should be located at the center of the screen"
            size = vec(20, 20)
            draw {
                fill(0, 0, 20, 20, 0x7FFF00FFu)
            }
        }

        +UnitTestSuite("unit_tests") {
            add<UnitTestTests>()
        }
    }
}

internal val logger = LibrarianLibTestBaseTestMod.makeLogger(null)
