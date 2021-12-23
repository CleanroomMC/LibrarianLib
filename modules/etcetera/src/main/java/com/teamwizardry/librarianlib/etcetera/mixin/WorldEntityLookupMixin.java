package com.teamwizardry.librarianlib.etcetera.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(World.class)
public interface WorldEntityLookupMixin {
    @Invoker
    EntityLookup<Entity> callGetEntityLookup();
}
