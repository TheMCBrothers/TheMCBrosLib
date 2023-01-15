package net.themcbrothers.lib.registration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Objects;

@Deprecated
public class FluidRegistryObject<STILL extends Fluid, FLOWING extends Fluid, BLOCK extends LiquidBlock, BUCKET extends BucketItem> {
    private RegistryObject<STILL> stillRegistryObject;
    private RegistryObject<FLOWING> flowingRegistryObject;
    private RegistryObject<BLOCK> blockRegistryObject;
    private RegistryObject<BUCKET> bucketRegistryObject;

    public FluidRegistryObject(String modId, String name) {
        this.stillRegistryObject = RegistryObject.create(new ResourceLocation(modId, name), ForgeRegistries.FLUIDS);
        this.flowingRegistryObject = RegistryObject.create(new ResourceLocation(modId, "flowing_" + name), ForgeRegistries.FLUIDS);
        this.blockRegistryObject = RegistryObject.create(new ResourceLocation(modId, name), ForgeRegistries.BLOCKS);
        this.bucketRegistryObject = RegistryObject.create(new ResourceLocation(modId, name + "_bucket"), ForgeRegistries.ITEMS);
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

    void updateStill(RegistryObject<STILL> stillRegistryObject) {
        this.stillRegistryObject = Objects.requireNonNull(stillRegistryObject);
    }

    void updateFlowing(RegistryObject<FLOWING> flowingRegistryObject) {
        this.flowingRegistryObject = Objects.requireNonNull(flowingRegistryObject);
    }

    void updateBlock(RegistryObject<BLOCK> blockRegistryObject) {
        this.blockRegistryObject = Objects.requireNonNull(blockRegistryObject);
    }

    void updateBucket(RegistryObject<BUCKET> bucketRegistryObject) {
        this.bucketRegistryObject = Objects.requireNonNull(bucketRegistryObject);
    }
}
