package net.dravigen.letMeMove.render;

import net.dravigen.letMeMove.interfaces.functional.IAnimationCondition;
import net.dravigen.letMeMove.interfaces.functional.IAnimationLeaning;
import net.dravigen.letMeMove.interfaces.functional.IAnimationRender;
import net.minecraft.src.*;

public class AnimationCustom {

    private IAnimationRender animationRender;
    private IAnimationCondition conditons;
    private IAnimationLeaning leaningUpdate;
    private final ResourceLocation animationIdentifier;
    public float height;
    public float speedModifier;

    public AnimationCustom(ResourceLocation animationIdentifier, float height, float speedModifier) {
        this.animationIdentifier = animationIdentifier;
        this.height = height;
        this.speedModifier = speedModifier;
    }

    public ResourceLocation getID() {
        return this.animationIdentifier;
    }


    public void setConditions(IAnimationCondition animationCondition) {
        this.conditons = animationCondition;
    }

    public void setAnimationRender(IAnimationRender render) {
        this.animationRender = render;
    }

    public void setLeaningUpdate(IAnimationLeaning leaningUpdate) {
        this.leaningUpdate = leaningUpdate;
    }

    public boolean isConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
        return this.conditons.isConditionsMet(player, axisAlignedBB);
    }

    public void renderAnimation(Minecraft mc, ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u) {
        this.animationRender.animate(mc, model, entity, f, g, h, i, j, u);
    }

    public void updateLeaning(EntityLivingBase entity) {
        this.leaningUpdate.updateLeaningPitch(entity);
    }
}
