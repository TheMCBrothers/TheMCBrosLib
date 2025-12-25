package net.themcbrothers.lib.fluidtank;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.themcbrothers.lib.LibDataComponents;
import net.themcbrothers.lib.TheMCBrosLib;

import java.util.function.Consumer;

public final class FluidTankBlockItem extends BlockItem {
    public FluidTankBlockItem(Block block, Properties properties) {
        super(block, properties.useBlockDescriptionPrefix().component(LibDataComponents.FLUID.get(), SimpleFluidContent.EMPTY));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (display.shows(LibDataComponents.FLUID.get())) {
            ResourceHandler<FluidResource> handler = ItemAccess.forStack(itemStack).getCapability(Capabilities.Fluid.ITEM);

            if (handler == null || handler.size() != 1) {
                return;
            }

            builder.accept(MutableComponent.create(TheMCBrosLib.TEXT_UTILS.fluidName(handler).getContents()).withStyle(ChatFormatting.GRAY));
            builder.accept(TheMCBrosLib.TEXT_UTILS.fluidWithMax(handler).withStyle(ChatFormatting.GRAY));
        }
    }
}
