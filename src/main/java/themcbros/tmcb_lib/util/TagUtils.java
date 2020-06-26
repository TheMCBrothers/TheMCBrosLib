package themcbros.tmcb_lib.util;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;

public class TagUtils {

    // ITEM TAGS

    public static Tag<Item> forgeItemTag(String path) {
        return new ItemTags.Wrapper(new ResourceLocation(ForgeVersion.MOD_ID, path));
    }

    public static Tag<Item> modItemTag(String modID, String path) {
        return new ItemTags.Wrapper(new ResourceLocation(modID, path));
    }

    public static Tag<Item> modItemTag(ResourceLocation location) {
        return new ItemTags.Wrapper(location);
    }

    // BLOCK TAGS

    public static Tag<Block> forgeBlockTag(String path) {
        return new BlockTags.Wrapper(new ResourceLocation(ForgeVersion.MOD_ID, path));
    }

    public static Tag<Block> modBlockTag(String modID, String path) {
        return new BlockTags.Wrapper(new ResourceLocation(modID, path));
    }

    public static Tag<Block> modBlockTag(ResourceLocation location) {
        return new BlockTags.Wrapper(location);
    }

    // FLUID TAGS

    public static Tag<Fluid> forgeFluidTag(String path) {
        return new FluidTags.Wrapper(new ResourceLocation(ForgeVersion.MOD_ID, path));
    }

    public static Tag<Fluid> vanillaFluidTag(String path) {
        return new FluidTags.Wrapper(new ResourceLocation(path));
    }

    public static Tag<Fluid> modFluidTag(String modID, String path) {
        return new FluidTags.Wrapper(new ResourceLocation(modID, path));
    }

    public static Tag<Fluid> modFluidTag(ResourceLocation location) {
        return new FluidTags.Wrapper(location);
    }

}
