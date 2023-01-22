package net.themcbrothers.lib.registration.object;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Registry object wrapper to implement {@link ItemLike}
 *
 * @param <I> Item class
 */
public class ItemObject<I extends ItemLike> implements Supplier<I>, ItemLike {
    private final Supplier<? extends I> entry;
    private final ResourceLocation name;

    public ItemObject(RegistryObject<? extends I> entry) {
        this.entry = entry;
        this.name = entry.getId();
    }

    @Override
    public I get() {
        return Objects.requireNonNull(this.entry.get(), () -> "Item Object not present " + this.name);
    }

    @Override
    public @NotNull Item asItem() {
        return this.get().asItem();
    }

    public ResourceLocation getId() {
        return this.name;
    }
}
