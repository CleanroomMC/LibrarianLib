package com.teamwizardry.librarianlib.features.facade.component.supporting

import com.teamwizardry.librarianlib.features.facade.component.GuiLayer
import java.util.Collections

interface ILayerData {
    /**
     * Returns all valid data keys for [clazz].
     */
    fun <D : Any> getAllDataKeys(clazz: Class<D>): Set<String>

    /**
     * Returns all classes for data that contain at least one value.
     */
    fun getAllDataClasses(): Set<Class<*>>

    fun <D : Any> setData(clazz: Class<D>, key: String, value: D)

    fun <D : Any> removeData(clazz: Class<D>, key: String)

    fun <D> getData(clazz: Class<D>, key: String): D?

    fun <D> hasData(clazz: Class<D>, key: String): Boolean

    /**
     * Sets the value associated with the pair of keys [clazz] and `""`. The value must be a subclass of [clazz]
     */
    fun <D : Any> setData(clazz: Class<D>, value: D)

    /**
     * Removes the value associated with the pair of keys [clazz] and `""`
     */
    fun <D : Any> removeData(clazz: Class<D>)

    /**
     * Returns the value Associated with the pair of keys [clazz] and `""` if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    fun <D : Any> getData(clazz: Class<D>): D?

    /**
     * Checks if there is a value associated with the pair of keys [clazz] and `""`
     */
    fun <D : Any> hasData(clazz: Class<D>): Boolean
}

/**
 * TODO: Document file LayerDataHandler
 *
 * Created by TheCodeWarrior
 */
class LayerDataHandler: ILayerData {
    lateinit var layer: GuiLayer

    private val data: MutableMap<Class<*>, MutableMap<String, Any>> = mutableMapOf()

    /**
     * Returns all valid data keys for [clazz].
     */
    override fun <D : Any> getAllDataKeys(clazz: Class<D>): Set<String> {
        if (!data.containsKey(clazz))
            return setOf()
        return Collections.unmodifiableSet(data[clazz]?.keys)
    }

    /**
     * Returns all classes for data that contain at least one value.
     */
    override fun getAllDataClasses(): Set<Class<*>> {
        return data.entries.filter { it.value.isNotEmpty() }.map { it.key }.toMutableSet()
    }

    /** [GuiLayer.setData] */
    override fun <D : Any> setData(clazz: Class<D>, key: String, value: D) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
            data[clazz]?.put(key, value)
    }

    /** [GuiLayer.removeData] */
    override fun <D : Any> removeData(clazz: Class<D>, key: String) {
        if (!data.containsKey(clazz))
            data.put(clazz, mutableMapOf())
            data[clazz]?.remove(key)
    }

    /** [GuiLayer.getData] */
    @Suppress("UNCHECKED_CAST")
    override fun <D> getData(clazz: Class<D>, key: String): D? {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return data[clazz]?.get(key) as D?
    }

    /** [GuiLayer.hasData] */
    @Suppress("UNCHECKED_CAST")
    override fun <D> hasData(clazz: Class<D>, key: String): Boolean {
        if (!data.containsKey(clazz))
            data.put(clazz, HashMap<String, Any>())
        return data[clazz]?.get(key) as D? != null
    }

    /**
     * Sets the value associated with the pair of keys [clazz] and `""`. The value must be a subclass of [clazz]
     */
    override fun <D : Any> setData(clazz: Class<D>, value: D) {
        setData(clazz, "", value)
    }

    /**
     * Removes the value associated with the pair of keys [clazz] and `""`
     */
    override fun <D : Any> removeData(clazz: Class<D>) {
        removeData(clazz, "")
    }

    /**
     * Returns the value Associated with the pair of keys [clazz] and `""` if it exists, else it returns null.
     * The value will be an instance of [clazz]
     */
    override fun <D : Any> getData(clazz: Class<D>): D? {
        return getData(clazz, "")
    }

    /**
     * Checks if there is a value associated with the pair of keys [clazz] and `""`
     */
    override fun <D : Any> hasData(clazz: Class<D>): Boolean {
        return hasData(clazz, "")
    }
}

inline fun <reified D : Any> ILayerData.setData(key: String, value: D) = this.setData(D::class.java, key, value)
inline fun <reified D : Any> ILayerData.removeData(key: String) = this.removeData(D::class.java, key)
inline fun <reified D> ILayerData.getData(key: String): D? = this.getData(D::class.java, key)
inline fun <reified D> ILayerData.hasData(key: String): Boolean = this.hasData(D::class.java, key)
inline fun <reified D : Any> ILayerData.setData(value: D) = this.setData(D::class.java, value)
inline fun <reified D : Any> ILayerData.removeData() = this.removeData(D::class.java)
inline fun <reified D : Any> ILayerData.getData(): D? = this.getData(D::class.java)
inline fun <reified D : Any> ILayerData.hasData(): Boolean = this.hasData(D::class.java)
