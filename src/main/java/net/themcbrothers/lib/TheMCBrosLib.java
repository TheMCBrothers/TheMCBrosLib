package net.themcbrothers.lib;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.themcbrothers.lib.config.Config;
import net.themcbrothers.lib.crafting.FluidContainerIngredient;
import net.themcbrothers.lib.registration.deferred.ItemDeferredRegister;
import net.themcbrothers.lib.registration.object.ItemObject;
import net.themcbrothers.lib.util.ComponentFormatter;
import net.themcbrothers.lib.wrench.WrenchItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod(TheMCBrosLib.MOD_ID)
public class TheMCBrosLib {
    public static final String MOD_ID = "tmcb_lib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ComponentFormatter TEXT_UTILS = new ComponentFormatter(MOD_ID);

    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MOD_ID);
    public static final ItemObject<WrenchItem> WRENCH = TheMCBrosLib.ITEMS.register("wrench",
            () -> new WrenchItem(properties -> properties));

    public TheMCBrosLib() {
        ForgeMod.enableMilkFluid();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TheMCBrosLib.ITEMS.register(modEventBus);

        modEventBus.register(this);

        MinecraftForge.EVENT_BUS.addListener(this::onPlayerInteractWithEntity);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    public static ResourceLocation rl(String s) {
        return new ResourceLocation(MOD_ID, s);
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
            CraftingHelper.register(FluidContainerIngredient.ID, FluidContainerIngredient.SERIALIZER);
        }
    }

    private void onPlayerInteractWithEntity(final PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() instanceof WrenchItem) {
            Entity target = event.getTarget();

            if (event.getEntity().isSecondaryUseActive()) {
                if (target instanceof Boat boat) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);

                    ItemEntity itemEntity = target.spawnAtLocation(boat.getDropItem());

                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                    }

                    target.discard();
                } else if (target instanceof AbstractMinecart minecart) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);

                    ItemStack stack = new ItemStack(getDropItemFromMinecart(minecart));

                    if (!stack.isEmpty()) {
                        if (target.hasCustomName()) {
                            stack.setHoverName(target.getCustomName());
                        }

                        ItemEntity itemEntity = target.spawnAtLocation(stack);

                        if (itemEntity != null) {
                            itemEntity.setNoPickUpDelay();
                        }
                    }

                    if (minecart instanceof AbstractMinecartContainer minecartContainer) {
                        minecartContainer.chestVehicleDestroyed(event.getLevel().damageSources().generic(), event.getLevel(), target);
                    }

                    target.ejectPassengers();
                    target.kill();
                }
            }
        }
    }

    private Item getDropItemFromMinecart(final AbstractMinecart minecart) {
        Class<? extends AbstractMinecart> clazz = minecart.getClass();

        try {
            Method getDropItem = clazz.getDeclaredMethod("getDropItem");
            getDropItem.setAccessible(true);
            Object o = getDropItem.invoke(minecart);
            return (Item) o;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassCastException ex) {
            throw new RuntimeException(ex);
        }
    }
}
