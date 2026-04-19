package net.ndefix.chaosdice.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ChaosDiceItem extends Item {
    public ChaosDiceItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Wykonujemy logikę tylko po stronie serwera
        if (!level.isClientSide()) {
            RandomSource random = level.getRandom();
            int roll = random.nextInt(3);

            // Wybór losowego efektu
            switch (roll) {
                case 0 -> {
                    BlockPos pos = player.blockPosition();
                    LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(level);
                    if (lightning != null) {
                        lightning.moveTo(Vec3.atBottomCenterOf(pos));
                        level.addFreshEntity(lightning);
                        player.sendSystemMessage(Component.literal("§6Chaos: Piorun kulisty!"));
                    }
                }
                case 1 -> {
                    player.setDeltaMovement(player.getDeltaMovement().add(0, 1.5, 0));
                    player.hurtMarked = true;
                    player.sendSystemMessage(Component.literal("§bChaos: Grawitacja to sugestia!"));
                }
                case 2 -> {
                    player.heal(4.0f);
                    player.sendSystemMessage(Component.literal("§dChaos: Oddech życia!"));
                }
            }

            // KLUCZOWA ZMIANA: Usunięcie przedmiotu z ekwipunku
            // Metoda consume automatycznie sprawdza, czy gracz jest w trybie Creative.
            // Jeśli nie jest – zmniejsza liczbę przedmiotów w stosie o 1.
            itemStack.consume(1, player);

            // Opcjonalnie: cooldown, jeśli gracz miałby więcej niż jedną kostkę w innym slocie
            player.getCooldowns().addCooldown(this, 20);
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }
}