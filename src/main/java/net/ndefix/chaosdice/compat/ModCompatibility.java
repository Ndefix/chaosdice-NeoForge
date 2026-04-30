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

    public static void addRareEffects(List<Runnable> pool, Player player) {
        // start artifacts
        if (isLoaded("artifacts")) {
            List<String> items = List.of(
                    "umbrella", "novelty_drinking_hat", "snorkel", "villager_hat",
                    "cowboy_hat", "angler_hat", "flame_pendant", "panic_necklace",
                    "lucky_scarf", "feral_claws", "digging_claws", "pocket_piston",
                    "flippers", "bunny_hoppers", "running_shoes", "snowshoes",
                    "night_vision_goggles"
            );
            for (String id : items) {
                pool.add(() -> {
                    ItemStack item = getItem("artifacts", id);
                    if (!item.isEmpty()) {
                        player.addItem(item);
                        msg(player, "§9Rare! Something from Artifacts!");
                    }
                });
            }
        }
    }

    // -------------------------------------------------------
    // VERY RARE
    // -------------------------------------------------------

    public static void addVeryRareEffects(List<Runnable> pool, Player player) {
        // start artifacts
        if (isLoaded("artifacts")) {
            List<String> items = List.of(
                    "superstitious_hat", "scarf_of_invisibility", "shock_pendant",
                    "thorn_pendant", "cross_necklace", "antidote_vessel",
                    "cloud_in_a_bottle", "obsidian_skull", "universal_attractor",
                    "warp_drive", "fire_gauntlet", "golden_hook", "power_glove",
                    "vampiric_glove", "aqua_dashers", "steadfast_spikes", "strider_shoes",
                    "crystal_heart", "helium_flamingo", "chorus_totem", "onion_ring",
                    "pickaxe_heater", "withered_bracelet", "charm_of_shrinking",
                    "kitty_slippers", "rooted_boots"
            );
            for (String id : items) {
                pool.add(() -> {
                    ItemStack item = getItem("artifacts", id);
                    if (!item.isEmpty()) {
                        player.addItem(item);
                        msg(player, "§5Very Rare! Something from Artifacts!");
                    }
                });
            }
        }
    }

    // -------------------------------------------------------
    // EPIC
    // -------------------------------------------------------

    public static void addEpicEffects(List<Runnable> pool, Player player) {
        // Start SS
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

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "wraithfang");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! wraithfang from Simply Swords!");
                }

            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "magiscythe");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! magiscythe from Simply Swords!");
                }

            });

            pool.add(() -> {
                ItemStack sword = getItem("simplyswords", "ribboncleaver");
                if (!sword.isEmpty()) {
                    player.addItem(sword);
                    msg(player, "§6Epic! ribboncleaver from Simply Swords!");
                }

            });

        }
        // end SS


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
