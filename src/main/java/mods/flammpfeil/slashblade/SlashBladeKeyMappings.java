package mods.flammpfeil.slashblade;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;

public class SlashBladeKeyMappings {
    public static final KeyMapping KEY_SPECIAL_MOVE = new KeyMapping("key.slashblade.special_move", KeyConflictContext.IN_GAME,
            KeyModifier.NONE, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.slashblade");
}
