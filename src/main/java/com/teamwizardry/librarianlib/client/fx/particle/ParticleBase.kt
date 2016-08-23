package com.teamwizardry.librarianlib.client.fx.particle

import com.teamwizardry.librarianlib.LibrarianLog
import com.teamwizardry.librarianlib.client.fx.particle.functions.RenderFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.InterpFunction
import com.teamwizardry.librarianlib.common.util.math.interpolate.StaticInterp
import com.teamwizardry.librarianlib.common.util.minus
import com.teamwizardry.librarianlib.common.util.plus
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.Particle
import net.minecraft.client.renderer.VertexBuffer
import net.minecraft.entity.Entity
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import java.awt.Color
import java.util.*
import com.teamwizardry.librarianlib.common.util.math.interpolate.position.*
import com.teamwizardry.librarianlib.client.fx.particle.functions.*
import com.teamwizardry.librarianlib.common.util.times
import net.minecraft.util.ResourceLocation
import java.util.concurrent.ThreadLocalRandom

/**
 * Created by TheCodeWarrior
 */
class ParticleBase(
        val world: World,
        val position: Vec3d,
        val lifetime: Int,
        val animStart: Int,
        val animOverflow: Int,
        val positionFunc: InterpFunction<Vec3d>,
        val easing: InterpFunction<Float>,
        val colorFunc: InterpFunction<Color>,
        val renderFunc: RenderFunction,
        val movementMode: EnumMovementMode,
        val scaleFunc: InterpFunction<Float>,
        val motionEnabled: Boolean,
        var motion: Vec3d,
        val acceleration: Vec3d,
        val deceleration: Vec3d,
        val friction: Vec3d,
        val jitterMagnitude: Vec3d = Vec3d(0.05, 0.05, 0.05),
        val jitterChance: Float = 0.1f
) : Particle(world, 0.0, 0.0, 0.0) {

    private var lastPos: Vec3d = positionFunc?.get(0f) ?: Vec3d.ZERO
    private var jitterMotion: Vec3d = Vec3d.ZERO

    private val randomNum: Int = ThreadLocalRandom.current().nextInt()

    init {
        particleMaxAge = lifetime
        setPosition(lastPos + position)
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ

        val sprite = Minecraft.getMinecraft().textureMapBlocks.getAtlasSprite("wizardry:particles/sparkle")
        this.setParticleTexture(sprite)
    }

    private fun setPosition(vec: Vec3d) {
        setPosition(vec.xCoord, vec.yCoord, vec.zCoord)
    }

    override fun getFXLayer(): Int {
        return 1
    }

    override fun onUpdate() {
        particleAge++
        this.prevPosX = this.posX
        this.prevPosY = this.posY
        this.prevPosZ = this.posZ
        val i = ( particleAge.toFloat() + animStart) / ( particleMaxAge.toFloat() + animOverflow - animStart )

        if(particleAge > particleMaxAge) {
            this.setExpired()
        }

        var pos = (
                if(motionEnabled)
                    Vec3d.ZERO
                else
                    positionFunc.get(Math.min(1f, easing.get(i)))
                )
        pos += jitterMotion
        if(motionEnabled) {
            pos += motion

            motion += acceleration
            motion *= deceleration
            if(this.isCollided)
                motion *= friction
        }

        if(ThreadLocalRandom.current().nextFloat() < jitterChance) {
            jitterMotion += jitterMagnitude * Vec3d(
                    ThreadLocalRandom.current().nextDouble()*2.0 - 1.0,
                    ThreadLocalRandom.current().nextDouble()*2.0 - 1.0,
                    ThreadLocalRandom.current().nextDouble()*2.0 - 1.0
            )
        }

        if(movementMode == EnumMovementMode.PHASE) {
            setPosition(pos + position)
        } else {
            val direction: Vec3d
            if(movementMode == EnumMovementMode.IN_DIRECTION) {
                direction = pos - lastPos
            } else { // effectivly `else if(movementMode == EnumMovementMode.TOWARD_POINT)`, only else to avoid errors
                direction = pos - ( Vec3d(posX, posY, posZ) - position )
            }
            this.motionX = direction.xCoord
            this.motionY = direction.yCoord
            this.motionZ = direction.zCoord
            this.moveEntity(this.motionX, this.motionY, this.motionZ)
        }

        lastPos = pos
    }

    override fun renderParticle(worldRendererIn: VertexBuffer, entityIn: Entity?, partialTicks: Float, rotationX: Float, rotationZ: Float, rotationYZ: Float, rotationXY: Float, rotationXZ: Float) {
        val i = Math.min(1f, ( particleAge.toFloat() + partialTicks ) / particleMaxAge.toFloat())

        val posX = this.prevPosX + (this.posX - this.prevPosX) * partialTicks.toDouble() - Particle.interpPosX
        val posY = this.prevPosY + (this.posY - this.prevPosY) * partialTicks.toDouble() - Particle.interpPosY
        val posZ = this.prevPosZ + (this.posZ - this.prevPosZ) * partialTicks.toDouble() - Particle.interpPosZ

        val brightness = this.getBrightnessForRender(partialTicks)
        val skyLight = brightness shr 16 and 65535
        val blockLight = brightness and 65535

        renderFunc.render(i, this, colorFunc.get(i), worldRendererIn, entityIn, partialTicks,
                rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ,
                scaleFunc.get(i), posX, posY, posZ, skyLight, blockLight)
    }

    override fun isTransparent(): Boolean {
        return true
    }
}

