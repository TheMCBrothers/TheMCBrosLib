package net.themcbrothers.lib.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.themcbrothers.lib.LibDataComponents;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.Collections;
import java.util.stream.Collectors;

public class LibraryBlockLoot extends BlockLootSubProvider {
    protected LibraryBlockLoot(HolderLookup.Provider registries) {
        super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        this.add(TheMCBrosLib.FLUID_TANK.get(), block -> LootTable.lootTable()
                .withPool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1.0F))
                                .add(LootItem.lootTableItem(block)
                                        .apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY)
                                                .include(DataComponents.CUSTOM_NAME)
                                                .include(LibDataComponents.FLUID.get())
                                        )
                                )
                )
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return BuiltInRegistries.BLOCK.stream().filter(block -> BuiltInRegistries.BLOCK.getKey(block).getNamespace().equals(TheMCBrosLib.MOD_ID)).collect(Collectors.toList());
    }
}
