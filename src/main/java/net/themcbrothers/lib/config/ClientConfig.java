package net.themcbrothers.lib.config;

import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.themcbrothers.lib.energy.EnergyUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.EnumSet;
import java.util.Locale;
import java.util.StringJoiner;

/**
 * Client configuration
 */
public class ClientConfig {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ModConfigSpec.EnumValue<EnergyUnit> energyUnit;
    private final ModConfigSpec.ConfigValue<String> modNameFormatFriendly;

    private String cachedModNameFormat;

    ClientConfig(ModConfigSpec.Builder builder) {
        builder.comment("Welcome to the client config file!");

        builder.push("gui");
        this.energyUnit = builder
                .comment("Energy Unit rendered in machines")
                .translation("config.tmcb_lib.gui.energyUnit")
                .defineEnum("energyUnit", EnergyUnit.FORGE_ENERGY);
        builder.pop();

        EnumSet<ChatFormatting> validFormatting = EnumSet.allOf(ChatFormatting.class);
        validFormatting.remove(ChatFormatting.RESET);

        StringJoiner validColorsJoiner = new StringJoiner(", ");
        StringJoiner validFormatsJoiner = new StringJoiner(", ");

        for (ChatFormatting chatFormatting : validFormatting) {
            String lowerCaseName = chatFormatting.getName().toLowerCase(Locale.ROOT);
            if (chatFormatting.isColor()) {
                validColorsJoiner.add(lowerCaseName);
            } else if (chatFormatting.isFormat()) {
                validFormatsJoiner.add(lowerCaseName);
            }
        }

        String validColors = validColorsJoiner.toString();
        String validFormats = validFormatsJoiner.toString();

        builder.push("formatting");
        this.modNameFormatFriendly = builder
                .comment(
                        "How the mod name should be formatted in the tooltip. Leave blank to disable.",
                        "Use these formatting colors:", validColors,
                        "With these formatting options:", validFormats
                )
                .translation("config.tmcb_lib.formatting.modNameFormat")
                .define("modNameFormat", "blue italic");
        builder.pop();
    }

    /**
     * Gets called when config loads or reloads
     */
    void bake() {
        this.cachedModNameFormat = null;
    }

    public void setEnergyUnit(EnergyUnit energyUnit) {
        this.energyUnit.set(energyUnit);
        Config.CLIENT_SPEC.save();
    }

    public EnergyUnit getEnergyUnit() {
        return this.energyUnit.get();
    }

    /**
     * Gets and parses the config value of the mod name format setting
     *
     * @return Mod Name formatting in tooltips in (non-friendly format)
     */
    public String getModNameFormat() {
        if (this.cachedModNameFormat == null) {
            this.cachedModNameFormat = parseFriendlyFormat(this.modNameFormatFriendly.get());
        }

        return this.cachedModNameFormat;
    }

    private static String parseFriendlyFormat(String friendlyFormat) {
        if (friendlyFormat.isEmpty()) {
            return "";
        }

        StringBuilder format = new StringBuilder();
        String[] strings = friendlyFormat.split(" ");

        for (String string : strings) {
            ChatFormatting formatting = ChatFormatting.getByName(string);

            if (formatting != null) {
                format.append(formatting);
            } else {
                LOGGER.error("Invalid format: " + string);
            }
        }

        return format.toString();
    }
}
