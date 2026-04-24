package net.ndefix.chaosdice.item;

import net.ndefix.chaosdice.ChaosDice;
import net.ndefix.chaosdice.item.custom.ChaosDiceItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ChaosDice.MODID);

    // Zmieniamy "new Item" na "new ChaosDiceItem"
    public static final DeferredItem<ChaosDiceItem> CHAOSDICE = ITEMS.register("chaosdice",
            () -> new ChaosDiceItem(new Item.Properties().stacksTo(64)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}