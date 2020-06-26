package themcbros.tmcb_lib.registry;

import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    public FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, FlowingFluidBlock, BucketItem> register(String name, FluidAttributes.Builder builder, Function<Item.Properties, Item.Properties> bucketProps) {
        String flowingName = "flowing_" + name;
        String bucketName = name + "_bucket";

        FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, FlowingFluidBlock, BucketItem> fluidRegistryObject = new FluidRegistryObject<>(modId, name);
        ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(fluidRegistryObject::getStillFluid, fluidRegistryObject::getFlowingFluid, builder)
                .bucket(fluidRegistryObject::getBucket).block(fluidRegistryObject::getBlock);
        fluidRegistryObject.updateStill(fluidRegister.register(name, () -> new ForgeFlowingFluid.Source(fluidProperties)));
        fluidRegistryObject.updateFlowing(fluidRegister.register(flowingName, () -> new ForgeFlowingFluid.Flowing(fluidProperties)));
        fluidRegistryObject.updateBlock(blockRegister.register(name, () -> new FlowingFluidBlock(fluidRegistryObject::getStillFluid,
                Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())));
        fluidRegistryObject.updateBucket(itemRegister.register(bucketName, () -> new BucketItem(fluidRegistryObject::getStillFluid,
                bucketProps.apply(new Item.Properties().maxStackSize(1).containerItem(Items.BUCKET)))));
        allFluids.add(fluidRegistryObject);
        return fluidRegistryObject;
    }

    public FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, FlowingFluidBlock, BucketItem> register(String name, FluidAttributes.Builder builder) {
        return register(name, builder, properties -> properties.group(ItemGroup.MISC));
    }

    public FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, FlowingFluidBlock, BucketItem> registerVanilla(String name, FluidAttributes.Builder builder) {
        String flowingName = "flowing_" + name;

        FluidRegistryObject<ForgeFlowingFluid.Source, ForgeFlowingFluid.Flowing, FlowingFluidBlock, BucketItem> fluidRegistryObject = new FluidRegistryObject<>(modId, name);
        ForgeFlowingFluid.Properties fluidProperties = new ForgeFlowingFluid.Properties(fluidRegistryObject::getStillFluid, fluidRegistryObject::getFlowingFluid, builder)
                .bucket(fluidRegistryObject::getBucket).block(fluidRegistryObject::getBlock);
        fluidRegistryObject.updateStill(fluidRegister.register(name, () -> new ForgeFlowingFluid.Source(fluidProperties)));
        fluidRegistryObject.updateFlowing(fluidRegister.register(flowingName, () -> new ForgeFlowingFluid.Flowing(fluidProperties)));
        fluidRegistryObject.updateBlock(blockRegister.register(name, () -> new FlowingFluidBlock(fluidRegistryObject::getStillFluid,
                Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())));
        fluidRegistryObject.updateBucket(RegistryObject.of(new ResourceLocation("milk"), ForgeRegistries.ITEMS));
        allFluids.add(fluidRegistryObject);
        return fluidRegistryObject;
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
