package net.themcbrothers.lib;


import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.themcbrothers.lib.config.Config;
import net.themcbrothers.lib.energy.EnergyContainerItem;
import net.themcbrothers.lib.energy.EnergyConversionStorage;
import net.themcbrothers.lib.util.ComponentFormatter;
import net.themcbrothers.lib.wrench.WrenchItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheMCBrosLib.MOD_ID)
public class TheMCBrosLib {
    public static final String MOD_ID = "tmcb_lib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ComponentFormatter TEXT_UTILS = new ComponentFormatter(MOD_ID);

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.registerItem("wrench", WrenchItem::new, new Item.Properties().stacksTo(1));

    // Data Components
    static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public TheMCBrosLib(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeMod.enableMilkFluid();

        // Components
        LibDataComponents.init();
        DATA_COMPONENT_TYPES.register(modEventBus);

        ITEMS.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onPlayerInteractWithEntity);

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        modEventBus.addListener(EventPriority.HIGH, RegisterCapabilitiesEvent.class, event -> {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof EnergyContainerItem containerItem) {
                    event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> new EnergyConversionStorage(containerItem, stack), item);
                }
            }
        });
    }

    public static ResourceLocation rl(String s) {
        return new ResourceLocation(MOD_ID, s);
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

                    ItemStack stack = minecart.getPickResult();

                    if (stack != null && !stack.isEmpty()) {
                        if (target.hasCustomName()) {
                            stack.set(DataComponents.CUSTOM_NAME, target.getCustomName());
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
}
