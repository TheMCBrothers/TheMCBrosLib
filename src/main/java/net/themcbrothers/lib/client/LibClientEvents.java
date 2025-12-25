package net.themcbrothers.lib.client;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialBlockModelRendererEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.themcbrothers.lib.TheMCBrosLib;
import net.themcbrothers.lib.client.render.fluidtank.FluidTankRenderer;
import net.themcbrothers.lib.client.render.fluidtank.FluidTankSpecialRenderer;

@EventBusSubscriber(modid = TheMCBrosLib.MOD_ID)
public class LibClientEvents {
    @SubscribeEvent
    static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemBlockRenderTypes.setRenderLayer(TheMCBrosLib.FLUID_TANK.get(), ChunkSectionLayer.TRANSLUCENT));
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TheMCBrosLib.FLUID_TANK_TYPE.get(), FluidTankRenderer::new);
    }

    @SubscribeEvent
    static void registerSpecialRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(TheMCBrosLib.id("fluid_tank"), FluidTankSpecialRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    static void registerSpecialBlockRenderers(RegisterSpecialBlockModelRendererEvent event) {
        event.register(TheMCBrosLib.FLUID_TANK.get(), new FluidTankSpecialRenderer.Unbaked());
    }
}
