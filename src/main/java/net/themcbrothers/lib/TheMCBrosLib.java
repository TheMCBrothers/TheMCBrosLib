package net.themcbrothers.lib;


import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecartContainer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.transfer.energy.ItemAccessEnergyHandler;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import net.themcbrothers.lib.config.Config;
import net.themcbrothers.lib.energy.EnergyContainerItem;
import net.themcbrothers.lib.fluidtank.FluidTankBlock;
import net.themcbrothers.lib.fluidtank.FluidTankBlockEntity;
import net.themcbrothers.lib.fluidtank.FluidTankBlockItem;
import net.themcbrothers.lib.util.ComponentFormatter;
import net.themcbrothers.lib.util.CreativeTabHelper;
import net.themcbrothers.lib.wrench.WrenchItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheMCBrosLib.MOD_ID)
public class TheMCBrosLib {
    public static final String MOD_ID = "tmcb_lib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ComponentFormatter TEXT_UTILS = new ComponentFormatter(MOD_ID);

    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredBlock<FluidTankBlock> FLUID_TANK = BLOCKS.registerBlock("fluid_tank", FluidTankBlock::new, properties -> properties
            .instrument(NoteBlockInstrument.HAT)
            .strength(0.5F)
            .sound(SoundType.GLASS)
            .noOcclusion()
            .isValidSpawn(Blocks::never)
            .isRedstoneConductor((state, level, pos) -> false)
            .isSuffocating((state, level, pos) -> false)
            .isViewBlocking((state, level, pos) -> false));

    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FluidTankBlockEntity>> FLUID_TANK_TYPE = BLOCK_ENTITY_TYPES.register("fluid_tank", () -> new BlockEntityType<>(FluidTankBlockEntity::new, FLUID_TANK.get()));

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredItem<WrenchItem> WRENCH = ITEMS.registerItem("wrench", WrenchItem::new, item -> new Item.Properties().stacksTo(1));

    // Data Components
    static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MOD_ID);

    public TheMCBrosLib(IEventBus modEventBus, ModContainer modContainer) {
        NeoForgeMod.enableMilkFluid();

        ITEMS.registerItem(FLUID_TANK.getId().getPath(), props -> new FluidTankBlockItem(FLUID_TANK.get(), props));
        CreativeTabHelper.addToCreativeTabs(FLUID_TANK, CreativeModeTabs.FUNCTIONAL_BLOCKS.identifier());

        LibDataComponents.init();
        DATA_COMPONENT_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        ITEMS.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::onPlayerInteractWithEntity);

        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);

        modEventBus.addListener(EventPriority.HIGH, RegisterCapabilitiesEvent.class, event -> {
            for (Item item : BuiltInRegistries.ITEM) {
                if (item instanceof EnergyContainerItem containerItem) {
                    event.registerItem(Capabilities.Energy.ITEM, (stack, itemAccess) -> new ItemAccessEnergyHandler(
                            itemAccess, LibDataComponents.ENERGY.get(), containerItem.getCapacity(),
                            containerItem.getMaxReceive(), containerItem.getMaxExtract()) {
                    }, item);
                }
            }

            event.registerItem(Capabilities.Fluid.ITEM, (stack, itemAccess) -> new ItemAccessFluidHandler(itemAccess, LibDataComponents.FLUID.get(), FluidTankBlockEntity.CAPACITY), FLUID_TANK);
            event.registerBlockEntity(Capabilities.Fluid.BLOCK, TheMCBrosLib.FLUID_TANK_TYPE.get(), (tank, side) -> tank.getTank());
        });
    }

    public static Identifier id(String s) {
        return Identifier.fromNamespaceAndPath(MOD_ID, s);
    }

    private void onPlayerInteractWithEntity(final PlayerInteractEvent.EntityInteract event) {
        if (event.getItemStack().getItem() instanceof WrenchItem) {
            Entity target = event.getTarget();

            if (event.getLevel() instanceof ServerLevel serverLevel && event.getEntity().isSecondaryUseActive()) {
                if (target instanceof Boat boat) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);

                    ItemEntity itemEntity = target.spawnAtLocation(serverLevel, boat.getDropItem());

                    if (itemEntity != null) {
                        itemEntity.setNoPickUpDelay();
                    }

                    target.discard();
                } else if (target instanceof AbstractMinecart minecart) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);

                    ItemStack stack = minecart.getPickResult();

                    if (!stack.isEmpty()) {
                        if (target.hasCustomName()) {
                            stack.set(DataComponents.CUSTOM_NAME, target.getCustomName());
                        }

                        ItemEntity itemEntity = target.spawnAtLocation(serverLevel, stack);

                        if (itemEntity != null) {
                            itemEntity.setNoPickUpDelay();
                        }
                    }

                    if (minecart instanceof AbstractMinecartContainer minecartContainer) {
                        minecartContainer.chestVehicleDestroyed(event.getLevel().damageSources().generic(), serverLevel, target);
                    }

                    target.ejectPassengers();
                    target.kill(serverLevel);
                }
            }
        }
    }
}
