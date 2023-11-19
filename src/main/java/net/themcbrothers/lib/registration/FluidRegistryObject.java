package net.themcbrothers.lib.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Objects;

@Deprecated
public class FluidRegistryObject<STILL extends Fluid, FLOWING extends Fluid, BLOCK extends LiquidBlock, BUCKET extends BucketItem> {
    private DeferredHolder<Fluid, STILL> stillRegistryObject;
    private DeferredHolder<Fluid, FLOWING> flowingRegistryObject;
    private DeferredHolder<Block, BLOCK> blockRegistryObject;
    private DeferredHolder<Item, BUCKET> bucketRegistryObject;

    public FluidRegistryObject(String modId, String name) {
        this.stillRegistryObject = DeferredHolder.create(Registries.FLUID, new ResourceLocation(modId, name));
        this.flowingRegistryObject = DeferredHolder.create(Registries.FLUID, new ResourceLocation(modId, "flowing_" + name));
        this.blockRegistryObject = DeferredHolder.create(Registries.BLOCK, new ResourceLocation(modId, name));
        this.bucketRegistryObject = DeferredHolder.create(Registries.ITEM, new ResourceLocation(modId, name + "_bucket"));
    }

    public STILL getStillFluid() {
        return stillRegistryObject.get();
    }

    public FLOWING getFlowingFluid() {
        return flowingRegistryObject.get();
    }

    public BLOCK getBlock() {
        return blockRegistryObject.get();
    }

    public BUCKET getBucket() {
        return bucketRegistryObject.get();
    }

    void updateStill(DeferredHolder<Fluid, STILL> stillRegistryObject) {
        this.stillRegistryObject = Objects.requireNonNull(stillRegistryObject);
    }

    void updateFlowing(DeferredHolder<Fluid, FLOWING> flowingRegistryObject) {
        this.flowingRegistryObject = Objects.requireNonNull(flowingRegistryObject);
    }

    void updateBlock(DeferredHolder<Block, BLOCK> blockRegistryObject) {
        this.blockRegistryObject = Objects.requireNonNull(blockRegistryObject);
    }

    void updateBucket(DeferredHolder<Item, BUCKET> bucketRegistryObject) {
        this.bucketRegistryObject = Objects.requireNonNull(bucketRegistryObject);
    }
}
