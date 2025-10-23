package net.dravigen.let_me_move.mixin.client.render;

import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.dravigen.let_me_move.utils.AnimationUtils;
import net.minecraft.src.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin extends Gui {
	
	@Shadow @Final private Minecraft mc;
	
	@Inject(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;renderDebugOverlay()V"))
	private void renderAnimationTexts(float par1, boolean par2, int par3, int par4, CallbackInfo ci) {
		EntityClientPlayerMP player = mc.thePlayer;
		ICustomMovementEntity customPlayer = (ICustomMovementEntity) player;
		BaseAnimation animation = customPlayer.llm_$getAnimation();
		FontRenderer fontRenderer = mc.fontRenderer;
		ScaledResolution scaledresolution = new ScaledResolution(this.mc.gameSettings,
																 this.mc.displayWidth,
																 this.mc.displayHeight);
		int scaledWidth = scaledresolution.getScaledWidth();
		int scaledHeight = scaledresolution.getScaledHeight();
		
		String name = getAnimName(animation);
		
		int y = scaledHeight - fontRenderer.FONT_HEIGHT - 1;
		
		this.drawString(fontRenderer,
						name,
						scaledWidth - fontRenderer.getStringWidth(name) - 2,
						y,
						Color.white.getRGB());
		
		y -= 5;
		
		for (BaseAnimation animCool : AnimationUtils.getAnimationsMap().values()) {
			int cooldown = animCool.cooldown;
			if (animCool.hasCooldown() && cooldown > 0) {
				String cooldownS = String.valueOf(cooldown);
				
				name = getAnimName(animCool);
				
				String string = cooldownS + ": " + name;
				
				this.drawString(fontRenderer, string,
								scaledWidth - fontRenderer.getStringWidth(string) - 2,
								y -= fontRenderer.FONT_HEIGHT - 1,
								Color.red.getRGB());
				
			}
		}
	}
	
	@Unique
	private static String getAnimName(BaseAnimation animCool) {
		return StatCollector.translateToLocal(animCool.getID().getResourceDomain() + ".animation." + animCool.getID().getResourcePath());
	}
	
	
}
