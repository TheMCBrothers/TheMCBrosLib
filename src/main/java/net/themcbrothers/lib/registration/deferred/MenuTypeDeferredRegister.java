package net.themcbrothers.lib.registration.deferred;

import net.minecraft.core.Registry;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.RegistryObject;

/**
 * Deferred register for menu types, automatically mapping a factory argument in {@link IForgeMenuType}
 */
public class MenuTypeDeferredRegister extends DeferredRegisterWrapper<MenuType<?>> {
    public MenuTypeDeferredRegister(String modId) {
        super(Registry.MENU_REGISTRY, modId);
    }

    /**
     * Registers a menu type
     *
     * @param name    Menu name
     * @param factory Menu factory
     * @return Registry object containing the menu type
     */
    public <C extends AbstractContainerMenu> RegistryObject<MenuType<C>> register(String name, IContainerFactory<C> factory) {
        return this.register.register(name, () -> IForgeMenuType.create(factory));
    }
}
