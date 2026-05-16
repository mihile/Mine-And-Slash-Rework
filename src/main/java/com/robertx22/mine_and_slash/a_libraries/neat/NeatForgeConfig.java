package com.robertx22.mine_and_slash.a_libraries.neat;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.network.NetworkConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Locale;


public class NeatForgeConfig {
    private static ModConfigSpec spec;

    public static void init() {
        Pair<ForgeNeatConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(ForgeNeatConfig::new);
        NeatConfig.instance = specPair.getLeft();
        spec = specPair.getRight();
    }

    public static String defaultConfigName(ModConfig.Type type, String modId) {
        // config file name would be "forge-client.toml" and "forge-server.toml"
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }

    public static void register(ModContainer modContainer) {
        NeatForgeConfig.init();
        modContainer.registerConfig(ModConfig.Type.CLIENT, spec, defaultConfigName(ModConfig.Type.CLIENT, "mine_and_slash_neat"));
    }

    private static class ForgeNeatConfig implements NeatConfig.ConfigAccess {
        private final ConfigValue<Integer> maxDistance;
        private final ConfigValue<Boolean> renderInF1;
        private final ConfigValue<Double> heightAbove;
        private final ConfigValue<Boolean> drawBackground;
        private final ConfigValue<Integer> backgroundPadding;
        private final ConfigValue<Integer> backgroundHeight;
        private final ConfigValue<Integer> barHeight;
        private final ConfigValue<Integer> plateSize;
        private final ConfigValue<Integer> plateSizeBoss;
        private final ConfigValue<Boolean> showAttributes;
        private final ConfigValue<Boolean> showArmor;
        private final ConfigValue<Boolean> groupArmor;
        private final ConfigValue<Boolean> colorByType;
        private final ConfigValue<Integer> hpTextHeight;
        private final ConfigValue<Boolean> showMaxHP;
        private final ConfigValue<Boolean> showCurrentHP;
        private final ConfigValue<Boolean> showPercentage;
        private final ConfigValue<Boolean> showOnPlayers;
        private final ConfigValue<Boolean> showOnBosses;
        private final ConfigValue<Boolean> showOnlyFocused;
        private final ConfigValue<Boolean> showFullHealth;
        private final ConfigValue<Boolean> enableDebugInfo;
        private final ConfigValue<Integer> backgroundAlpha;
        private final ConfigValue<Integer> barAlpha;
        private final ConfigValue<Double> debuffIconYOffset;
        private final ConfigValue<Double> debuffIconXOffset;
        private final ConfigValue<Integer> wikiListY0;
        private final ConfigValue<Integer> wikiListY1Offset;
        private final ConfigValue<Integer> wikiListItemHeight;
        private final ConfigValue<List<? extends String>> blacklist;

        public ForgeNeatConfig(ModConfigSpec.Builder builder) {
            builder.push("general");

            maxDistance = builder.define("Max Distance", 12);
            renderInF1 = builder.define("Render with Interface Disabled (F1)", false);
            heightAbove = builder.define("Height Above Mob", 0.6);
            drawBackground = builder.define("Draw Background", true);
            backgroundPadding = builder.define("Background Padding", 2);
            backgroundHeight = builder.define("Background Height", 6);
            barHeight = builder.define("Health Bar Height", 4);
            plateSize = builder.define("Plate Size", 25);
            plateSizeBoss = builder.define("Plate Size (Boss)", 50);
            showAttributes = builder.define("Show Attributes", true);
            showArmor = builder.define("Show Armor", true);
            groupArmor = builder.define("Group Armor (condense 5 iron icons into 1 diamond icon)", true);
            colorByType = builder.define("Color Health Bar by Type (instead of health percentage)", false);
            hpTextHeight = builder.define("HP Text Height", 14);
            showMaxHP = builder.define("Show Max HP", true);
            showCurrentHP = builder.define("Show Current HP", true);
            showPercentage = builder.define("Show HP Percentage", true);
            showOnPlayers = builder.define("Display on Players", true);
            showOnBosses = builder.define("Display on Bosses", true);
            showOnlyFocused = builder.define("Only show the health bar for the entity looked at", false);
            showFullHealth = builder.define("Show entities with full health", true);
            enableDebugInfo = builder.define("Show Debug Info with F3", true);
            backgroundAlpha = builder.comment("Background plate alpha (0-255). Higher = more opaque. Default 64.")
                    .defineInRange("Background Alpha", 64, 0, 255);
            barAlpha = builder.comment("Health bar alpha (0-255). Higher = more opaque. Default 127.")
                    .defineInRange("Bar Alpha", 127, 0, 255);

            builder.push("debuff_icons");
            debuffIconYOffset = builder.comment("Y offset for status effect icons. Positive = above health bar, Negative = below. Default -9.0.")
                    .define("Debuff Icon Y Offset", -9.0);
            debuffIconXOffset = builder.comment("X offset for status effect icons. Default 0.0.")
                    .define("Debuff Icon X Offset", 0.0);
            builder.pop();

            builder.push("wiki_ui");
            wikiListY0 = builder.comment("The top Y coordinate of the wiki list. Default 48.")
                    .defineInRange("Wiki List Top Y", 48, 0, 1000);
            wikiListY1Offset = builder.comment("The offset from the bottom of the screen for the wiki list. Default 70.")
                    .defineInRange("Wiki List Bottom Offset", 70, 0, 1000);
            wikiListItemHeight = builder.comment("The height of each entry in the wiki list. Default 36.")
                    .defineInRange("Wiki List Item Height", 36, 10, 200);
            builder.pop();

            blacklist = builder.comment("Blacklist uses entity IDs, not their display names. Use F3 to see them in the Neat bar.")
                    .defineList("Blacklist", NeatConfig.DEFAULT_DISABLED, a -> true);

            builder.pop();
        }

