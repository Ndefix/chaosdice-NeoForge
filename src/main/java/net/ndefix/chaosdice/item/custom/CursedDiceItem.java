package net.ndefix.chaosdice.item.custom;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import java.util.List;

public class CursedDiceItem extends ChaosDiceItem {

    public CursedDiceItem(Properties properties) {
        super(properties);
    }

    // Only ever rolls negative
    @Override
    protected TierResult rollTier() {
        return TierResult.NEGATIVE;
    }

    // Only ever applies from the negative pool
    @Override
    protected void applyEffect(TierResult tier, Level level, Player player) {
        pickRandom(negativeEffects(level, player)).run();
    }

    @Override
    public void appendHoverText(net.minecraft.world.item.ItemStack stack,
                                TooltipContext context,
                                List<net.minecraft.network.chat.Component> tooltipComponents,
                                net.minecraft.world.item.TooltipFlag tooltipFlag) {
        tooltipComponents.add(
                net.minecraft.network.chat.Component.literal("Cursed with dark intent.")
                        .withStyle(s -> s.withColor(0x4A0000).withItalic(true))
        );
        tooltipComponents.add(
                net.minecraft.network.chat.Component.literal("Something bad is coming.")
                        .withStyle(s -> s.withColor(0x8B0000).withItalic(true))
        );
        tooltipComponents.add(
                net.minecraft.network.chat.Component.literal("Roll. And suffer.")
                        .withStyle(s -> s.withColor(0xFF0000).withItalic(true))
        );
    }
}