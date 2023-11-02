package net.themcbrothers.lib.capability;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Implementation of {@link ICapabilityProvider} for item stack
 *
 * @param <HANDLER> Type
 */
public class CapabilityProvider<HANDLER> implements ICapabilityProvider {
    protected final HANDLER instance;
    protected final Capability<HANDLER> capability;
    protected final Direction facing;

    public CapabilityProvider(final HANDLER instance, final Capability<HANDLER> capability, @Nullable final Direction facing) {
        this.instance = instance;
        this.capability = capability;
        this.facing = facing;
    }

    private final LazyOptional<HANDLER> holder = LazyOptional.of(this::getInstance);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == getCapability() ? this.holder.cast() : LazyOptional.empty();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return ICapabilityProvider.super.getCapability(cap);
    }

    public final Capability<HANDLER> getCapability() {
        return this.capability;
    }

    public final @NotNull HANDLER getInstance() {
        return this.instance;
    }

    @Nullable
    public Direction getFacing() {
        return this.facing;
    }
}
