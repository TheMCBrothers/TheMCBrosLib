package net.themcbrothers.lib.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.themcbrothers.lib.TheMCBrosLib;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = TheMCBrosLib.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    public static final ClientConfig CLIENT_CONFIG;
    public static final ModConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClientConfig, ModConfigSpec> clientConfigPair = new ModConfigSpec.Builder().configure(ClientConfig::new);

        CLIENT_CONFIG = clientConfigPair.getLeft();
        CLIENT_SPEC = clientConfigPair.getRight();
    }

    @SubscribeEvent
    public static void bakeConfig(final ModConfigEvent event) {
        if (event.getConfig().getSpec() == CLIENT_SPEC)
            CLIENT_CONFIG.bake();
    }
}
