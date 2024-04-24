package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TheMCBrosLib.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGeneration {
    @SubscribeEvent
    static void dataGen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        LibraryTagsProvider.Blocks blockTagsProvider = new LibraryTagsProvider.Blocks(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new LibraryTagsProvider.Items(packOutput, lookupProvider, blockTagsProvider.contentsGetter()));
        generator.addProvider(event.includeServer(), new LibraryRecipeProvider(packOutput, lookupProvider));
    }
}
