package net.ndefix.chaosdice.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class BlessedDiceItem extends ChaosDiceItem {

    public BlessedDiceItem(Properties properties) {
        super(properties);
    }

    // Rolls only positive tiers — weighted toward rare/very rare, with a
    // small legendary chance. Never common, never negative.
    @Override
    protected TierResult rollTier() {
        // Weights: Rare=40, VeryRare=30, Epic=20, Legendary=10
        int roll = (int) (Math.random() * 100);
        if (roll < 40) return TierResult.RARE;
        if (roll < 70) return TierResult.VERY_RARE;
        if (roll < 90) return TierResult.EPIC;
        return TierResult.LEGENDARY;
    }

    @Override
    protected void applyEffect(TierResult tier, Level level, Player player) {
        switch (tier) {
            case RARE      -> pickRandom(rareEffects(level, player)).run();
            case VERY_RARE -> pickRandom(veryRareEffects(level, player)).run();
            case EPIC      -> pickRandom(epicEffects(level, player)).run();
            case LEGENDARY -> pickRandom(legendaryEffects(level, player)).run();
            // Fallback — should never happen but be safe
            default        -> pickRandom(rareEffects(level, player)).run();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltipComponents,
                                TooltipFlag tooltipFlag) {
        tooltipComponents.add(
                Component.literal("Touched by divine fortune.")
                        .withStyle(s -> s.withColor(0xFFD700).withItalic(true))
        );
        tooltipComponents.add(
                Component.literal("Fate smiles upon the worthy.")
                        .withStyle(s -> s.withColor(0xFFA500).withItalic(true))
        );
        tooltipComponents.add(
                Component.literal("Roll. And be rewarded.")
                        .withStyle(s -> s.withColor(0xFFFFAA).withItalic(true))
        );
    }
}