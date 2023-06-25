package net.themcbrothers.lib.datagen;

import net.minecraft.DetectedVersion;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = TheMCBrosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGeneration {
    @SubscribeEvent
    static void dataGen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new LibraryTagsProvider.Blocks(packOutput, lookupProvider, existingFileHelper));
        generator.addProvider(event.includeServer(), new LibraryRecipeProvider(packOutput));

        // pack.mcmeta
        generator.addProvider(true, new PackMetadataGenerator(packOutput))
                .add(PackMetadataSection.TYPE, new PackMetadataSection(Component.literal("TheMCBrosLib"),
                        DetectedVersion.BUILT_IN.getPackVersion(PackType.CLIENT_RESOURCES), Arrays.stream(PackType.values())
                        .collect(Collectors.toMap(Function.identity(), DetectedVersion.BUILT_IN::getPackVersion))));
    }
}
