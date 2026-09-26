package com.alben.createhandmade.bellows;

import com.alben.createhandmade.CreateHandMade;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = CreateHandMade.MODID)
public class BellowsMediaEvents {

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new BellowsMediaReloadListener());
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        BellowsMediaRegistry.autoDiscover();
    }
}