/**
 * Create a particle builder
 *
 * Particle builders are used to easily create particles and allow you to pass the
 * particle definition to various methods such as [ParticleSpawner.spawn]
 *
 */
class ParticleBuilder(private var lifetime: Int) {
    var animOverflow: Int = 0
        private set
    var animStart: Int = 0
        private set
    var positionFunc: InterpFunction<Vec3d>? = null
        private set
    var easingFunc: InterpFunction<Float> = InterpFunction.ONE_TO_ONE
        private set
    var scaleFunc: InterpFunction<Float> = StaticInterp(1f)
        private set
    var colorFunc: InterpFunction<Color>? = null
        private set
    var renderFunc: RenderFunction? = null
        private set
    var movementMode: EnumMovementMode = EnumMovementMode.IN_DIRECTION
        private set

    var motion: Vec3d = Vec3d.ZERO
        private set
    var acceleration: Vec3d = Vec3d(0.0, -0.04*0.04, 0.0)
        private set
    var deceleration: Vec3d = Vec3d(0.98, 0.98, 0.98)
        private set
    var friction: Vec3d = Vec3d(0.7, 1.0, 0.7)
        private set
    var jitterMagnitude: Vec3d = Vec3d(0.05, 0.05, 0.05)
        private set
    var jitterChance: Float = 0.0f
        private set
    var motionEnabled: Boolean = false
        private set
    /**
     * Set the number of ticks the particle will live
     */
    fun setLifetime(value: Int): ParticleBuilder {
        lifetime = value
        return this
    }

    /**
     * Set the starting point of the animation (in lifetime ticks).
     *
     * Allows you to start in the middle of an animation
     */
    fun setAnimStart(value: Int): ParticleBuilder {
        animStart = value
        return this
    }

    /**
     * Set the overflow amount of the animation (in lifetime ticks).
     *
     * Allows you to have the particle die before it finishes the animation
     */
    fun setAnimOverflow(value: Int): ParticleBuilder {
        animOverflow = value
        return this
    }

    /**
     * Set the position function for the particle.
     *
     * Positions are relative to the position specified in the [build] method
     *
     * @see StaticInterp
     * @see InterpLine
     * @see InterpHelix
     * @see InterpCircle
     * @see InterpBezier3D
     * @see InterpUnion
     */
    fun setPosition(value: InterpFunction<Vec3d>): ParticleBuilder {
        positionFunc = value
        return this
    }

