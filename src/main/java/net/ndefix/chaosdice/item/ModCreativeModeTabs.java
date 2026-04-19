package net.ndefix.chaosdice.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.ndefix.chaosdice.ChaosDice;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {
    // Tworzymy rejestr dla zakładek kreatywnych
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChaosDice.MODID);

    // Definicja samej zakładki
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CHAOS_DICE_TAB =
            CREATIVE_MODE_TABS.register("chaos_dice_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.chaosdice.chaos_dice_tab")) // Klucz tłumaczenia
                    .icon(() -> new ItemStack(ModItems.CHAOSDICE.get())) // Ikona zakładki
                    .displayItems((parameters, output) -> {
                        // Tutaj dodajemy przedmioty do zakładki
                        output.accept(ModItems.CHAOSDICE.get());
                    })
                    .build());

    // Metoda rejestrująca wywoływana w głównej klasie
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}