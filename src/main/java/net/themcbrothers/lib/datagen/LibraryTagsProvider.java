package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.themcbrothers.lib.LibraryTags;
import net.themcbrothers.lib.TheMCBrosLib;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class LibraryTagsProvider {
    public static class Blocks extends BlockTagsProvider {
        public Blocks(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, TheMCBrosLib.MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            this.tag(LibraryTags.Blocks.WRENCHABLE).addTag(BlockTags.RAILS);
        }
    }

    public static class Items extends ItemTagsProvider {
        public Items(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags) {
            super(output, lookupProvider, blockTags);
        }

        @Override
        protected void addTags(HolderLookup.Provider lookupProvider) {
            this.tag(LibraryTags.Items.TOOLS_WRENCH).add(TheMCBrosLib.WRENCH.value());
        }
    }
}