    /**
     * Sets the motion
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun setMotion(value: Vec3d): ParticleBuilder {
        motion = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the motion
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun addMotion(value: Vec3d): ParticleBuilder {
        motion += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the acceleration
     *
     * Each tick this value is added to the particle's motion
     *
     * (calling this method enables standard particle motion)
     */
    fun setAcceleration(value: Vec3d): ParticleBuilder {
        acceleration = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the acceleration
     *
     * Each tick this value is added to the particle's motion
     *
     * (calling this method enables standard particle motion)
     */
    fun addAcceleration(value: Vec3d): ParticleBuilder {
        acceleration += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the deceleration
     *
     * Each tick the particle's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun setDeceleration(value: Vec3d): ParticleBuilder {
        deceleration = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the deceleration
     *
     * Each tick the particle's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun addDeceleration(value: Vec3d): ParticleBuilder {
        deceleration += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the friction
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun setFriction(value: Vec3d): ParticleBuilder {
        friction = value
        motionEnabled = true
        return this
    }

    /**
     * Adds to the friction
     *
     * Each tick while the particle is colliding with a block, it's motion is multiplied by this vector
     *
     * (calling this method enables standard particle motion)
     */
    fun addFriction(value: Vec3d): ParticleBuilder {
        friction += value
        motionEnabled = true
        return this
    }

    /**
     * Sets the motion enabled flag
     *
     * The motion enabled flag controls whether the particle uses the position function or traditional motion mechanics
     */
    fun setMotionEnabled(value: Boolean): ParticleBuilder {
        motionEnabled = value
        return this
    }

    /**
     * Sets the motion enabled flag to true
     *
     * The motion enabled flag controls whether the particle uses the position function or traditional motion mechanics
     */
    fun enableMotion(): ParticleBuilder {
        motionEnabled = true
        return this
    }

    /**
     * Sets the motion enabled flag to false
     *
     * The motion enabled flag controls whether the particle uses the position function or traditional motion mechanics
     */
    fun disableMotion(): ParticleBuilder {
        motionEnabled = false
        return this
    }

    /**
     * Set jitter amount.
     *
     * Each tick there is a 1 in [chance] chance of `rand(-1 to 1) *` each of [value]'s components being added
     * to the particle's motion.
     */
    fun setJitter(chance: Int, value: Vec3d): ParticleBuilder {
        jitterMagnitude = value
        jitterChance = 1f / chance
        return this
    }

    /**
     * Set the scale function for the particle.
     */
    fun setScale(value: InterpFunction<Float>): ParticleBuilder {
        scaleFunc = value
        return this
    }

    /**
     * Shortcut for a static scale
     */
    fun setScale(value: Float): ParticleBuilder {
        scaleFunc = StaticInterp(value)
        return this
    }

    /**
     * Set the color function for the particle.
     *
     * @see InterpColorComponents
     * @see InterpColorHSV
     */
    fun setColor(value: InterpFunction<Color>): ParticleBuilder {
        colorFunc = value
        return this
    }

    /**
     * Shortcut for creating a static color
     */
    fun setColor(value: Color): ParticleBuilder {
        colorFunc = StaticInterp(value)
        return this
    }

    /**
     * Set the render function for the particle
     *
     * @see RenderFunctionBasic
     */
    fun setRender(value: RenderFunction): ParticleBuilder {
        renderFunc = value
        return this
    }

    /**
     * Shortcut for creating a basic render function
     */
    fun setRender(value: ResourceLocation): ParticleBuilder {
        renderFunc = RenderFunctionBasic(value)
        return this
    }

    /**
     * Set the movement mode for the particle
     *
     * @see EnumMovementMode
     */
    fun setMovementMode(value: EnumMovementMode): ParticleBuilder {
        movementMode = value
        return this
    }

    /**
     * Build an instance of the particle.
     *
     * Returns null and prints a warning if the color function or render function are null.
     */
    fun build(world: World, pos: Vec3d): ParticleBase? {
        val renderFunc_ = renderFunc

        if(renderFunc_ == null) {
            LibrarianLog.warn("Particle render function was null!!")
            return null
        }

        return ParticleBase(world, pos, lifetime, animStart, animOverflow,
                positionFunc ?: StaticInterp(Vec3d.ZERO), easingFunc, colorFunc ?: StaticInterp(Color.WHITE),
                renderFunc_, movementMode, scaleFunc,
                motionEnabled, motion, acceleration, deceleration, friction, jitterMagnitude, jitterChance)
    }
}

enum class EnumMovementMode {
    /**
     * Particles don't collide, they follow their path exactly and phase through walls
     */
    PHASE,
    /**
     * Particles always try to move toward the point specified by the position function, but will collide with blocks.
     *
     * If a particle's position function is a straight line and it goes through an angled wall, the particle will slide
     * along the wall until it passes the edge, then it will quickly go toward the point it was supposed to be at
     *
     * ```
     * _- == particle path
     * // == wall
     *     __
     * ___-//-___
     * ```
     */
    TOWARD_POINT,
    /**
     * Particles will go the direction specified by the previous and current function values and will collide with blocks.
     *
     * If a particle's position function is a straight line and it goes through an angled wall, the particle will slide
     * along the wall until it passes the edge, then continue to go the direction the position function is moving at
     * that point in time. This may mean the particle doesn't go the distance projected in the position function as it
     * may slow down while it's hitting an object.
     *
     * ```
     * _- == particle path
     * // == wall
     * .. == actual position function location
     * ** == length difference from time spent slowly sliding along wall
     *       ________******
     * ____-//.............
     * ```
     */
    IN_DIRECTION
}