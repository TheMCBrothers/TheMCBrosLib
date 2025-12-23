package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.themcbrothers.lib.LibraryTags;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.concurrent.CompletableFuture;

public class LibraryTagsProvider {
    public static class Blocks extends BlockTagsProvider {
        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, TheMCBrosLib.MOD_ID);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            this.tag(LibraryTags.Blocks.WRENCHABLE).addTag(BlockTags.RAILS);
        }
    }

    public static class Items extends ItemTagsProvider {
        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider, TheMCBrosLib.MOD_ID);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            this.tag(LibraryTags.Items.TOOLS_WRENCH).add(TheMCBrosLib.WRENCH.value());
        }
    }
}
