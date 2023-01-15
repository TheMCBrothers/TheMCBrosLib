package net.themcbrothers.lib.registration;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Deprecated
public class FluidDeferredRegister {
    private final List<FluidRegistryObject<?, ?, ?, ?>> allFluids = new ArrayList<>();

    private final String modId;

    private final DeferredRegister<Fluid> fluidRegister;
    private final DeferredRegister<Block> blockRegister;
    private final DeferredRegister<Item> itemRegister;

    public FluidDeferredRegister(String modId) {
        this.modId = modId;
        this.fluidRegister = DeferredRegister.create(ForgeRegistries.FLUIDS, modId);
        this.blockRegister = DeferredRegister.create(ForgeRegistries.BLOCKS, modId);
        this.itemRegister = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    public FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, LiquidBlock, BucketItem> register(String name, FluidAttributes.Builder builder, Function<Item.Properties, Item.Properties> bucketProps) {
        String flowingName = "flowing_" + name;
        String bucketName = name + "_bucket";

        FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, LiquidBlock, BucketItem> fluidRegistryObject = new FluidRegistryObject<>(modId, name);
        ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(fluidRegistryObject::getStillFluid, fluidRegistryObject::getFlowingFluid, builder)
                .bucket(fluidRegistryObject::getBucket).block(fluidRegistryObject::getBlock);
        fluidRegistryObject.updateStill(fluidRegister.register(name, () -> new ForgeFlowingFluid.Source(fluidProperties)));
        fluidRegistryObject.updateFlowing(fluidRegister.register(flowingName, () -> new ForgeFlowingFluid.Flowing(fluidProperties)));
        fluidRegistryObject.updateBlock(blockRegister.register(name, () -> new LiquidBlock(fluidRegistryObject::getStillFluid,
                Block.Properties.of(Material.WATER).noCollission().strength(100.0F).noDrops())));
        fluidRegistryObject.updateBucket(itemRegister.register(bucketName, () -> new BucketItem(fluidRegistryObject::getStillFluid,
                bucketProps.apply(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)))));
        allFluids.add(fluidRegistryObject);
        return fluidRegistryObject;
    }

    public FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, LiquidBlock, BucketItem> register(String name, FluidAttributes.Builder builder) {
        return register(name, builder, properties -> properties.tab(CreativeModeTab.TAB_MISC));
    }

    public void register(IEventBus bus) {
        fluidRegister.register(bus);
        blockRegister.register(bus);
        itemRegister.register(bus);
    }

    public List<FluidRegistryObject<?, ?, ?, ?>> getAllFluids() {
        return allFluids;
    }
}
