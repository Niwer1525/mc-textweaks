package io.github.ultimateboomer.textweaks.mixin;

import java.nio.IntBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import io.github.ultimateboomer.textweaks.TexTweaks;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.TextureUtil;

@Mixin(value = TextureUtil.class, priority = 900)
public class TextureUtilMixin {
	@Shadow private static void bind(int id) {}

	/**
	 * Set mipmap LOD bias
	 */
	@Overwrite // Using Overwrite instead of Redirect to avoid conflicts with other mods (Distant Horizons)
	public static void allocate(NativeImage.GLFormat internalFormat, int id, int maxLevel, int width, int height) {
		RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
		bind(id);
		if (maxLevel >= 0) {
			GlStateManager.texParameter(3553, 33085, maxLevel);
			GlStateManager.texParameter(3553, 33082, 0);
			GlStateManager.texParameter(3553, 33083, maxLevel);

			// Modified version according to the original code
			GlStateManager.texParameter(3553, 34049, TexTweaks.config.lodBias.enable? TexTweaks.config.lodBias.value : 0.0F);
		}

		for(int i = 0; i <= maxLevel; ++i) {
			GlStateManager.texImage2D(3553, i, internalFormat.getGlConstant(), width >> i, height >> i, 0, 6408, 5121, (IntBuffer)null);
		}
   }
}
