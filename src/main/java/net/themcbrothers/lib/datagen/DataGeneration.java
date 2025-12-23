package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TheMCBrosLib.MOD_ID)
public class DataGeneration {
    @SubscribeEvent
    static void dataGen(final GatherDataEvent.Server event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new LibraryTagsProvider.Blocks(packOutput, lookupProvider));
        generator.addProvider(true, new LibraryTagsProvider.Items(packOutput, lookupProvider));
        generator.addProvider(true, new LibraryRecipeProvider.Runner(packOutput, lookupProvider));
    }
}
