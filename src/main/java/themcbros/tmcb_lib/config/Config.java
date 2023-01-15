package themcbros.tmcb_lib.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import themcbros.tmcb_lib.TheMCBrosLib;

@Mod.EventBusSubscriber(modid = TheMCBrosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static final ClientConfig CLIENT_CONFIG;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> clientConfigPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);

        CLIENT_CONFIG = clientConfigPair.getLeft();
        CLIENT_SPEC = clientConfigPair.getRight();
    }

    @SubscribeEvent
    public static void bakeConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC)
            CLIENT_CONFIG.bake();
    }

}
