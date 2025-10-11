package net.dravigen.letMeMove.interfaces.functional;

import net.minecraft.src.EntityLivingBase;
import net.minecraft.src.Minecraft;
import net.minecraft.src.ModelBiped;

@FunctionalInterface
public interface IAnimationRender {
    void animate(Minecraft mc, ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, float delta);
}
