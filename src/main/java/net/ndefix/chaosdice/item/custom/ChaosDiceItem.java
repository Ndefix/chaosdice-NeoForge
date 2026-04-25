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
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
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
            player.getCooldowns().addCooldown(this, 100);

            spawnRitualSequence(tier, serverLevel, player, floatingDice);
            itemStack.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    // -------------------------------------------------------------------------
    // TIERS
    // -------------------------------------------------------------------------

    public enum TierResult {
        COMMON, RARE, VERY_RARE, EPIC, LEGENDARY, NEGATIVE
    }

    private TierResult rollTier() {
        double roll = Math.random() * 100;
        if (roll < 40)      return TierResult.COMMON;
        else if (roll < 55) return TierResult.RARE;
        else if (roll < 65) return TierResult.VERY_RARE;
        else if (roll < 70) return TierResult.EPIC;
        else if (roll < 71) return TierResult.LEGENDARY;
        else                return TierResult.NEGATIVE;
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

        // 32x bread
        pool.add(() -> {
            player.addItem(new ItemStack(Items.BREAD, 32));
            msg(player, "§aCommon! Fresh bread from the void!");
        });

        // 32x cooked beef
        pool.add(() -> {
            player.addItem(new ItemStack(Items.COOKED_BEEF, 32));
            msg(player, "§aCommon! A feast of steak appears!");
        });

        // 16x arrows
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ARROW, 16));
            msg(player, "§aCommon! A quiver of arrows!");
        });

        // 8x spectral arrows
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SPECTRAL_ARROW, 8));
            msg(player, "§aCommon! Spectral arrows, nice!");
        });

        // Stone tools set
        pool.add(() -> {
            player.addItem(new ItemStack(Items.STONE_SWORD));
            player.addItem(new ItemStack(Items.STONE_PICKAXE));
            player.addItem(new ItemStack(Items.STONE_AXE));
            msg(player, "§aCommon! A basic set of stone tools!");
        });

        // Iron tools set
        pool.add(() -> {
            player.addItem(new ItemStack(Items.IRON_SWORD));
            player.addItem(new ItemStack(Items.IRON_PICKAXE));
            player.addItem(new ItemStack(Items.IRON_AXE));
            msg(player, "§aCommon! Iron tools, reliable!");
        });

        // Leather armor
        pool.add(() -> {
            player.addItem(new ItemStack(Items.LEATHER_HELMET));
            player.addItem(new ItemStack(Items.LEATHER_CHESTPLATE));
            player.addItem(new ItemStack(Items.LEATHER_LEGGINGS));
            player.addItem(new ItemStack(Items.LEATHER_BOOTS));
            msg(player, "§aCommon! A leather armor set!");
        });

        // XP 3–8 levels
        pool.add(() -> {
            int levels = 3 + RAND.nextInt(6);
            player.giveExperienceLevels(levels);
            msg(player, "§aCommon! +" + levels + " XP levels!");
        });

        // Speed II 60s
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 1));
            msg(player, "§aCommon! You feel swift!");
        });

        // Jump Boost II 60s
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 1));
            msg(player, "§aCommon! Feeling bouncy!");
        });

        // Saturation 30s
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 600, 0));
            msg(player, "§aCommon! Your hunger is satisfied!");
        });

        // 64x torches
        pool.add(() -> {
            player.addItem(new ItemStack(Items.TORCH, 64));
            msg(player, "§aCommon! Let there be light!");
        });

        // 32x ladders
        pool.add(() -> {
            player.addItem(new ItemStack(Items.LADDER, 32));
            msg(player, "§aCommon! Climb anything!");
        });

        // 8x ender pearls
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ENDER_PEARL, 8));
            msg(player, "§aCommon! Ender pearls at your service!");
        });

        return pool;
    }

    // -------------------------------------------------------------------------
    // RARE POOL
    // -------------------------------------------------------------------------

    private List<Runnable> rareEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        // 3–5 emeralds
        pool.add(() -> {
            int count = 3 + RAND.nextInt(3);
            player.addItem(new ItemStack(Items.EMERALD, count));
            msg(player, "§9Rare! " + count + " emeralds!");
        });

        // Enchanted bow Power III Infinity
        pool.add(() -> {
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.POWER), 3);
            bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.INFINITY), 1);
            player.addItem(bow);
            msg(player, "§9Rare! An enchanted bow!");
        });

        // Full iron armor enchanted Prot II
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

        // Strength II 3 min
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 1));
            msg(player, "§9Rare! Strength surges through you!");
        });

        // Night Vision 5 min
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 6000, 0));
            msg(player, "§9Rare! You can see in the dark!");
        });

        // 16x obsidian
        pool.add(() -> {
            player.addItem(new ItemStack(Items.OBSIDIAN, 16));
            msg(player, "§9Rare! Obsidian materializes!");
        });

        // 16x XP bottles
        pool.add(() -> {
            player.addItem(new ItemStack(Items.EXPERIENCE_BOTTLE, 16));
            msg(player, "§9Rare! Bottles of experience!");
        });

        // Saddle + iron horse armor
        pool.add(() -> {
            player.addItem(new ItemStack(Items.SADDLE));
            player.addItem(new ItemStack(Items.IRON_HORSE_ARMOR));
            msg(player, "§9Rare! Ready to ride!");
        });

        // Trident
        pool.add(() -> {
            player.addItem(new ItemStack(Items.TRIDENT));
            msg(player, "§9Rare! A trident from the depths!");
        });

        // 32x gold ingots
        pool.add(() -> {
            player.addItem(new ItemStack(Items.GOLD_INGOT, 32));
            msg(player, "§9Rare! Gold pours forth!");
        });

        return pool;
    }

    // -------------------------------------------------------------------------
    // VERY RARE POOL
    // -------------------------------------------------------------------------

    private List<Runnable> veryRareEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        // Elytra
        pool.add(() -> {
            ItemStack elytra = new ItemStack(Items.ELYTRA);
            elytra.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.UNBREAKING), 3);
            player.addItem(elytra);
            msg(player, "§5Very Rare! Wings of chaos!");
        });

        // Enchanted golden apple
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE));
            msg(player, "§5Very Rare! A golden apple of legends!");
        });


        // Mending + Unbreaking III book
        pool.add(() -> {
            ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
            book.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.MENDING), 1);
            book.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                    .getOrThrow(Enchantments.UNBREAKING), 3);
            player.addItem(book);
            msg(player, "§5Very Rare! Mending + Unbreaking III book!");
        });

        // Beacon
        pool.add(() -> {
            player.addItem(new ItemStack(Items.BEACON));
            msg(player, "§5Very Rare! A beacon of power!");
        });

        return pool;
    }

    // -------------------------------------------------------------------------
    // EPIC POOL
    // -------------------------------------------------------------------------

    private List<Runnable> epicEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        // Enchanted bow Power V, Flame, Infinity, Unbreaking III
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

        // All max positive effects 3 min
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

        // 5x enchanted golden apples
        pool.add(() -> {
            player.addItem(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 5));
            msg(player, "§6Epic! Five enchanted golden apples!");
        });

        // Loot room — 3x3 obsidian platform with chests
        pool.add(() -> {
            ServerLevel serverLevel = (ServerLevel) level;
            BlockPos center = player.blockPosition().below();
            List<ItemStack> loot = List.of(
                    new ItemStack(Items.DIAMOND, 4),
                    new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1),
                    new ItemStack(Items.TOTEM_OF_UNDYING, 1)
            );

            // Place obsidian platform
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    level.setBlock(center.offset(x, 0, z),
                            net.minecraft.world.level.block.Blocks.OBSIDIAN.defaultBlockState(),
                            3);
                }
            }

            // Place 4 chests at corners and fill them
            List<BlockPos> chestPositions = List.of(
                    center.offset(-1, 1, -1),
                    center.offset(1, 1, -1),
                    center.offset(-1, 1, 1),
                    center.offset(1, 1, 1)
            );

            for (BlockPos chestPos : chestPositions) {
                level.setBlock(chestPos,
                        net.minecraft.world.level.block.Blocks.CHEST.defaultBlockState(), 3);
                if (level.getBlockEntity(chestPos) instanceof
                        net.minecraft.world.level.block.entity.ChestBlockEntity chest) {
                    for (int i = 0; i < loot.size(); i++) {
                        chest.setItem(i, loot.get(i).copy());
                    }
                }
            }
            msg(player, "§6Epic! A loot room has appeared around you!");
        });

        return pool;
    }

    // -------------------------------------------------------------------------
    // LEGENDARY POOL
    // -------------------------------------------------------------------------

    private List<Runnable> legendaryEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        // Treasure rain — 20 items drop from sky over 5 seconds
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
                final int delay = i * 6; // every 6 ticks (~0.3s apart)
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

        // Chaos Storm — 10 lightning bolts in a circle + loot piles
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

                    // Drop random loot at each strike
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

        return pool;
    }

    // -------------------------------------------------------------------------
    // NEGATIVE POOL
    // -------------------------------------------------------------------------

    private List<Runnable> negativeEffects(Level level, Player player) {
        List<Runnable> pool = new ArrayList<>();

        // Inventory scramble
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

        // Gravity flip — launches player upward
        pool.add(() -> {
            player.setDeltaMovement(0, 2.5, 0);
            msg(player, "§4Negative! Gravity betrays you!");
        });

        // Curse of Hunger — Hunger + Weakness + Slowness 2 min
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 2400, 2));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2400, 1));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 1));
            msg(player, "§4Negative! You feel cursed with hunger!");
        });

        // Mob party — 5 random hostile mobs
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

        // Item thief — scatters hotbar on ground
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

        // Blindness + Nausea 15s
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, 0));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 300, 0));
            msg(player, "§4Negative! You can't see or think straight!");
        });

        // Wither II 10s
        pool.add(() -> {
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
            msg(player, "§4Negative! Wither consumes you!");
        });

        // Time Bandit — midnight + thunderstorm
        pool.add(() -> {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.setDayTime(18000); // midnight
                serverLevel.setWeatherParameters(0, 6000, true, true); // thunderstorm
            }
            msg(player, "§4Negative! The sky turns dark and stormy!");
        });

        // Spawn Wither
        pool.add(() -> {
            WitherBoss wither = EntityType.WITHER.create(level);
            if (wither != null) {
                wither.moveTo(player.getX(), player.getY() + 2, player.getZ(), 0, 0);
                level.addFreshEntity(wither);
            }
            msg(player, "§4Negative... Something dark stirs nearby...");
        });

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