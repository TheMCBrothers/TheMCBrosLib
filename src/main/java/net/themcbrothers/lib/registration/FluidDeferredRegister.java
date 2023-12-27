package net.themcbrothers.lib.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@Deprecated
public class FluidDeferredRegister {
    private final List<FluidRegistryObject<?, ?, ?, ?>> allFluids = new ArrayList<>();

    private final String modId;

    private final DeferredRegister<FluidType> fluidTypeRegister;
    private final DeferredRegister<Fluid> fluidRegister;
    private final DeferredRegister<Block> blockRegister;
    private final DeferredRegister<Item> itemRegister;

    public FluidDeferredRegister(String modId) {
        this.modId = modId;
        this.fluidTypeRegister = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, modId);
        this.fluidRegister = DeferredRegister.create(Registries.FLUID, modId);
        this.blockRegister = DeferredRegister.create(Registries.BLOCK, modId);
        this.itemRegister = DeferredRegister.create(Registries.ITEM, modId);
    }

    public FluidRegistryObject<BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, LiquidBlock, BucketItem> register(String name, FluidType.Properties builder, Function<Item.Properties, Item.Properties> bucketProps) {
        String flowingName = "flowing_" + name;
        String bucketName = name + "_bucket";

        Supplier<FluidType> typeObj = this.fluidTypeRegister.register(name, () -> new FluidType(builder));

        FluidRegistryObject<BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, LiquidBlock, BucketItem> fluidRegistryObject = new FluidRegistryObject<>(modId, name);
        BaseFlowingFluid.Properties fluidProperties = new BaseFlowingFluid.Properties(typeObj, fluidRegistryObject::getStillFluid, fluidRegistryObject::getFlowingFluid)
                .bucket(fluidRegistryObject::getBucket).block(fluidRegistryObject::getBlock);
        fluidRegistryObject.updateStill(fluidRegister.register(name, () -> new BaseFlowingFluid.Source(fluidProperties)));
        fluidRegistryObject.updateFlowing(fluidRegister.register(flowingName, () -> new BaseFlowingFluid.Flowing(fluidProperties)));
        fluidRegistryObject.updateBlock(blockRegister.register(name, () -> new LiquidBlock(fluidRegistryObject::getStillFluid,
                Block.Properties.ofLegacyCopy(Blocks.WATER))));
        fluidRegistryObject.updateBucket(itemRegister.register(bucketName, () -> new BucketItem(fluidRegistryObject::getStillFluid,
                bucketProps.apply(new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)))));
        allFluids.add(fluidRegistryObject);
        return fluidRegistryObject;
    }

    public FluidRegistryObject<BaseFlowingFluid.Source, BaseFlowingFluid.Flowing, LiquidBlock, BucketItem> register(String name, FluidType.Properties builder) {
        return register(name, builder, properties -> properties);
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
