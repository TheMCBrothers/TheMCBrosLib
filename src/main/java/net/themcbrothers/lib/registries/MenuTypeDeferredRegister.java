package net.themcbrothers.lib.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Deferred register for menu types, automatically mapping a factory argument in {@link IMenuTypeExtension}
 */
public class MenuTypeDeferredRegister extends DeferredRegister<MenuType<?>> {
    private MenuTypeDeferredRegister(String namespace) {
        super(Registries.MENU, namespace);
    }

    public <C extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<C>> register(String name, IContainerFactory<C> factory) {
        return this.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static MenuTypeDeferredRegister create(String namespace) {
        return new MenuTypeDeferredRegister(namespace);
    }
}
