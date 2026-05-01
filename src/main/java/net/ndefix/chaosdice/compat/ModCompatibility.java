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

            // Gem Kit: 3 random high-purity gems + Sigil of Socketing
            pool.add(() -> {
                String[] gemTypes = {
                        "core/solar", "core/lunar", "core/brawler", "core/breach",
                        "core/combatant", "core/guardian", "core/lightning",
                        "core/samurai", "core/slipstream", "core/splendor"
                };

                List<String> shuffled = new ArrayList<>(List.of(gemTypes));
                Collections.shuffle(shuffled);

                for (int i = 0; i < 3; i++) {
                    ItemStack gem = getApothGem(shuffled.get(i), "flawless");
                    if (!gem.isEmpty()) player.addItem(gem);
                }

                ItemStack sigil = getItem("apotheosis", "sigil_of_socketing");
                if (!sigil.isEmpty()) player.addItem(sigil);

                msg(player, "§6Epic! Three flawless gems and a Sigil of Socketing!");
            });

            // Perfect Gem
            pool.add(() -> {
                String[] gemTypes = {
                        "core/solar", "core/lunar", "core/brawler", "core/breach",
                        "core/combatant", "core/guardian", "core/lightning",
                        "core/samurai", "core/slipstream", "core/splendor"
                };
                String chosen = gemTypes[(int) (Math.random() * gemTypes.length)];
                ItemStack gem = getApothGem(chosen, "perfect");
                if (!gem.isEmpty()) {
                    player.addItem(gem);
                    msg(player, "§6Epic! A perfect gem emerges from the chaos!");
                }
            });

            // Reforging Bundle
            pool.add(() -> {
                ItemStack rebirth = getItem("apotheosis", "sigil_of_rebirth");
                ItemStack enhance = getItem("apotheosis", "sigil_of_enhancement");

                if (!rebirth.isEmpty()) {
                    rebirth.setCount(3);
                    player.addItem(rebirth);
                }
                if (!enhance.isEmpty()) {
                    enhance.setCount(2);
                    player.addItem(enhance);
                }

                msg(player, "§6Epic! Reforging supplies from the void!");
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

            // Full Apotheosis Legendary Haul
            pool.add(() -> {
                String[] allGemTypes = {
                        "core/solar", "core/lunar", "core/brawler", "core/breach",
                        "core/combatant", "core/guardian", "core/lightning",
                        "core/samurai", "core/slipstream", "core/splendor"
                };

                int given = 0;
                for (String type : allGemTypes) {
                    ItemStack gem = getApothGem(type, "perfect");
                    if (!gem.isEmpty()) {
                        player.addItem(gem);
                        given++;
                    }
                }

                ItemStack withdrawal = getItem("apotheosis", "sigil_of_withdrawal");
                ItemStack malice = getItem("apotheosis", "sigil_of_malice");
                ItemStack enhance = getItem("apotheosis", "sigil_of_enhancement");

                if (!withdrawal.isEmpty()) player.addItem(withdrawal);
                if (!malice.isEmpty()) player.addItem(malice);
                if (!enhance.isEmpty()) {
                    enhance.setCount(5);
                    player.addItem(enhance);
                }

                if (given > 0) {
                    msg(player, "§e§lLEGENDARY! Every perfect gem rains from the chaos! (" + given + " gems)");
                }
            });

            // Sigil Storm
            pool.add(() -> {
                List<String> sigils = List.of(
                        "sigil_of_socketing", "sigil_of_rebirth", "sigil_of_withdrawal",
                        "sigil_of_enhancement", "sigil_of_malice", "sigil_of_unnaming"
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

    private static ItemStack getApothGem(String gemPath, String purity) {
        ItemStack base = getItem("apotheosis", "gem");
        if (base.isEmpty()) return ItemStack.EMPTY;

        net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
        tag.putString("apotheosis:gem", "apotheosis:" + gemPath);
        tag.putString("apotheosis:purity", purity);

        base.applyComponents(net.minecraft.core.component.DataComponentPatch.EMPTY);

        return base;
    }
}