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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.themcbrothers.lib.config.Config;
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

//    private static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES = DeferredRegister.create(ForgeRegistries.Keys.INGREDIENT_TYPES, MOD_ID);
//    public static final RegistryObject<IngredientType<FluidContainerIngredient>> FLUID_CONTAINER = INGREDIENT_TYPES.register("fluid_container", () -> new IngredientType<>());

    public TheMCBrosLib() {
        NeoForgeMod.enableMilkFluid();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TheMCBrosLib.ITEMS.register(modEventBus);

        modEventBus.register(this);

        NeoForge.EVENT_BUS.addListener(this::onPlayerInteractWithEntity);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    public static ResourceLocation rl(String s) {
        return new ResourceLocation(MOD_ID, s);
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
//            CraftingHelper.register(FluidContainerIngredient.ID, FluidContainerIngredient.SERIALIZER);
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
