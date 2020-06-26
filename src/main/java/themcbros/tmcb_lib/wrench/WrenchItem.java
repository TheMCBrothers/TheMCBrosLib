package themcbros.tmcb_lib.wrench;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

public class WrenchItem extends Item implements IWrench {

    private final Collection<ItemGroup> itemGroups = Lists.newArrayList();

    public WrenchItem(Function<Properties, Properties> properties) {
        super(properties.apply(new Properties().maxStackSize(1)));
        this.addToItemGroups(ItemGroup.TOOLS);
    }

    public void addToItemGroups(ItemGroup... itemGroups) {
        this.itemGroups.addAll(Arrays.asList(itemGroups));
    }

    public void removeFromGroups(ItemGroup... itemGroups) {
        for (ItemGroup itemGroup : itemGroups) {
            this.itemGroups.remove(itemGroup);
        }
    }

    @Override
    public Collection<ItemGroup> getCreativeTabs() {
        return this.itemGroups;
    }

    @Override
    public boolean doesSneakBypassUse(ItemStack stack, IWorldReader world, BlockPos pos, PlayerEntity player) {
        return true;
    }

    @Override
    public boolean canUseWrench(ItemStack stack, PlayerEntity player, BlockPos pos) {
        return true;
    }
}
