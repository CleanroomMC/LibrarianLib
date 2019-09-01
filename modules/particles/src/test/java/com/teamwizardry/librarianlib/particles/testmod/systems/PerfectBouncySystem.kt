package com.teamwizardry.librarianlib.particles.testmod.systems

import com.teamwizardry.librarianlib.particles.ParticleSystem
import com.teamwizardry.librarianlib.particles.bindings.CallbackBinding
import com.teamwizardry.librarianlib.particles.bindings.ConstantBinding
import com.teamwizardry.librarianlib.particles.modules.BasicPhysicsUpdateModule
import com.teamwizardry.librarianlib.particles.modules.GlLineBeamRenderModule
import com.teamwizardry.librarianlib.particles.modules.SpriteRenderModule
import com.teamwizardry.librarianlib.particles.testmod.modules.VelocityRenderModule
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.Vec3d

object PerfectBouncySystem: TestSystem() {
    override fun configure() {
        val position = bind(3)
        val previousPosition = bind(3)
        val velocity = bind(3)
        val color = bind(4)

        updateModules.add(BasicPhysicsUpdateModule(
            position = position,
            previousPosition = previousPosition,
            velocity = velocity,
            enableCollision = true,
            gravity = 0.02,
            bounciness = 1.0f,
            friction = 0.00f,
            damping = 0.0f
        ))

        renderModules.add(SpriteRenderModule(
            sprite = ResourceLocation("minecraft", "textures/item/clay_ball.png"), // #balance,
            enableBlend = true,
            previousPosition = position,
            position = position,
            color = color,
            size = ConstantBinding(0.05)
        ))
        renderModules.add(VelocityRenderModule(
            blend = true,
            previousPosition = position,
            position = position,
            velocity = velocity,
            color = color,
            size = 1f,
            alpha = null
        ))
    }

    override fun spawn(player: Entity) {
        val eyePos = player.getEyePosition(0f)
        val look = player.lookVec

        val spawnDistance = 2
        val spawnVelocity = 0.2

        this.addParticle(200,
            // position
            eyePos.x + look.x * spawnDistance,
            eyePos.y + look.y * spawnDistance,
            eyePos.z + look.z * spawnDistance,
            // previous position
            eyePos.x + look.x * spawnDistance,
            eyePos.y + look.y * spawnDistance,
            eyePos.z + look.z * spawnDistance,
            // velocity
            look.x * spawnVelocity,
            look.y * spawnVelocity,
            look.z * spawnVelocity,
            // color
            Math.random(),
            Math.random(),
            Math.random(),
            1.0
        )
    }
}