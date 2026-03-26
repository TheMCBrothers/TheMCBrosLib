package net.themcbrothers.lib.datagen.provider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.List;

/**
 * Implementation of {@link ModelProvider} for multiple {@link ModelSubProvider sub-providers}
 */
public class GroupedModelProvider extends ModelProvider {
    private final List<ModelSubProviderFactory> subProviders;

    private GroupedModelProvider(PackOutput output, String modId, List<ModelSubProviderFactory> subProviders) {
        super(output, modId);
        this.subProviders = subProviders;
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        for (var subProvider : this.subProviders) {
            subProvider.create(blockModels, itemModels).register();
        }
    }

    /**
     * Creates a new {@link ModelProvider} for multiple {@link ModelSubProvider sub-providers}
     *
     * @param modId        Namespace
     * @param subProviders Providers
     * @return Provider
     */
    public static Factory<DataProvider> create(String modId, ModelSubProviderFactory... subProviders) {
        var subProviderList = List.of(subProviders);
        return output -> new GroupedModelProvider(output, modId, subProviderList);
    }

    // This matches the super-class constructor of ModelSubProvider
    @FunctionalInterface
    public interface ModelSubProviderFactory {
        ModelSubProvider create(BlockModelGenerators blockModels, ItemModelGenerators itemModels);
    }
}
