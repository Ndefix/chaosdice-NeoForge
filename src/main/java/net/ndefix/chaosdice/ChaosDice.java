package net.ndefix.chaosdice;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.ndefix.chaosdice.item.ModCreativeModeTabs;
import net.ndefix.chaosdice.item.ModItems;
import org.slf4j.Logger;

@Mod(ChaosDice.MODID)
public class ChaosDice {
    public static final String MODID = "chaosdice";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ChaosDice(IEventBus modEventBus, ModContainer modContainer) {
        // Rejestracja przedmiotów
        ModItems.register(modEventBus);

        // REJESTRACJA ZAKŁADKI KREATYWNEJ
        ModCreativeModeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Ta metoda może zostać pusta, jeśli dodajesz itemy bezpośrednio w ModCreativeModeTabs
        // Ale możesz tu zostawić dodawanie do standardowych zakładek, jeśli chcesz:
        // if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) { event.accept(ModItems.CHAOSDICE); }
    }

    @net.neoforged.bus.api.SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}