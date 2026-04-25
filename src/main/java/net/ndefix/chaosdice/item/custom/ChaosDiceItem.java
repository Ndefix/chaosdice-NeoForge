package net.ndefix.chaosdice.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.phys.Vec3;
import net.ndefix.chaosdice.Config;
import net.ndefix.chaosdice.compat.ModCompatibility;
import net.ndefix.chaosdice.item.ModItems;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ChaosDiceItem extends Item {

    private static final Random RAND = new Random();

    public ChaosDiceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Check if the item is on cooldown
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(itemStack);
        }

        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            TierResult tier = rollTier();

            Vec3 eyePos = player.getEyePosition();
            Vec3 lookVec = player.getLookAngle();
            Vec3 targetPos = eyePos.add(lookVec.x * 2.0, lookVec.y * 2.0, lookVec.z * 2.0);

            ItemEntity floatingDice = new ItemEntity(level, targetPos.x, targetPos.y, targetPos.z, new ItemStack(this));
            floatingDice.setNoGravity(true);
            floatingDice.setDeltaMovement(0, 0, 0);
            floatingDice.setPickUpDelay(32767);
            floatingDice.setGlowingTag(true);
            level.addFreshEntity(floatingDice);

            // Apply 5 second cooldown (100 ticks) per player
            player.getCooldowns().addCooldown(this, 50);

            spawnRitualSequence(tier, serverLevel, player, floatingDice);
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(
                Component.literal("The chaos within stirs, waiting...")
                        .withStyle(style -> style.withColor(0x8A2BE2).withItalic(true))
        );
        tooltipComponents.add(
                Component.literal("Fate is not yours to command.")
                        .withStyle(style -> style.withColor(0x4B0082).withItalic(true))
        );
        tooltipComponents.add(
                Component.literal("Roll. And accept what comes.")
                        .withStyle(style -> style.withColor(0x2E0854).withItalic(true))
        );
    }

    // -------------------------------------------------------------------------
    // TIERS
    // -------------------------------------------------------------------------

    public enum TierResult {
        COMMON, RARE, VERY_RARE, EPIC, LEGENDARY, NEGATIVE
    }

    private TierResult rollTier() {
        int wCommon = Config.INSTANCE.commonWeight.get();
        int wRare = Config.INSTANCE.rareWeight.get();
        int wVRare = Config.INSTANCE.veryRareWeight.get();
        int wEpic = Config.INSTANCE.epicWeight.get();
        int wLegend = Config.INSTANCE.legendaryWeight.get();
        int wNeg = Config.INSTANCE.negativeWeight.get();

        int totalWeight = wCommon + wRare + wVRare + wEpic + wLegend + wNeg;
        if (totalWeight <= 0) return TierResult.COMMON; // Zabezpieczenie przed 0

        int roll = (int) (Math.random() * totalWeight);
        int cursor = 0;

        if (roll < (cursor += wCommon)) return TierResult.COMMON;
        if (roll < (cursor += wRare))   return TierResult.RARE;
        if (roll < (cursor += wVRare))  return TierResult.VERY_RARE;
        if (roll < (cursor += wEpic))   return TierResult.EPIC;
        if (roll < (cursor += wLegend)) return TierResult.LEGENDARY;

        return TierResult.NEGATIVE;
    }

    // -------------------------------------------------------------------------
    // EFFECT DISPATCH — picks one random effect from the tier's pool
    // -------------------------------------------------------------------------

    private void applyEffect(TierResult tier, Level level, Player player) {
        switch (tier) {
            case COMMON    -> pickRandom(commonEffects(player)).run();
            case RARE      -> pickRandom(rareEffects(level, player)).run();
            case VERY_RARE -> pickRandom(veryRareEffects(level, player)).run();
            case EPIC      -> pickRandom(epicEffects(level, player)).run();
            case LEGENDARY -> pickRandom(legendaryEffects(level, player)).run();
            case NEGATIVE  -> pickRandom(negativeEffects(level, player)).run();
        }
    }

    private Runnable pickRandom(List<Runnable> pool) {
        return pool.get(RAND.nextInt(pool.size()));
    }

    // -------------------------------------------------------------------------
    // COMMON POOL
    // -------------------------------------------------------------------------

    private List<Runnable> commonEffects(Player player) {
        List<Runnable> pool = new ArrayList<>();

        pool.add(() -> {
            player.addItem(new ItemStack(Items.BREAD, 32));
            msg(player, "§aCommon! Fresh bread from the void!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.COOKED_BEEF, 32));
            msg(player, "§aCommon! A feast of steak appears!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ARROW, 16));
            msg(player, "§aCommon! A quiver of arrows!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SPECTRAL_ARROW, 8));
            msg(player, "§aCommon! Spectral arrows, nice!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.STONE_SWORD));
            player.addItem(new ItemStack(Items.STONE_PICKAXE));
            player.addItem(new ItemStack(Items.STONE_AXE));
            msg(player, "§aCommon! A basic set of stone tools!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.IRON_SWORD));
            player.addItem(new ItemStack(Items.IRON_PICKAXE));
            player.addItem(new ItemStack(Items.IRON_AXE));
            msg(player, "§aCommon! Iron tools, reliable!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.LEATHER_HELMET));
            player.addItem(new ItemStack(Items.LEATHER_CHESTPLATE));
            player.addItem(new ItemStack(Items.LEATHER_LEGGINGS));
            player.addItem(new ItemStack(Items.LEATHER_BOOTS));
            msg(player, "§aCommon! A leather armor set!");
        });
        pool.add(() -> {
            int levels = 3 + RAND.nextInt(6);
            player.giveExperienceLevels(levels);
            msg(player, "§aCommon! +" + levels + " XP levels!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
            msg(player, "§aCommon! You feel swift!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 1));
            msg(player, "§aCommon! Feeling bouncy!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 600, 0));
            msg(player, "§aCommon! Your hunger is satisfied!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.TORCH, 64));
            msg(player, "§aCommon! Let there be light!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.LADDER, 32));
            msg(player, "§aCommon! Climb anything!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ENDER_PEARL, 8));
            msg(player, "§aCommon! Ender pearls at your service!");
        });

        // NEW — sticks + string
        pool.add(() -> {
            player.addItem(new ItemStack(Items.STICK, 16));
            player.addItem(new ItemStack(Items.STRING, 8));
            msg(player, "§aCommon! A crafter's starter pack!");
        });

        // NEW — random colored wool x64
        pool.add(() -> {
            Item[] wools = {
                    Items.WHITE_WOOL, Items.ORANGE_WOOL, Items.MAGENTA_WOOL,
                    Items.LIGHT_BLUE_WOOL, Items.YELLOW_WOOL, Items.LIME_WOOL,
                    Items.PINK_WOOL, Items.GRAY_WOOL, Items.CYAN_WOOL,
                    Items.PURPLE_WOOL, Items.BLUE_WOOL, Items.GREEN_WOOL,
                    Items.RED_WOOL, Items.BLACK_WOOL
            };
            Item wool = wools[RAND.nextInt(wools.length)];
            player.addItem(new ItemStack(wool, 64));
            msg(player, "§aCommon! A stack of colorful wool!");
        });

        // NEW — 3x golden carrots
        pool.add(() -> {
            player.addItem(new ItemStack(Items.GOLDEN_CARROT, 3));
            msg(player, "§aCommon! Golden carrots, tasty!");
        });

        // NEW — 8x snowballs
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SNOWBALL, 8));
            msg(player, "§aCommon! Snowball fight!");
        });

        // NEW — fishing rod
        pool.add(() -> {
            player.addItem(new ItemStack(Items.FISHING_ROD));
            msg(player, "§aCommon! Gone fishin'!");
        });

        // NEW — 32x bones
        pool.add(() -> {
            player.addItem(new ItemStack(Items.BONE, 32));
            msg(player, "§aCommon! A pile of bones!");
        });

        // NEW — 16x sugar cane
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SUGAR_CANE, 16));
            msg(player, "§aCommon! Sugar cane materializes!");
        });

        // NEW — Night vision potion 3 min
        pool.add(() -> {
            player.addItem(new ItemStack(Items.POTION));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0));
            msg(player, "§aCommon! Night vision potion!");
        });

        // NEW — 5x books
        pool.add(() -> {
            player.addItem(new ItemStack(Items.BOOK, 5));
            msg(player, "§aCommon! Knowledge... sort of.");
        });

        // NEW — 64x gravel
        pool.add(() -> {
            player.addItem(new ItemStack(Items.GRAVEL, 64));
            msg(player, "§aCommon! The chaos tax has been paid.");
        });

        ModCompatibility.addCommonEffects(pool, player);

        return pool;
    }

    // -------------------------------------------------------------------------
    // RARE POOL
    // -------------------------------------------------------------------------

    private List<Runnable> rareEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        pool.add(() -> {
            int count = 3 + RAND.nextInt(3);
            player.addItem(new ItemStack(Items.EMERALD, count));
            msg(player, "§9Rare! " + count + " emeralds!");
        });
        pool.add(() -> {
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.POWER), 3);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.INFINITY), 1);
            player.addItem(bow);
            msg(player, "§9Rare! An enchanted bow!");
        });
        pool.add(() -> {
            List<ItemStack> armor = List.of(
                    new ItemStack(Items.IRON_HELMET),
                    new ItemStack(Items.IRON_CHESTPLATE),
                    new ItemStack(Items.IRON_LEGGINGS),
                    new ItemStack(Items.IRON_BOOTS)
            );
            armor.forEach(a -> {
                a.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.PROTECTION), 2);
                player.addItem(a);
            });
            msg(player, "§9Rare! Enchanted iron armor!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 1));
            msg(player, "§9Rare! Strength surges through you!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0));
            msg(player, "§9Rare! You can see in the dark!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.OBSIDIAN, 16));
            msg(player, "§9Rare! Obsidian materializes!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.EXPERIENCE_BOTTLE, 16));
            msg(player, "§9Rare! Bottles of experience!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SADDLE));
            player.addItem(new ItemStack(Items.IRON_HORSE_ARMOR));
            msg(player, "§9Rare! Ready to ride!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.TRIDENT));
            msg(player, "§9Rare! A trident from the depths!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.GOLD_INGOT, 32));
            msg(player, "§9Rare! Gold pours forth!");
        });

        // NEW — enchanted fishing rod
        pool.add(() -> {
            ItemStack rod = new ItemStack(Items.FISHING_ROD);
            rod.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.LUCK_OF_THE_SEA), 3);
            rod.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.LURE), 2);
            player.addItem(rod);
            msg(player, "§9Rare! The luckiest fishing rod!");
        });

        // NEW — random music disc
        pool.add(() -> {
            List<Item> discs = List.of(
                    Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS,
                    Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL,
                    Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD,
                    Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT,
                    Items.MUSIC_DISC_OTHERSIDE, Items.MUSIC_DISC_PIGSTEP, Items.MUSIC_DISC_5
            );
            player.addItem(new ItemStack(discs.get(RAND.nextInt(discs.size()))));
            msg(player, "§9Rare! A random music disc!");
        });

        // NEW — full golden armor Fire Prot III
        pool.add(() -> {
            List<ItemStack> armor = List.of(
                    new ItemStack(Items.GOLDEN_HELMET),
                    new ItemStack(Items.GOLDEN_CHESTPLATE),
                    new ItemStack(Items.GOLDEN_LEGGINGS),
                    new ItemStack(Items.GOLDEN_BOOTS)
            );
            armor.forEach(a -> {
                a.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                        .getOrThrow(Enchantments.FIRE_PROTECTION), 3);
                player.addItem(a);
            });
            msg(player, "§9Rare! Golden armor, fireproof!");
        });

        // NEW — slow falling 5 min
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 6000, 0));
            msg(player, "§9Rare! You feel weightless!");
        });

        // NEW — 8x chorus fruit
        pool.add(() -> {
            player.addItem(new ItemStack(Items.CHORUS_FRUIT, 8));
            msg(player, "§9Rare! Chorus fruit from the End!");
        });

        // NEW — crossbow Quick Charge III + Multishot
        pool.add(() -> {
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);
            crossbow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.QUICK_CHARGE), 3);
            crossbow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.MULTISHOT), 1);
            player.addItem(crossbow);
            msg(player, "§9Rare! A rapid-fire crossbow!");
        });

        // NEW — 16x prismarine shards + 8x crystals
        pool.add(() -> {
            player.addItem(new ItemStack(Items.PRISMARINE_SHARD, 16));
            player.addItem(new ItemStack(Items.PRISMARINE_CRYSTALS, 8));
            msg(player, "§9Rare! Treasures from the ocean monument!");
        });

        // NEW — conduit
        pool.add(() -> {
            player.addItem(new ItemStack(Items.CONDUIT));
            msg(player, "§9Rare! A conduit of the deep!");
        });

        // NEW — 8x ghast tears
        pool.add(() -> {
            player.addItem(new ItemStack(Items.GHAST_TEAR, 8));
            msg(player, "§9Rare! Ghast tears, still warm.");
        });

        ModCompatibility.addRareEffects(pool, player);

        return pool;
    }

    // -------------------------------------------------------------------------
    // VERY RARE POOL
    // -------------------------------------------------------------------------

    private List<Runnable> veryRareEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        pool.add(() -> {
            ItemStack elytra = new ItemStack(Items.ELYTRA);
            elytra.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.UNBREAKING), 3);
            player.addItem(elytra);
            msg(player, "§5Very Rare! Wings of chaos!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
            msg(player, "§5Very Rare! A golden apple of legends!");
        });
        pool.add(() -> {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            book.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.MENDING), 1);
            book.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.UNBREAKING), 3);
            player.addItem(book);
            msg(player, "§5Very Rare! Mending + Unbreaking III book!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.BEACON));
            msg(player, "§5Very Rare! A beacon of power!");
        });

        // NEW — heart of the sea
        pool.add(() -> {
            player.addItem(new ItemStack(Items.HEART_OF_THE_SEA));
            msg(player, "§5Very Rare! The heart of the sea!");
        });

        // NEW — dragon egg
        pool.add(() -> {
            player.addItem(new ItemStack(Items.DRAGON_EGG));
            msg(player, "§5Very Rare! A dragon egg... handle with care.");
        });

        // NEW — 16x sponge
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SPONGE, 16));
            msg(player, "§5Very Rare! Sponges from the deep!");
        });

        // NEW — 4x shulker shells
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SHULKER_SHELL, 4));
            msg(player, "§5Very Rare! Shulker shells appear!");
        });

        // NEW — 4x netherite scrap
        pool.add(() -> {
            player.addItem(new ItemStack(Items.NETHERITE_SCRAP, 4));
            msg(player, "§5Very Rare! Netherite scrap from the void!");
        });

        // NEW — 32x echo shards
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ECHO_SHARD, 32));
            msg(player, "§5Very Rare! Echoes of the ancient city!");
        });

        // NEW — netherite upgrade smithing template
        pool.add(() -> {
            player.addItem(new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
            msg(player, "§5Very Rare! A netherite upgrade template!");
        });

        // NEW — all positive effects 60s
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0));
            msg(player, "§5Very Rare! Blessed with all powers!");
        });

        ModCompatibility.addVeryRareEffects(pool, player);
        return pool;
    }

    // -------------------------------------------------------------------------
    // EPIC POOL
    // -------------------------------------------------------------------------

    private List<Runnable> epicEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        pool.add(() -> {
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.POWER), 5);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.FLAME), 1);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.INFINITY), 1);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.UNBREAKING), 3);
            player.addItem(bow);
            msg(player, "§6Epic! The ultimate bow!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 2));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 3600, 2));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 3600, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 2));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 3600, 0));
            player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 3600, 4));
            msg(player, "§6Epic! Godlike powers for 3 minutes!");
        });
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 5));
            msg(player, "§6Epic! Five enchanted golden apples!");
        });

        // Loot room
        pool.add(() -> {
            BlockPos center = player.blockPosition().below();
            List<ItemStack> loot = List.of(
                    new ItemStack(Items.DIAMOND, 4),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1),
                    new ItemStack(Items.TOTEM_OF_UNDYING, 1)
            );
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    level.setBlock(center.offset(x, 0, z),
                            Blocks.OBSIDIAN.defaultBlockState(), 3);
                }
            }
            List<BlockPos> chestPositions = List.of(
                    center.offset(-1, 1, -1), center.offset(1, 1, -1),
                    center.offset(-1, 1, 1),  center.offset(1, 1, 1)
            );
            for (BlockPos chestPos : chestPositions) {
                level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
                if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                    for (int i = 0; i < loot.size(); i++) {
                        chest.setItem(i, loot.get(i).copy());
                    }
                }
            }
            msg(player, "§6Epic! A loot room has appeared around you!");
        });

        // NEW — XP Overload 20–40 levels
        pool.add(() -> {
            int levels = 20 + RAND.nextInt(21);
            player.giveExperienceLevels(levels);
            msg(player, "§6Epic! +" + levels + " XP levels!");
        });

        // NEW — Chaos Fishing: 10 random fish/treasure items
        pool.add(() -> {
            List<ItemStack> fishLoot = new ArrayList<>(List.of(
                    new ItemStack(Items.COD, 3),
                    new ItemStack(Items.SALMON, 3),
                    new ItemStack(Items.TROPICAL_FISH, 2),
                    new ItemStack(Items.PUFFERFISH, 1),
                    new ItemStack(Items.NAUTILUS_SHELL, 2),
                    new ItemStack(Items.SADDLE),
                    new ItemStack(Items.NAME_TAG),
                    new ItemStack(Items.BOW),
                    new ItemStack(Items.ENCHANTED_BOOK),
                    new ItemStack(Items.LILY_PAD, 4)
            ));
            if (!(level instanceof ServerLevel serverLevel)) return;
            int currentTick = serverLevel.getServer().getTickCount();
            for (int i = 0; i < fishLoot.size(); i++) {
                final ItemStack drop = fishLoot.get(i);
                final int delay = i * 4;
                serverLevel.getServer().tell(new TickTask(currentTick + delay, () -> {
                    double ox = (RAND.nextDouble() - 0.5) * 3;
                    double oz = (RAND.nextDouble() - 0.5) * 3;
                    ItemEntity entity = new ItemEntity(serverLevel,
                            player.getX() + ox, player.getY() + 0.5, player.getZ() + oz, drop);
                    entity.setDeltaMovement(
                            (RAND.nextDouble() - 0.5) * 0.2, 0.3, (RAND.nextDouble() - 0.5) * 0.2);
                    serverLevel.addFreshEntity(entity);
                }));
            }
            msg(player, "§6Epic! Chaos fishing — 10 catches at once!");
        });

        // NEW — Mob Grinder: 20 passive mobs spawned dead with drops
        pool.add(() -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            List<EntityType<?>> passiveMobs = List.of(
                    EntityType.COW, EntityType.PIG, EntityType.SHEEP,
                    EntityType.CHICKEN, EntityType.RABBIT
            );
            int currentTick = serverLevel.getServer().getTickCount();
            for (int i = 0; i < 20; i++) {
                final int fi = i;
                serverLevel.getServer().tell(new TickTask(currentTick + fi * 2, () -> {
                    EntityType<?> type = passiveMobs.get(RAND.nextInt(passiveMobs.size()));
                    double ox = (RAND.nextDouble() - 0.5) * 10;
                    double oz = (RAND.nextDouble() - 0.5) * 10;
                    var mob = type.create(serverLevel);
                    if (mob != null) {
                        mob.moveTo(player.getX() + ox, player.getY(), player.getZ() + oz, 0, 0);
                        serverLevel.addFreshEntity(mob);
                        mob.kill();
                    }
                }));
            }
            msg(player, "§6Epic! The mob grinder activates!");
        });

        // NEW — Instant Mineshaft: shaft down with ladders and chest
        pool.add(() -> {
            BlockPos start = player.blockPosition();
            // Dig 5 blocks down with ladders
            for (int i = 1; i <= 5; i++) {
                BlockPos shaft = start.below(i);
                level.setBlock(shaft, Blocks.AIR.defaultBlockState(), 3);
                // Place ladder on north wall of shaft
                level.setBlock(shaft.north(),
                        Blocks.LADDER.defaultBlockState()
                                .setValue(net.minecraft.world.level.block.LadderBlock.FACING,
                                        net.minecraft.core.Direction.SOUTH), 3);
            }
            // Chest at bottom with loot
            BlockPos chestPos = start.below(5).below();
            level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);
            if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest) {
                chest.setItem(0, new ItemStack(Items.IRON_PICKAXE));
                chest.setItem(1, new ItemStack(Items.TORCH, 16));
                chest.setItem(2, new ItemStack(Items.BREAD, 8));
                chest.setItem(3, new ItemStack(Items.IRON_INGOT, 8));
                chest.setItem(4, new ItemStack(Items.GOLD_INGOT, 4));
                chest.setItem(5, new ItemStack(Items.DIAMOND, 1 + RAND.nextInt(3)));
            }
            msg(player, "§6Epic! A mineshaft opens beneath you!");
        });

        // NEW — Chaos Garden: 5x5 fully grown crops
        pool.add(() -> {
            BlockPos base = player.blockPosition().below();
            List<net.minecraft.world.level.block.state.BlockState> crops = List.of(
                    Blocks.WHEAT.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.CropBlock.AGE, 7),
                    Blocks.CARROTS.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.CropBlock.AGE, 7),
                    Blocks.POTATOES.defaultBlockState()
                            .setValue(net.minecraft.world.level.block.CropBlock.AGE, 7)
            );
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    BlockPos soil = base.offset(x, 0, z);
                    BlockPos crop = soil.above();
                    level.setBlock(soil, Blocks.FARMLAND.defaultBlockState(), 3);
                    level.setBlock(crop, crops.get(RAND.nextInt(crops.size())), 3);
                }
            }
            msg(player, "§6Epic! A chaos garden blooms around you!");
        });

        // NEW — max enchant crossbow
        pool.add(() -> {
            ItemStack crossbow = new ItemStack(Items.CROSSBOW);
            crossbow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.QUICK_CHARGE), 3);
            crossbow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.MULTISHOT), 1);
            crossbow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.PIERCING), 4);
            crossbow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.UNBREAKING), 3);
            player.addItem(crossbow);
            msg(player, "§6Epic! The ultimate crossbow!");
        });

        ModCompatibility.addEpicEffects(pool, player);
        return pool;
    }

    // -------------------------------------------------------------------------
    // LEGENDARY POOL
    // -------------------------------------------------------------------------

    private List<Runnable> legendaryEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        // Treasure rain
        pool.add(() -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            List<ItemStack> treasures = new ArrayList<>(List.of(
                    new ItemStack(Items.DIAMOND, 16),
                    new ItemStack(Items.NETHERITE_INGOT, 4),
                    new ItemStack(Items.TOTEM_OF_UNDYING, 2),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 3),
                    new ItemStack(Items.ANCIENT_DEBRIS, 8),
                    new ItemStack(Items.NETHER_STAR, 1),
                    new ItemStack(Items.ELYTRA, 1),
                    new ItemStack(Items.DIAMOND_BLOCK, 4)
            ));
            int currentTick = serverLevel.getServer().getTickCount();
            for (int i = 0; i < treasures.size(); i++) {
                final ItemStack drop = treasures.get(i);
                final int delay = i * 6;
                serverLevel.getServer().tell(new TickTask(currentTick + delay, () -> {
                    double ox = (RAND.nextDouble() - 0.5) * 6;
                    double oz = (RAND.nextDouble() - 0.5) * 6;
                    ItemEntity entity = new ItemEntity(serverLevel,
                            player.getX() + ox, player.getY() + 8, player.getZ() + oz, drop);
                    entity.setDeltaMovement(0, -0.2, 0);
                    serverLevel.addFreshEntity(entity);
                }));
            }
            msg(player, "§e§lLEGENDARY! Treasure rains from the sky!");
        });

        // Chaos Storm
        pool.add(() -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            int currentTick = serverLevel.getServer().getTickCount();
            for (int i = 0; i < 10; i++) {
                final int fi = i;
                serverLevel.getServer().tell(new TickTask(currentTick + fi * 6, () -> {
                    double angle = (2 * Math.PI / 10) * fi;
                    double bx = player.getX() + Math.cos(angle) * 5;
                    double bz = player.getZ() + Math.sin(angle) * 5;
                    double by = player.getY();
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    if (bolt != null) {
                        bolt.moveTo(bx, by, bz);
                        bolt.setVisualOnly(true);
                        serverLevel.addFreshEntity(bolt);
                    }
                    List<ItemStack> strikeLoot = List.of(
                            new ItemStack(Items.DIAMOND, 2 + RAND.nextInt(4)),
                            new ItemStack(Items.GOLD_INGOT, 4 + RAND.nextInt(8)),
                            new ItemStack(Items.EMERALD, 2 + RAND.nextInt(4))
                    );
                    strikeLoot.forEach(loot -> {
                        ItemEntity lootEntity = new ItemEntity(serverLevel, bx, by + 1, bz, loot);
                        serverLevel.addFreshEntity(lootEntity);
                    });
                }));
            }
            msg(player, "§e§lLEGENDARY! Chaos Storm strikes!");
        });

        // NEW — Dice Jackpot: gives 5 more chaos dice
        pool.add(() -> {
            player.addItem(new ItemStack(player.getItemInHand(InteractionHand.MAIN_HAND).getItem(), 0));
            // We reference the item via the registered item — replace with your actual item reference
            // For now we give via the item's own class trick; wire up your ModItems reference here:
            player.addItem(new ItemStack(ModItems.CHAOSDICE.get(), 5));
            // Placeholder until you wire ModItems:
            player.sendSystemMessage(Component.literal(
                    "§e§lLEGENDARY! JACKPOT! 5 Chaos Dice appear! (wire ModItems.CHAOS_DICE.get())"));
        });

        // NEW — Ender Dragon loot drop
        pool.add(() -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            List<ItemStack> endLoot = List.of(
                    new ItemStack(Items.DRAGON_EGG),
                    new ItemStack(Items.ELYTRA),
                    new ItemStack(Items.SHULKER_BOX),
                    new ItemStack(Items.END_CRYSTAL, 4),
                    new ItemStack(Items.DRAGON_HEAD),
                    new ItemStack(Items.CHORUS_FRUIT, 16)
            );
            int currentTick = serverLevel.getServer().getTickCount();
            for (int i = 0; i < endLoot.size(); i++) {
                final ItemStack drop = endLoot.get(i);
                final int delay = i * 8;
                serverLevel.getServer().tell(new TickTask(currentTick + delay, () -> {
                    double ox = (RAND.nextDouble() - 0.5) * 4;
                    double oz = (RAND.nextDouble() - 0.5) * 4;
                    ItemEntity entity = new ItemEntity(serverLevel,
                            player.getX() + ox, player.getY() + 5, player.getZ() + oz, drop);
                    entity.setDeltaMovement(0, -0.15, 0);
                    serverLevel.addFreshEntity(entity);
                }));
            }
            msg(player, "§e§lLEGENDARY! The End yields its treasures!");
        });

        // NEW — Chaos Titan: buffed Iron Golem follows player
        pool.add(() -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            var golem = EntityType.IRON_GOLEM.create(serverLevel);
            if (golem == null) return;
            golem.moveTo(player.getX() + 2, player.getY(), player.getZ() + 2, 0, 0);
            // Buff its health
            golem.getAttribute(Attributes.MAX_HEALTH).setBaseValue(500.0);
            golem.setHealth(500.0f);
            golem.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(30.0);
            golem.setPlayerCreated(true);
            serverLevel.addFreshEntity(golem);

            // Remove after 5 minutes (6000 ticks)
            int currentTick = serverLevel.getServer().getTickCount();
            serverLevel.getServer().tell(new TickTask(currentTick + 6000, () -> {
                if (!golem.isRemoved()) {
                    // Dramatic goodbye particles
                    serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                            golem.getX(), golem.getY() + 1, golem.getZ(),
                            20, 0.5, 0.5, 0.5, 0.1);
                    golem.discard();
                }
            }));
            msg(player, "§e§lLEGENDARY! A Chaos Titan rises to protect you!");
        });

        ModCompatibility.addLegendaryEffects(pool, player);
        return pool;
    }

    // -------------------------------------------------------------------------
    // NEGATIVE POOL
    // -------------------------------------------------------------------------

    private List<Runnable> negativeEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        pool.add(() -> {
            var inventory = player.getInventory();
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                items.add(inventory.getItem(i).copy());
            }
            Collections.shuffle(items, RAND);
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                inventory.setItem(i, items.get(i));
            }
            msg(player, "§4Negative! Your inventory has been scrambled!");
        });
        pool.add(() -> {
            player.setDeltaMovement(0, 2.5, 0);
            msg(player, "§4Negative! Gravity betrays you!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 2400, 2));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, 1));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 1));
            msg(player, "§4Negative! You feel cursed with hunger!");
        });
        pool.add(() -> {
            List<EntityType<? extends Mob>> mobs = List.of(
                    EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                    EntityType.PHANTOM, EntityType.WITCH, EntityType.PILLAGER
            );
            for (int i = 0; i < 5; i++) {
                EntityType<? extends Mob> type = mobs.get(RAND.nextInt(mobs.size()));
                Mob mob = type.create(level);
                if (mob != null) {
                    double ox = (RAND.nextDouble() - 0.5) * 6;
                    double oz = (RAND.nextDouble() - 0.5) * 6;
                    mob.moveTo(player.getX() + ox, player.getY(), player.getZ() + oz, 0, 0);
                    level.addFreshEntity(mob);
                }
            }
            msg(player, "§4Negative! A mob party crashes your game!");
        });
        pool.add(() -> {
            var inventory = player.getInventory();
            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory.getItem(i);
                if (!stack.isEmpty()) {
                    double ox = (RAND.nextDouble() - 0.5) * 8;
                    double oz = (RAND.nextDouble() - 0.5) * 8;
                    ItemEntity entity = new ItemEntity(level,
                            player.getX() + ox, player.getY() + 1, player.getZ() + oz, stack.copy());
                    entity.setDeltaMovement(
                            (RAND.nextDouble() - 0.5) * 0.3,
                            0.3 + RAND.nextDouble() * 0.2,
                            (RAND.nextDouble() - 0.5) * 0.3
                    );
                    level.addFreshEntity(entity);
                    inventory.setItem(i, ItemStack.EMPTY);
                }
            }
            msg(player, "§4Negative! Your hotbar was stolen!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
            msg(player, "§4Negative! You can't see or think straight!");
        });
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
            msg(player, "§4Negative! Wither consumes you!");
        });
        pool.add(() -> {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setDayTime(18000);
                serverLevel.setWeatherParameters(0, 6000, true, true);
            }
            msg(player, "§4Negative! The sky turns dark and stormy!");
        });
        pool.add(() -> {
            WitherBoss wither = EntityType.WITHER.create(level);
            if (wither != null) {
                wither.moveTo(player.getX(), player.getY() + 2, player.getZ(), 0, 0);
                level.addFreshEntity(wither);
            }
            msg(player, "§4Negative... Something dark stirs nearby...");
        });


        // NEW — Anvil Rain: 5 falling anvils from above
        pool.add(() -> {
            if (!(level instanceof ServerLevel serverLevel)) return;
            int currentTick = serverLevel.getServer().getTickCount();
            for (int i = 0; i < 5; i++) {
                final int fi = i;
                serverLevel.getServer().tell(new TickTask(currentTick + fi * 8, () -> {
                    double ox = (RAND.nextDouble() - 0.5) * 4;
                    double oz = (RAND.nextDouble() - 0.5) * 4;
                    FallingBlockEntity anvil = FallingBlockEntity.fall(
                            serverLevel,
                            BlockPos.containing(player.getX() + ox, player.getY() + 15, player.getZ() + oz),
                            Blocks.ANVIL.defaultBlockState()
                    );
                    anvil.dropItem = true;
                    anvil.setHurtsEntities(6.0f, 40);
                }));
            }
            msg(player, "§4Negative! Anvils fall from the sky!");
        });

        // NEW — Mining Fatigue III for 2 minutes
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2400, 2));
            msg(player, "§4Negative! Your arms feel like lead!");
        });

        // NEW — Levitation 10 seconds
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 1));
            msg(player, "§4Negative! You're floating away!");
        });

        // NEW — Bad Omen V
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 120000, 4));
            msg(player, "§4Negative! A dark omen falls upon you...");
        });

        // NEW — Amnesia: remove 10 XP levels
        pool.add(() -> {
            int levelsToRemove = Math.min(10, player.experienceLevel);
            player.giveExperienceLevels(-levelsToRemove);
            msg(player, "§4Negative! You forgot " + levelsToRemove + " levels of experience!");
        });

        // NEW — Size Swap: Slowness V + Weakness III + Resistance II
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 4));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2400, 1));
            msg(player, "§4Negative! You shrink into helplessness!");
        });

        // NEW — Phantom Swarm: 8 phantoms from above
        pool.add(() -> {
            for (int i = 0; i < 8; i++) {
                Phantom phantom = EntityType.PHANTOM.create(level);
                if (phantom != null) {
                    double ox = (RAND.nextDouble() - 0.5) * 8;
                    double oz = (RAND.nextDouble() - 0.5) * 8;
                    phantom.moveTo(player.getX() + ox, player.getY() + 10, player.getZ() + oz, 0, 0);
                    level.addFreshEntity(phantom);
                }
            }
            msg(player, "§4Negative! A phantom swarm descends!");
        });

        ModCompatibility.addNegativeEffects(pool, player);
        return pool;
    }

    // -------------------------------------------------------------------------
    // RITUAL ANIMATION (unchanged)
    // -------------------------------------------------------------------------

    private void spawnRitualSequence(TierResult tier, ServerLevel serverLevel, Player player, ItemEntity dice) {
        int currentTick = serverLevel.getServer().getTickCount();

        SoundEvent revealSound = switch (tier) {
            case COMMON    -> SoundEvents.EXPERIENCE_ORB_PICKUP;
            case RARE      -> SoundEvents.PLAYER_LEVELUP;
            case VERY_RARE -> SoundEvents.TOTEM_USE;
            case EPIC      -> SoundEvents.END_PORTAL_SPAWN;
            case LEGENDARY -> SoundEvents.UI_TOAST_CHALLENGE_COMPLETE;
            case NEGATIVE  -> SoundEvents.WARDEN_EMERGE;
        };

        // Phase 1 — Awakening
        playSound(serverLevel, dice.getX(), dice.getY(), dice.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 0.6f);
        for (int t = 0; t < 20; t += 4) {
            final int tick = t;
            serverLevel.getServer().tell(new TickTask(currentTick + tick, () -> {
                if (dice.isRemoved()) return;
                double angle = tick * 0.3;
                double ox = Math.cos(angle) * 0.8;
                double oz = Math.sin(angle) * 0.8;
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                        dice.getX() + ox, dice.getY() + 0.25, dice.getZ() + oz,
                        3, 0.05, 0.05, 0.05, 0.01);
            }));
        }

        // Phase 2 — Spinning up
        serverLevel.getServer().tell(new TickTask(currentTick + 20, () -> {
            if (dice.isRemoved()) return;
            dice.setDeltaMovement(0.0, 0.01, 0.0);
            playSound(serverLevel, dice.getX(), dice.getY(), dice.getZ(),
                    SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 1.0f, 1.0f);
        }));

        int[] spinTicks  = {22, 25, 28, 31, 34, 37, 40};
        float[] pitches  = {0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f};
        int[] orbitCount = {4, 5, 6, 7, 8, 9, 10};

        for (int i = 0; i < spinTicks.length; i++) {
            final int fi = i;
            serverLevel.getServer().tell(new TickTask(currentTick + spinTicks[i], () -> {
                if (dice.isRemoved()) return;
                playSound(serverLevel, dice.getX(), dice.getY(), dice.getZ(),
                        SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.6f, pitches[fi]);
                spawnOrbitRing(serverLevel, ParticleTypes.ENCHANT,
                        dice.getX(), dice.getY() + 0.25, dice.getZ(), 0.6, orbitCount[fi]);
            }));
        }

        // Phase 3 — Frenzy
        serverLevel.getServer().tell(new TickTask(currentTick + 40, () -> {
            if (dice.isRemoved()) return;
            playSound(serverLevel, dice.getX(), dice.getY(), dice.getZ(),
                    SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 1.2f, 1.4f);
        }));

        int[] frenzyTicks = {42, 44, 46, 48, 50, 52, 54, 56, 58};
        float[] fPitches  = {1.2f, 1.3f, 1.4f, 1.5f, 1.6f, 1.7f, 1.8f, 1.9f, 2.0f};

        for (int i = 0; i < frenzyTicks.length; i++) {
            final int fi = i;
            serverLevel.getServer().tell(new TickTask(currentTick + frenzyTicks[i], () -> {
                if (dice.isRemoved()) return;
                playSound(serverLevel, dice.getX(), dice.getY(), dice.getZ(),
                        SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.8f, fPitches[fi]);
                spawnOrbitRing(serverLevel, ParticleTypes.WITCH,
                        dice.getX(), dice.getY() + 0.25, dice.getZ(), 0.5, 12);
                if (fi % 2 == 0) {
                    serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                            dice.getX(), dice.getY(), dice.getZ(), 4, 0.1, 0.1, 0.1, 0.05);
                }
            }));
        }

        // Phase 4 — Reveal
        serverLevel.getServer().tell(new TickTask(currentTick + 60, () -> {
            if (dice.isRemoved() || player.isRemoved()) return;

            double dx = dice.getX();
            double dy = dice.getY() + 0.25;
            double dz = dice.getZ();

            dice.discard();

            playSound(serverLevel, dx, dy, dz,
                    SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 0.7f, 1.6f);
            playSound(serverLevel, dx, dy, dz,
                    revealSound, SoundSource.PLAYERS, 1.5f, 1.0f);

            applyEffect(tier, serverLevel, player);

            switch (tier) {
                case COMMON -> {
                    spawnBurst(serverLevel, ParticleTypes.HAPPY_VILLAGER, dx, dy, dz, 50, 0.3);
                    spawnBurst(serverLevel, ParticleTypes.END_ROD, dx, dy, dz, 20, 0.2);
                }
                case RARE -> {
                    spawnBurst(serverLevel, ParticleTypes.FIREWORK, dx, dy, dz, 100, 0.4);
                    spawnBurst(serverLevel, ParticleTypes.END_ROD, dx, dy, dz, 40, 0.3);
                }
                case VERY_RARE -> {
                    spawnBurst(serverLevel, ParticleTypes.TOTEM_OF_UNDYING, dx, dy, dz, 150, 0.5);
                    spawnBurst(serverLevel, ParticleTypes.PORTAL, dx, dy, dz, 80, 0.6);
                }
                case EPIC -> {
                    spawnBurst(serverLevel, ParticleTypes.FLAME, dx, dy, dz, 180, 0.4);
                    spawnBurst(serverLevel, ParticleTypes.LAVA, dx, dy, dz, 60, 0.3);
                    spawnVerticalRing(serverLevel, ParticleTypes.SOUL_FIRE_FLAME, dx, dy, dz, 3.0, 60);
                }
                case LEGENDARY -> {
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
                    if (lightning != null) {
                        lightning.moveTo(dx, dy, dz);
                        lightning.setVisualOnly(true);
                        serverLevel.addFreshEntity(lightning);
                    }
                    playSound(serverLevel, dx, dy, dz,
                            SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 2.0f, 1.0f);
                    spawnBurst(serverLevel, ParticleTypes.END_ROD, dx, dy, dz, 250, 0.7);
                    spawnBurst(serverLevel, ParticleTypes.GLOW, dx, dy, dz, 200, 0.4);
                    spawnVerticalRing(serverLevel, ParticleTypes.GLOW_SQUID_INK, dx, dy, dz, 4.0, 80);
                }
                case NEGATIVE -> {
                    playSound(serverLevel, dx, dy, dz,
                            SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.5f, 1.0f);
                    spawnBurst(serverLevel, ParticleTypes.LARGE_SMOKE, dx, dy, dz, 200, 0.3);
                    spawnBurst(serverLevel, ParticleTypes.SOUL, dx, dy, dz, 100, 0.2);
                    spawnBurst(serverLevel, ParticleTypes.SQUID_INK, dx, dy, dz, 120, 0.4);
                    spawnVerticalRing(serverLevel, ParticleTypes.SMOKE, dx, dy, dz, 3.0, 60);
                }
            }
        }));
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private void msg(Player player, String text) {
        player.sendSystemMessage(Component.literal(text));
    }

    private void playSound(ServerLevel level, double x, double y, double z,
                           SoundEvent sound, SoundSource source, float volume, float pitch) {
        level.playSound(null, x, y, z, sound, source, volume, pitch);
    }

    private void spawnOrbitRing(ServerLevel level, ParticleOptions particle,
                                double cx, double cy, double cz, double radius, int count) {
        for (int i = 0; i < count; i++) {
            double angle = (2 * Math.PI / count) * i;
            double ox = Math.cos(angle) * radius;
            double oz = Math.sin(angle) * radius;
            level.sendParticles(particle, cx + ox, cy, cz + oz, 1, 0, 0, 0, 0.01);
        }
    }

    private void spawnBurst(ServerLevel level, ParticleOptions particle,
                            double cx, double cy, double cz, int count, double speed) {
        level.sendParticles(particle, cx, cy, cz, count, 0.4, 0.4, 0.4, speed);
    }

    private void spawnVerticalRing(ServerLevel level, ParticleOptions particle,
                                   double cx, double cy, double cz, double height, int count) {
        for (int i = 0; i < count; i++) {
            double t = (double) i / count;
            double y = cy + t * height;
            double angle = t * Math.PI * 4;
            double radius = 0.5 + t * 0.8;
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;
            level.sendParticles(particle, cx + dx, y, cz + dz, 1, 0, 0.1, 0, 0.02);
        }
    }
}