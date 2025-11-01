package net.dravigen.let_me_move.mixin.client.render;

import net.dravigen.let_me_move.animation.BaseAnimation;
import net.dravigen.let_me_move.interfaces.ICustomMovementEntity;
import net.dravigen.let_me_move.utils.AnimationUtils;
import net.minecraft.src.*;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Mixin(GuiIngame.class)
public abstract class GuiIngameMixin extends Gui {
	
	@Shadow @Final private Minecraft mc;
	
	@Redirect(method = "renderGameOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/src/GuiIngame;drawTexturedModalRect(IIIIII)V", ordinal = 2))
	private void disable1CrossWhenF5(GuiIngame instance, int i, int i1, int i2, int i3, int i4, int i5) {
		if (this.mc.gameSettings.thirdPersonView == 0) {
			instance.drawTexturedModalRect(i ,i1, i2, i3, i4, i5);
		}
	}
	
	
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
		
		String name = animation.getName(player);
		
		int y = scaledHeight - fontRenderer.FONT_HEIGHT - 1;
		
		this.drawString(fontRenderer,
						name,
						scaledWidth - fontRenderer.getStringWidth(name) - 2,
						y,
						Color.white.getRGB());
		
		y -= 5;
		
		Map<ResourceLocation, BaseAnimation> cooldownAnims = new HashMap<>();
		
		for (BaseAnimation animCool : AnimationUtils.getAnimationsMap().values()) {
			int cooldown = animCool.cooldown;
			
			if (animCool.hasCooldown() && cooldown > 0) {
				cooldownAnims.put(animCool.getID(), animCool);
			}
		}
		
		cooldownAnims = cooldownAnims.entrySet()
				.stream()
				.sorted(Comparator.comparingInt(animCooldown -> animCooldown.getValue().cooldown))
				.collect(Collectors.toMap(Map.Entry::getKey,
										  Map.Entry::getValue,
										  (oldValue, newValue) -> oldValue,
										  LinkedHashMap::new));
		
		
		for (BaseAnimation animCool : cooldownAnims.values()) {
			String string = animCool.getName(player);
			
			int cooldownWidth = 60;
			float delta = (float) cooldownWidth / animCool.maxCooldown;
			y -= fontRenderer.FONT_HEIGHT - 1;
			
			GL11.glPushMatrix();
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			
			int var1 = (int) (animCool.cooldown * delta);
			
			for (int i = 0; i <= var1; i++) {
				float diff = (float) i / cooldownWidth;
				Color color = diff < 2 / 3f ? diff < 1 / 3f ? Color.green : Color.yellow : Color.red;
				Gui.drawRect(scaledWidth - 2 - i,
							 y - 1,
							 scaledWidth - 2,
							 y + fontRenderer.FONT_HEIGHT,
							 80 << 24 | color.getRed() << 16 | color.getGreen() << 8 | color.getBlue());
			}
			
			GL11.glDisable(3042);
			GL11.glPopMatrix();
			
			float diff = (float) animCool.cooldown / animCool.maxCooldown;
			Color color = diff <= 2 / 3f ? diff <= 1 / 3f ? Color.green : Color.yellow : Color.red;
			
			this.drawCenteredString(fontRenderer, string, scaledWidth - 2 - cooldownWidth / 2, y, color.getRGB());
			
			y -= 3;
		}
	}
}
