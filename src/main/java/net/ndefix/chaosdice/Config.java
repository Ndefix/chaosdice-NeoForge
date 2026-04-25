package net.ndefix.chaosdice;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final ModConfigSpec SPEC;
    public static final Config INSTANCE;

    static {
        Pair<Config, ModConfigSpec> pair = new ModConfigSpec.Builder().configure(Config::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    // Definicje wag dla każdej kategorii
    public final ModConfigSpec.IntValue commonWeight;
    public final ModConfigSpec.IntValue rareWeight;
    public final ModConfigSpec.IntValue veryRareWeight;
    public final ModConfigSpec.IntValue epicWeight;
    public final ModConfigSpec.IntValue legendaryWeight;
    public final ModConfigSpec.IntValue negativeWeight;

    public Config(ModConfigSpec.Builder builder) {
        builder.push("Drop Chances");
        builder.comment("Weights for the Chaos Dice outcomes. Higher weight = higher chance.");

        commonWeight = builder.defineInRange("commonWeight", 40, 0, 1000);
        rareWeight = builder.defineInRange("rareWeight", 15, 0, 1000);
        veryRareWeight = builder.defineInRange("veryRareWeight", 10, 0, 1000);
        epicWeight = builder.defineInRange("epicWeight", 5, 0, 1000);
        legendaryWeight = builder.defineInRange("legendaryWeight", 1, 0, 1000);
        negativeWeight = builder.defineInRange("negativeWeight", 29, 0, 1000);

        builder.pop();
    }
}