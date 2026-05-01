package net.ndefix.chaosdice.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.Collections;
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

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: ARTIFACTS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: ARTIFACTS
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    // -------------------------------------------------------
    // VERY RARE
    // -------------------------------------------------------

    public static void addVeryRareEffects(List<Runnable> pool, Player player) {

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: ARTIFACTS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
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
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: ARTIFACTS
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    // -------------------------------------------------------
    // EPIC
    // -------------------------------------------------------

    public static void addEpicEffects(List<Runnable> pool, Player player) {

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: APOTHEOSIS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("apotheosis")) {

            // Reforging Bundle — all plain items, no special components needed
            pool.add(() -> {
                ItemStack rebirth = getItem("apotheosis", "sigil_of_rebirth");
                ItemStack enhance = getItem("apotheosis", "sigil_of_enhancement");
                ItemStack socketing = getItem("apotheosis", "sigil_of_socketing");

                if (!rebirth.isEmpty()) { rebirth.setCount(3); player.addItem(rebirth); }
                if (!enhance.isEmpty()) { enhance.setCount(2); player.addItem(enhance); }
                if (!socketing.isEmpty()) { player.addItem(socketing); }

                msg(player, "§6Epic! Apotheosis reforging supplies from the void!");
            });

            // Gem Dust bundle — plain item, safe to give
            pool.add(() -> {
                ItemStack dust = getItem("apotheosis", "gem_dust");
                if (!dust.isEmpty()) {
                    dust.setCount(16);
                    player.addItem(dust);
                    msg(player, "§6Epic! A pile of gem dust!");
                }
            });

        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: APOTHEOSIS
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<


        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: SIMPLY SWORDS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("simplyswords")) {

            String[] swords = {
                    "whisperwind", "caelestis", "shadowsting", "brimstone_claymore",
                    "watching_warglaive", "soulrender", "enigma", "arcanethyst",
                    "stars_edge", "wraithfang", "magiscythe", "ribboncleaver"
            };

            for (String swordId : swords) {
                pool.add(() -> {
                    ItemStack sword = getItem("simplyswords", swordId);
                    if (!sword.isEmpty()) {
                        player.addItem(sword);
                        msg(player, "§6Epic! A " + swordId.replace("_", " ") + " from Simply Swords!");
                    }
                });
            }
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: SIMPLY SWORDS
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    // -------------------------------------------------------
    // LEGENDARY
    // -------------------------------------------------------

    public static void addLegendaryEffects(List<Runnable> pool, Player player) {

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: APOTHEOSIS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("apotheosis")) {

            // Sigil Storm — all plain items, zero component manipulation
            pool.add(() -> {
                List<String> sigils = List.of(
                        "sigil_of_socketing",
                        "sigil_of_rebirth",
                        "sigil_of_withdrawal",
                        "sigil_of_enhancement",
                        "sigil_of_malice",
                        "sigil_of_unnaming"
                );

                int given = 0;
                for (String id : sigils) {
                    ItemStack s = getItem("apotheosis", id);
                    if (!s.isEmpty()) {
                        s.setCount(2);
                        player.addItem(s);
                        given++;
                    }
                }
                if (given > 0) {
                    msg(player, "§e§lLEGENDARY! A storm of sigils! The forge is yours!");
                }
            });

            // Gem Dust jackpot
            pool.add(() -> {
                ItemStack dust = getItem("apotheosis", "gem_dust");
                if (!dust.isEmpty()) {
                    dust.setCount(64);
                    player.addItem(dust);
                    msg(player, "§e§lLEGENDARY! 64 gem dust rains from the chaos!");
                }
            });
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: APOTHEOSIS
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    // -------------------------------------------------------
    // NEGATIVE
    // -------------------------------------------------------

    public static void addNegativeEffects(List<Runnable> pool, Player player) {}

    // -------------------------------------------------------
    // APOTHEOSIS HELPER
    // -------------------------------------------------------


}