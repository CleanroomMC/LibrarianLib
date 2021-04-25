package com.teamwizardry.librarianlib.prism.testmod.nbt

import com.teamwizardry.librarianlib.core.util.kotlin.TagBuilder
import com.teamwizardry.librarianlib.prism.nbt.ListSerializerFactory
import dev.thecodewarrior.mirror.Mirror
import dev.thecodewarrior.prism.DeserializationException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ListFactoryTests: NBTPrismTest() {
    @Test
    fun `the serializer for an ArrayList should be a ListSerializer`() {
        val serializer = prism[Mirror.reflect<ArrayList<String>>()].value
        assertEquals(ListSerializerFactory.ListSerializer::class.java, serializer.javaClass)
        assertSame(Mirror.reflect<ArrayList<String>>(), serializer.type)
    }

    @Test
    fun `read+write for ArrayList should be symmetrical`() {
        simple<ArrayList<String?>, ListSerializerFactory.ListSerializer>(
            arrayListOf("first", "second", null, "fourth"),
            TagBuilder.list {
                +compound { "V" *= string("first") }
                +compound { "V" *= string("second") }
                +compound {}
                +compound { "V" *= string("fourth") }
            }
        )
    }

    @Test
    fun `reading an ArrayList with an existing value should clear and fill the existing list`() {
        val targetList = arrayListOf("value")

        val theList = arrayListOf<String?>("junk")
        val theTag = TagBuilder.list {
            +compound { "V" *= string("value") }
        }
        val deserialized = prism[Mirror.reflect<ArrayList<String?>>()].value.read(theTag, theList)

        assertSame(theList, deserialized)
        assertEquals(targetList, deserialized)
    }

    @Test
    fun `reading an ArrayList with no existing value should create a new list`() {
        val targetList = arrayListOf("value")

        val theTag = TagBuilder.list {
            +compound { "V" *= string("value") }
        }
        val deserialized = prism[Mirror.reflect<ArrayList<String?>>()].value.read(theTag, null)

        assertEquals(ArrayList::class.java, deserialized.javaClass)
        assertEquals(targetList, deserialized)
    }

    @Test
    fun `reading an ArrayList with the wrong NBT type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<ArrayList<String?>>()].value.read(TagBuilder.string("oops!"), null)
        }
    }

    @Test
    fun `reading an ArrayList with the wrong ListNBT element type should throw`() {
        assertThrows<DeserializationException> {
            prism[Mirror.reflect<ArrayList<String?>>()].value.read(TagBuilder.list { +string("oops!") }, null)
        }
    }

    @Test
    fun `read+write nested lists with the same serializer should be symmetrical`() {
        @Suppress("NestedLambdaShadowedImplicitParameter")
        val list = Foo().also {
            it.add(Foo().also {
                it.add(Foo())
                it.add(Foo())
            })
            it.add(Foo().also {
                it.add(Foo())
                it.add(Foo())
                it.add(Foo())
            })
        }

        val targetTag = TagBuilder.list {
            +compound {
                "V" *= list {
                    +compound { "V" *= list {} }
                    +compound { "V" *= list {} }
                }
            }
            +compound {
                "V" *= list {
                    +compound { "V" *= list {} }
                    +compound { "V" *= list {} }
                    +compound { "V" *= list {} }
                }
            }
        }

        simple<Foo, ListSerializerFactory.ListSerializer>(list, targetTag)
    }

    class Foo: ArrayList<Foo>()
}