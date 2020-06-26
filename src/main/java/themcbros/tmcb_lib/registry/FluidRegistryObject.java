package themcbros.tmcb_lib.registry;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class FluidRegistryObject<STILL extends Fluid, FLOWING extends Fluid, BLOCK extends FlowingFluidBlock, BUCKET extends BucketItem> {

    private RegistryObject<STILL> stillRegistryObject;
    private RegistryObject<FLOWING> flowingRegistryObject;
    private RegistryObject<BLOCK> blockRegistryObject;
    private RegistryObject<BUCKET> bucketRegistryObject;

    public FluidRegistryObject(String modId, String name) {
        this.stillRegistryObject = RegistryObject.of(new ResourceLocation(modId, name), ForgeRegistries.FLUIDS);
        this.flowingRegistryObject = RegistryObject.of(new ResourceLocation(modId, "flowing_" + name), ForgeRegistries.FLUIDS);
        this.blockRegistryObject = RegistryObject.of(new ResourceLocation(modId, name), ForgeRegistries.BLOCKS);
        this.bucketRegistryObject = RegistryObject.of(new ResourceLocation(modId, name + "_bucket"), ForgeRegistries.ITEMS);
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
