package themcbros.tmcb_lib.util;

import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.versions.forge.ForgeVersion;
import org.lwjgl.system.CallbackI;

public class TagUtils {

    // ITEM TAGS

    public static ITag.INamedTag<Item> forgeItemTag(String id) {
        return ItemTags.makeWrapperTag(ForgeVersion.MOD_ID + ':' + id);
    }

    public static ITag.INamedTag<Item> modItemTag(String modID, String id) {
        return ItemTags.makeWrapperTag(modID + ':' + id);
    }

    public static ITag.INamedTag<Item> modItemTag(ResourceLocation location) {
        return ItemTags.makeWrapperTag(location.toString());
    }

    // BLOCK TAGS

    public static ITag.INamedTag<Block> forgeBlockTag(String id) {
        return BlockTags.makeWrapperTag(ForgeVersion.MOD_ID + ':' + id);
    }

    public static ITag.INamedTag<Block> modBlockTag(String modID, String id) {
        return BlockTags.makeWrapperTag(modID + ':' + id);
    }

    public static ITag.INamedTag<Block> modBlockTag(ResourceLocation id) {
        return BlockTags.makeWrapperTag(id.toString());
    }

    // FLUID TAGS

    public static ITag.INamedTag<Fluid> forgeFluidTag(String id) {
        return FluidTags.makeWrapperTag(ForgeVersion.MOD_ID + ':' + id);
    }

    public static ITag.INamedTag<Fluid> vanillaFluidTag(String id) {
        return FluidTags.makeWrapperTag(id);
    }

    public static ITag.INamedTag<Fluid> modFluidTag(String modID, String id) {
        return FluidTags.makeWrapperTag(modID + ':' + id);
    }

    public static ITag.INamedTag<Fluid> modFluidTag(ResourceLocation id) {
        return FluidTags.makeWrapperTag(id.toString());
    }

}
