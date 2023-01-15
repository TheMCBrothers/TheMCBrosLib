package themcbros.tmcb_lib;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import themcbros.tmcb_lib.config.Config;
import themcbros.tmcb_lib.wrench.WrenchItem;

@Mod(TheMCBrosLib.MOD_ID)
public class TheMCBrosLib {

    public static final String MOD_ID = "tmcb_lib";
    public static final Logger LOGGER = LogManager.getLogger();

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    public static final RegistryObject<WrenchItem> WRENCH = TheMCBrosLib.ITEMS.register("wrench",
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
    public void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {
        //CraftingHelper.register(rl("fluid"), FluidItemIngredient.Serializer.INSTANCE);
    }

}
