package net.themcbrothers.lib;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.themcbrothers.lib.config.Config;
import net.themcbrothers.lib.registration.deferred.ItemDeferredRegister;
import net.themcbrothers.lib.registration.object.ItemObject;
import net.themcbrothers.lib.util.ComponentFormatter;
import net.themcbrothers.lib.wrench.WrenchItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(TheMCBrosLib.MOD_ID)
public class TheMCBrosLib {
    public static final String MOD_ID = "tmcb_lib";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ComponentFormatter TEXT_UTILS = new ComponentFormatter(MOD_ID);

    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(MOD_ID);
    public static final ItemObject<WrenchItem> WRENCH = TheMCBrosLib.ITEMS.register("wrench",
            () -> new WrenchItem(properties -> properties));

    public TheMCBrosLib() {
        ForgeMod.enableMilkFluid();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        TheMCBrosLib.ITEMS.register(modEventBus);

        modEventBus.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.CLIENT_SPEC);
    }

    public static ResourceLocation rl(String s) {
        return new ResourceLocation(MOD_ID, s);
    }

    @SubscribeEvent
    public void registerRecipeSerializers(RegisterEvent event) {
        if (event.getRegistryKey().equals(ForgeRegistries.Keys.RECIPE_SERIALIZERS)) {
//            CraftingHelper.register(rl("fluid"), FluidItemIngredient.Serializer.INSTANCE);
        }
    }
}
