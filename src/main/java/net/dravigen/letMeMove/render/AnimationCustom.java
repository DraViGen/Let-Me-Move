package net.dravigen.letMeMove.render;

import net.dravigen.letMeMove.interfaces.functional.IAnimationCondition;
import net.dravigen.letMeMove.interfaces.functional.IAnimationLeaning;
import net.dravigen.letMeMove.interfaces.functional.IAnimationRender;
import net.minecraft.src.*;

public class AnimationCustom {

    private IAnimationRender animationRender;
    private IAnimationCondition activationConditions;
    private IAnimationCondition generalConditions;
    private IAnimationLeaning leaningUpdate;
    private final ResourceLocation animationIdentifier;
    public float height;
    public float speedModifier;
    public boolean needLeaningUpdate;

    public AnimationCustom(ResourceLocation animationIdentifier, float height, float speedModifier, boolean needLeaningUpdate) {
        this.animationIdentifier = animationIdentifier;
        this.height = height;
        this.speedModifier = speedModifier;
        this.needLeaningUpdate = needLeaningUpdate;
    }

    public ResourceLocation getID() {
        return this.animationIdentifier;
    }

    public void setActivationConditions(IAnimationCondition animationCondition) {
        this.activationConditions = animationCondition;
    }

    public void setGeneralConditions(IAnimationCondition animationCondition) {
        this.generalConditions = animationCondition;
    }

    public void setAnimationRender(IAnimationRender render) {
        this.animationRender = render;
    }

    public void setLeaningUpdate(IAnimationLeaning leaningUpdate) {
        this.leaningUpdate = leaningUpdate;
    }

    public boolean isActivationConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
        return this.activationConditions.isConditionsMet(player, axisAlignedBB);
    }

    public boolean isGeneralConditonsMet(EntityPlayer player, AxisAlignedBB axisAlignedBB) {
        return this.generalConditions.isConditionsMet(player, axisAlignedBB);
    }

    public void renderAnimation(Minecraft mc, ModelBiped model, EntityLivingBase entity, float f, float g, float h, float i, float j, float u, boolean tr) {
        this.animationRender.animate(mc, model, entity, f, g, h, i, j, u, tr);
    }

    public void updateLeaning(EntityLivingBase entity) {
        this.leaningUpdate.updateLeaningPitch(entity);
    }
}
