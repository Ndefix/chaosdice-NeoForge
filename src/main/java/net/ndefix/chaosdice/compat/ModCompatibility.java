package net.ndefix.chaosdice.compat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ModCompatibility {

    private static final Random RAND = new Random();

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


        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: IRON'S SPELLS 'N SPELLBOOKS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("irons_spellbooks")) {

            // Entry-level spellbooks
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "copper_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§9Rare! A copper spellbook — 5 slots of arcane potential!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "iron_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§9Rare! An iron spellbook materializes!");
                }
            });

            // Ink bundles — used at the arcane anvil to upgrade scrolls
            pool.add(() -> {
                ItemStack ink = getItem("irons_spellbooks", "common_ink");
                if (!ink.isEmpty()) {
                    ink.setCount(8);
                    player.addItem(ink);
                    msg(player, "§9Rare! A bundle of common ink!");
                }
            });
            pool.add(() -> {
                ItemStack ink = getItem("irons_spellbooks", "uncommon_ink");
                if (!ink.isEmpty()) {
                    ink.setCount(4);
                    player.addItem(ink);
                    msg(player, "§9Rare! Uncommon ink from the arcane realm!");
                }
            });

            // Generic upgrade orb
            pool.add(() -> {
                ItemStack orb = getItem("irons_spellbooks", "upgrade_orb");
                if (!orb.isEmpty()) {
                    orb.setCount(2);
                    player.addItem(orb);
                    msg(player, "§9Rare! Spell upgrade orbs!");
                }
            });

            // Curio rings
            pool.add(() -> {
                ItemStack ring = getItem("irons_spellbooks", "mana_ring");
                if (!ring.isEmpty()) {
                    player.addItem(ring);
                    msg(player, "§9Rare! A mana ring — your capacity grows!");
                }
            });
            pool.add(() -> {
                ItemStack ring = getItem("irons_spellbooks", "silver_ring");
                if (!ring.isEmpty()) {
                    player.addItem(ring);
                    msg(player, "§9Rare! A silver ring appears!");
                }
            });
            pool.add(() -> {
                ItemStack ring = getItem("irons_spellbooks", "cooldown_ring");
                if (!ring.isEmpty()) {
                    player.addItem(ring);
                    msg(player, "§9Rare! A cooldown ring — spells flow faster!");
                }
            });

            // Consumables
            pool.add(() -> {
                ItemStack elixir = getItem("irons_spellbooks", "oakskin_elixir");
                if (!elixir.isEmpty()) {
                    elixir.setCount(3);
                    player.addItem(elixir);
                    msg(player, "§9Rare! Oakskin elixirs — tough as bark!");
                }
            });

            // Arcane essence — core crafting resource
            pool.add(() -> {
                ItemStack essence = getItem("irons_spellbooks", "arcane_essence");
                if (!essence.isEmpty()) {
                    essence.setCount(16);
                    player.addItem(essence);
                    msg(player, "§9Rare! A bundle of arcane essence!");
                }
            });

            // Wandering magician armor — the starter mage set
            pool.add(() -> {
                List<String> armor = List.of(
                        "wandering_magician_helmet", "wandering_magician_chestplate",
                        "wandering_magician_leggings", "wandering_magician_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§9Rare! A wandering magician's armor set!");
            });
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: IRON'S SPELLS 'N SPELLBOOKS
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


        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: IRON'S SPELLS 'N SPELLBOOKS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("irons_spellbooks")) {

            // Mid-tier school-specific spellbooks — each its own pool entry
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "gold_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§5Very Rare! A gold spellbook — cast time reduced!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "blaze_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§5Very Rare! A blaze spellbook — fire spells empowered!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "ice_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§5Very Rare! An ice spellbook — frost magic surges!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "druidic_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§5Very Rare! A druidic spellbook — nature bends to your will!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "villager_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§5Very Rare! A villager spellbook — holy and swift!");
                }
            });

            // Rare ink bundle
            pool.add(() -> {
                ItemStack ink = getItem("irons_spellbooks", "rare_ink");
                if (!ink.isEmpty()) {
                    ink.setCount(4);
                    player.addItem(ink);
                    msg(player, "§5Very Rare! Rare ink for powerful scroll upgrades!");
                }
            });

            // School upgrade orbs — each its own entry so the picker can land on any one
            List<String> schoolOrbs = List.of(
                    "fire_upgrade_orb", "ice_upgrade_orb", "lightning_upgrade_orb",
                    "holy_upgrade_orb", "blood_upgrade_orb", "nature_upgrade_orb",
                    "ender_upgrade_orb", "evocation_upgrade_orb"
            );
            for (String orbId : schoolOrbs) {
                pool.add(() -> {
                    ItemStack orb = getItem("irons_spellbooks", orbId);
                    if (!orb.isEmpty()) {
                        orb.setCount(2);
                        player.addItem(orb);
                        msg(player, "§5Very Rare! A pair of " + orbId.replace("_", " ") + "s!");
                    }
                });
            }

            // Entry-level staves
            pool.add(() -> {
                ItemStack staff = getItem("irons_spellbooks", "graybeard_staff");
                if (!staff.isEmpty()) {
                    player.addItem(staff);
                    msg(player, "§5Very Rare! The graybeard's staff appears!");
                }
            });
            pool.add(() -> {
                ItemStack staff = getItem("irons_spellbooks", "lightning_rod");
                if (!staff.isEmpty()) {
                    player.addItem(staff);
                    msg(player, "§5Very Rare! A lightning rod staff crackles into existence!");
                }
            });
            pool.add(() -> {
                ItemStack staff = getItem("irons_spellbooks", "blood_staff");
                if (!staff.isEmpty()) {
                    player.addItem(staff);
                    msg(player, "§5Very Rare! A blood staff drips from the void!");
                }
            });

            // Elixir bundle
            pool.add(() -> {
                ItemStack heal = getItem("irons_spellbooks", "greater_healing_potion");
                ItemStack oak  = getItem("irons_spellbooks", "greater_oakskin_elixir");
                if (!heal.isEmpty()) { heal.setCount(3); player.addItem(heal); }
                if (!oak.isEmpty())  { oak.setCount(2);  player.addItem(oak); }
                if (!heal.isEmpty() || !oak.isEmpty()) {
                    msg(player, "§5Very Rare! Greater healing potions and oakskin elixirs!");
                }
            });

            // Curio necklaces
            pool.add(() -> {
                ItemStack necklace = getItem("irons_spellbooks", "heavy_chain_necklace");
                if (!necklace.isEmpty()) {
                    player.addItem(necklace);
                    msg(player, "§5Very Rare! A heavy chain necklace — spell resist surges!");
                }
            });
            pool.add(() -> {
                ItemStack charm = getItem("irons_spellbooks", "amethyst_resonance_charm");
                if (!charm.isEmpty()) {
                    player.addItem(charm);
                    msg(player, "§5Very Rare! An amethyst resonance charm — mana regenerates faster!");
                }
            });

            // School-themed armor sets
            pool.add(() -> {
                List<String> armor = List.of(
                        "pyromancer_helmet", "pyromancer_chestplate",
                        "pyromancer_leggings", "pyromancer_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§5Very Rare! Pyromancer armor — born of fire!");
            });
            pool.add(() -> {
                List<String> armor = List.of(
                        "cryomancer_helmet", "cryomancer_chestplate",
                        "cryomancer_leggings", "cryomancer_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§5Very Rare! Cryomancer armor — chilled to perfection!");
            });
            pool.add(() -> {
                List<String> armor = List.of(
                        "electromancer_helmet", "electromancer_chestplate",
                        "electromancer_leggings", "electromancer_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§5Very Rare! Electromancer armor — sparking with power!");
            });
            pool.add(() -> {
                List<String> armor = List.of(
                        "priest_helmet", "priest_chestplate",
                        "priest_leggings", "priest_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§5Very Rare! Priest armor — blessed by the light!");
            });
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: IRON'S SPELLS 'N SPELLBOOKS
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

            pool.add(() -> {
                ItemStack rebirth   = getItem("apotheosis", "sigil_of_rebirth");
                ItemStack enhance   = getItem("apotheosis", "sigil_of_enhancement");
                ItemStack socketing = getItem("apotheosis", "sigil_of_socketing");
                if (!rebirth.isEmpty())   { rebirth.setCount(3);   player.addItem(rebirth); }
                if (!enhance.isEmpty())   { enhance.setCount(2);   player.addItem(enhance); }
                if (!socketing.isEmpty()) { player.addItem(socketing); }
                msg(player, "§6Epic! Apotheosis reforging supplies from the void!");
            });

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


        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: IRON'S SPELLS 'N SPELLBOOKS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("irons_spellbooks")) {

            // High-tier spellbooks
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "diamond_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§6Epic! A diamond spellbook — 10 slots, max mana bonus!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "dragonskin_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§6Epic! A dragonskin spellbook — ender magic empowered!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "evoker_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§6Epic! An evoker spellbook — pre-loaded with evocation spells!");
                }
            });

            // Epic ink
            pool.add(() -> {
                ItemStack ink = getItem("irons_spellbooks", "epic_ink");
                if (!ink.isEmpty()) {
                    ink.setCount(4);
                    player.addItem(ink);
                    msg(player, "§6Epic! Epic ink — push your scrolls to the limit!");
                }
            });

            // Upgrade orb haul — generic + mana + cooldown
            pool.add(() -> {
                ItemStack orb     = getItem("irons_spellbooks", "upgrade_orb");
                ItemStack manaOrb = getItem("irons_spellbooks", "mana_upgrade_orb");
                ItemStack cdOrb   = getItem("irons_spellbooks", "cooldown_upgrade_orb");
                if (!orb.isEmpty())     { orb.setCount(4);     player.addItem(orb); }
                if (!manaOrb.isEmpty()) { manaOrb.setCount(2); player.addItem(manaOrb); }
                if (!cdOrb.isEmpty())   { cdOrb.setCount(2);   player.addItem(cdOrb); }
                if (!orb.isEmpty() || !manaOrb.isEmpty()) {
                    msg(player, "§6Epic! An arcane upgrade orb haul!");
                }
            });

            // Rare weapons — each its own entry
            pool.add(() -> {
                ItemStack weapon = getItem("irons_spellbooks", "amethyst_rapier");
                if (!weapon.isEmpty()) {
                    player.addItem(weapon);
                    msg(player, "§6Epic! The amethyst rapier gleams with echoing strikes!");
                }
            });
            pool.add(() -> {
                ItemStack weapon = getItem("irons_spellbooks", "boreal_blade");
                if (!weapon.isEmpty()) {
                    player.addItem(weapon);
                    msg(player, "§6Epic! The boreal blade — a greatsword of frost!");
                }
            });
            pool.add(() -> {
                ItemStack weapon = getItem("irons_spellbooks", "twilight_gale");
                if (!weapon.isEmpty()) {
                    player.addItem(weapon);
                    msg(player, "§6Epic! The twilight gale crackles with volt strikes!");
                }
            });
            pool.add(() -> {
                ItemStack weapon = getItem("irons_spellbooks", "ice_staff");
                if (!weapon.isEmpty()) {
                    player.addItem(weapon);
                    msg(player, "§6Epic! An ice staff from the frozen north!");
                }
            });

            // Rare armor sets
            pool.add(() -> {
                List<String> armor = List.of(
                        "shadowwalker_helmet", "shadowwalker_chestplate",
                        "shadowwalker_leggings", "shadowwalker_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§6Epic! Shadowwalker armor — vanish into the dark!");
            });
            pool.add(() -> {
                List<String> armor = List.of(
                        "archevoker_helmet", "archevoker_chestplate",
                        "archevoker_leggings", "archevoker_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§6Epic! Archevoker armor — evocation power unleashed!");
            });
            pool.add(() -> {
                List<String> armor = List.of(
                        "plagued_helmet", "plagued_chestplate",
                        "plagued_leggings", "plagued_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) msg(player, "§6Epic! Plagued armor — nature's wrath incarnate!");
            });

            // Elixir bundle
            pool.add(() -> {
                ItemStack heal  = getItem("irons_spellbooks", "greater_healing_potion");
                ItemStack oak   = getItem("irons_spellbooks", "greater_oakskin_elixir");
                ItemStack invis = getItem("irons_spellbooks", "greater_invisibility_elixir");
                ItemStack evas  = getItem("irons_spellbooks", "evasion_elixir");
                if (!heal.isEmpty())  { heal.setCount(4);  player.addItem(heal); }
                if (!oak.isEmpty())   { oak.setCount(4);   player.addItem(oak); }
                if (!invis.isEmpty()) { invis.setCount(2); player.addItem(invis); }
                if (!evas.isEmpty())  { evas.setCount(2);  player.addItem(evas); }
                if (!heal.isEmpty() || !oak.isEmpty()) {
                    msg(player, "§6Epic! An elixir bundle from the alchemist's cauldron!");
                }
            });

            // Mithril ingots — crafting material for weapons and armor
            pool.add(() -> {
                ItemStack mithril = getItem("irons_spellbooks", "mithril_ingot");
                if (!mithril.isEmpty()) {
                    mithril.setCount(8);
                    player.addItem(mithril);
                    msg(player, "§6Epic! Mithril ingots rain from the chaos!");
                }
            });

            // Teleportation amulet curio
            pool.add(() -> {
                ItemStack amulet = getItem("irons_spellbooks", "teleportation_amulet");
                if (!amulet.isEmpty()) {
                    player.addItem(amulet);
                    msg(player, "§6Epic! A teleportation amulet — blink at will!");
                }
            });
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: IRON'S SPELLS 'N SPELLBOOKS
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

            pool.add(() -> {
                List<String> sigils = List.of(
                        "sigil_of_socketing", "sigil_of_rebirth", "sigil_of_withdrawal",
                        "sigil_of_enhancement", "sigil_of_malice", "sigil_of_unnaming"
                );
                int given = 0;
                for (String id : sigils) {
                    ItemStack s = getItem("apotheosis", id);
                    if (!s.isEmpty()) { s.setCount(2); player.addItem(s); given++; }
                }
                if (given > 0) msg(player, "§e§lLEGENDARY! A storm of sigils! The forge is yours!");
            });

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


        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        // MOD START: IRON'S SPELLS 'N SPELLBOOKS
        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if (isLoaded("irons_spellbooks")) {

            // The ultimate spellbooks — each its own legendary entry
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "netherite_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§e§lLEGENDARY! A netherite spellbook — 12 slots, cooldown reduction, maximum mana!");
                }
            });
            pool.add(() -> {
                ItemStack book = getItem("irons_spellbooks", "legendary_spell_book");
                if (!book.isEmpty()) {
                    player.addItem(book);
                    msg(player, "§e§lLEGENDARY! The legendary spellbook — 12 slots of pure chaos!");
                }
            });

            // Full netherite mage armor — the best armor set in the mod, fireproof
            pool.add(() -> {
                List<String> armor = List.of(
                        "netherite_mage_helmet", "netherite_mage_chestplate",
                        "netherite_mage_leggings", "netherite_mage_boots"
                );
                int given = 0;
                for (String id : armor) {
                    ItemStack piece = getItem("irons_spellbooks", id);
                    if (!piece.isEmpty()) { player.addItem(piece); given++; }
                }
                if (given > 0) {
                    msg(player, "§e§lLEGENDARY! Full netherite mage armor — fireproof and unstoppable!");
                }
            });

            // Legendary weapons
            pool.add(() -> {
                ItemStack weapon = getItem("irons_spellbooks", "hellrazor");
                if (!weapon.isEmpty()) {
                    player.addItem(weapon);
                    msg(player, "§e§lLEGENDARY! Hellrazor — a blade that raises hell on hit!");
                }
            });
            pool.add(() -> {
                ItemStack weapon = getItem("irons_spellbooks", "pyrium_staff");
                if (!weapon.isEmpty()) {
                    player.addItem(weapon);
                    msg(player, "§e§lLEGENDARY! The pyrium staff blazes into existence!");
                }
            });

            // Legendary ink + spell slot upgrades
            pool.add(() -> {
                ItemStack ink  = getItem("irons_spellbooks", "legendary_ink");
                ItemStack slot = getItem("irons_spellbooks", "lesser_spell_slot_upgrade");
                if (!ink.isEmpty())  { ink.setCount(4);  player.addItem(ink); }
                if (!slot.isEmpty()) { slot.setCount(3); player.addItem(slot); }
                if (!ink.isEmpty() || !slot.isEmpty()) {
                    msg(player, "§e§lLEGENDARY! Legendary ink and spell slot upgrades pour from the void!");
                }
            });

            // Full orb jackpot — one of every school orb (2x each)
            pool.add(() -> {
                List<String> orbs = new ArrayList<>(List.of(
                        "upgrade_orb", "fire_upgrade_orb", "ice_upgrade_orb",
                        "lightning_upgrade_orb", "holy_upgrade_orb", "blood_upgrade_orb",
                        "evocation_upgrade_orb", "nature_upgrade_orb", "ender_upgrade_orb",
                        "mana_upgrade_orb", "cooldown_upgrade_orb", "protection_upgrade_orb"
                ));
                int given = 0;
                for (String id : orbs) {
                    ItemStack orb = getItem("irons_spellbooks", id);
                    if (!orb.isEmpty()) { orb.setCount(2); player.addItem(orb); given++; }
                }
                if (given > 0) {
                    msg(player, "§e§lLEGENDARY! Every upgrade orb type rains from the arcane void!");
                }
            });
        }
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        // MOD END: IRON'S SPELLS 'N SPELLBOOKS
        // <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    }

    // -------------------------------------------------------
    // NEGATIVE
    // -------------------------------------------------------

    public static void addNegativeEffects(List<Runnable> pool, Player player) {}
}