        private <T> T get(ConfigValue<T> value, T fallback) {
            try {
                return value.get();
            } catch (IllegalStateException e) {
                return fallback;
            }
        }

        @Override
        public int maxDistance() {
            return get(maxDistance, 12);
        }

        @Override
        public boolean renderInF1() {
            return get(renderInF1, false);
        }

        @Override
        public double heightAbove() {
            return get(heightAbove, 0.6);
        }

        @Override
        public boolean drawBackground() {
            return get(drawBackground, true);
        }

        @Override
        public int backgroundPadding() {
            return get(backgroundPadding, 2);
        }

        @Override
        public int backgroundHeight() {
            return get(backgroundHeight, 6);
        }

        @Override
        public int barHeight() {
            return get(barHeight, 4);
        }

        @Override
        public int plateSize() {
            return get(plateSize, 25);
        }

        @Override
        public int plateSizeBoss() {
            return get(plateSizeBoss, 50);
        }

        @Override
        public boolean showAttributes() {
            return get(showAttributes, true);
        }

        @Override
        public boolean showArmor() {
            return get(showArmor, true);
        }

        @Override
        public boolean groupArmor() {
            return get(groupArmor, true);
        }

        @Override
        public boolean colorByType() {
            return get(colorByType, false);
        }

        @Override
        public int hpTextHeight() {
            return get(hpTextHeight, 14);
        }

        @Override
        public boolean showMaxHP() {
            return get(showMaxHP, true);
        }

        @Override
        public boolean showCurrentHP() {
            return get(showCurrentHP, true);
        }

        @Override
        public boolean showPercentage() {
            return get(showPercentage, true);
        }

        @Override
        public boolean showOnPlayers() {
            return get(showOnPlayers, true);
        }

        @Override
        public boolean showOnBosses() {
            return get(showOnBosses, true);
        }

        @Override
        public boolean showOnlyFocused() {
            return get(showOnlyFocused, false);
        }

        @Override
        public boolean showFullHealth() {
            return get(showFullHealth, true);
        }

        @Override
        public boolean enableDebugInfo() {
            return get(enableDebugInfo, true);
        }

        @Override
        public int backgroundAlpha() {
            return get(backgroundAlpha, 64);
        }

        @Override
        public int barAlpha() {
            return get(barAlpha, 127);
        }

        @Override
        public double debuffIconYOffset() {
            return get(debuffIconYOffset, -9.0);
        }

        @Override
        public double debuffIconXOffset() {
            return get(debuffIconXOffset, 0.0);
        }

        @Override
        public int wikiListY0() {
            return get(wikiListY0, 48);
        }

        @Override
        public int wikiListY1Offset() {
            return get(wikiListY1Offset, 70);
        }

        @Override
        public int wikiListItemHeight() {
            return get(wikiListItemHeight, 36);
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<String> blacklist() {
            // Safe cast from List<? extends String> to List<String>, as String is final
            return (List<String>) get(blacklist, NeatConfig.DEFAULT_DISABLED);
        }
    }
}
