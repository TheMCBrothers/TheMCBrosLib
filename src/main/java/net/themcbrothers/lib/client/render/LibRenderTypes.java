package net.themcbrothers.lib.client.render;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderType;
import net.themcbrothers.lib.TheMCBrosLib;

import static net.minecraft.client.renderer.RenderStateShard.*;

/**
 * Class for render types defined by TheMCBrosLib
 */
public final class LibRenderTypes {
    private LibRenderTypes() {
    }

    /**
     * Render type used for the fluid renderer
     */
    public static final RenderType FLUID = RenderType.create(
            TheMCBrosLib.MOD_ID + ":fluid",
            786432,
            false,
            true,
            RenderPipelines.TRANSLUCENT,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(false));

    /**
     * Render type used for the fluid renderer used in GUIs
     */
    public static final RenderType FLUID_GUI = RenderType.create(
            TheMCBrosLib.MOD_ID + ":fluid_gui",
            786432,
            false,
            true,
            RenderPipelines.TRANSLUCENT,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .setOutputState(TRANSLUCENT_TARGET)
                    .createCompositeState(false));
}
