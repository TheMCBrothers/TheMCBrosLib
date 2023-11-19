package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Deferred register for menu types, automatically mapping a factory argument in {@link IMenuTypeExtension}
 */
public class MenuTypeDeferredRegister extends DeferredRegisterWrapper<MenuType<?>> {
    public MenuTypeDeferredRegister(String modId) {
        super(Registries.MENU, modId);
    }

    /**
     * Registers a menu type
     *
     * @param name    Menu name
     * @param factory Menu factory
     * @return Registry object containing the menu type
     */
    public <C extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<C>> register(String name, IContainerFactory<C> factory) {
        return this.register.register(name, () -> IMenuTypeExtension.create(factory));
    }
}
