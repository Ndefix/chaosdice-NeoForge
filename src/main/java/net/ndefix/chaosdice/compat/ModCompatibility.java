package net.ndefix.chaosdice.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.List;

public class ModCompatibility {
    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------

    private static boolean isLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    private static ItemStack getItem(String modId, String itemId) {
        return BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.fromNamespaceAndPath(modId, itemId))
                .map(ItemStack::new)
                .orElse(ItemStack.EMPTY);
    }

    private static void msg(Player player, String text) {
        player.sendSystemMessage(Component.literal(text));
    }

    // -------------------------------------------------------
    // COMMON
    // -------------------------------------------------------

    public static void addCommonEffects(List<Runnable> pool, Player player) {}

    // -------------------------------------------------------
    // RARE
    // -------------------------------------------------------

    public static void addRareEffects(List<Runnable> pool, Player player) {}

    // -------------------------------------------------------
    // VERY RARE
    // -------------------------------------------------------

    public static void addVeryRareEffects(List<Runnable> pool, Player player) {}

    // -------------------------------------------------------
    // EPIC
    // -------------------------------------------------------

    public static void addEpicEffects(List<Runnable> pool, Player player) {
        if (isLoaded("simplyswords")) {
            //pool.clear();  temporarily force only SS items
            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "whisperwind");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! A whisperwind from Simply Swords!");
                }
            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "caelestis");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! A caelestis from Simply Swords!");
                }
            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "shadowsting");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! A shadowsting from Simply Swords!");
                }
            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "brimstone_claymore");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! A brimstone claymore from Simply Swords!");
                }
            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "watching_warglaive");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! A watching warglaive from Simply Swords!");
                }
            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "soulrender");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! A soulrender from Simply Swords!");
                }

            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "enigma");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! An enigma from Simply Swords!");
                }

            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "arcanethyst");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! An arcanethyst from Simply Swords!");
                }

            });


            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "stars_edge");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! star's edge from Simply Swords!");
                }

            });

        }
    }

    // -------------------------------------------------------
    // LEGENDARY
    // -------------------------------------------------------

    public static void addLegendaryEffects(List<Runnable> pool, Player player) {}

    // -------------------------------------------------------
    // NEGATIVE
    // -------------------------------------------------------

    public static void addNegativeEffects(List<Runnable> pool, Player player) {}

}
