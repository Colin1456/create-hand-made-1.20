package com.alben.createhandmade.network;

import com.alben.createhandmade.CreateHandMade;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = CreateHandMade.MODID)
public class ModNetwork {

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                HighlightBlockPacket.TYPE,
                HighlightBlockPacket.STREAM_CODEC,
                (packet, context) -> handleClient(context, () ->
                        com.alben.createhandmade.network.client.ClientPayloadHandler
                                .handleHighlight(packet, context))
        );

        registrar.playToClient(
                PressParticlesPacket.TYPE,
                PressParticlesPacket.STREAM_CODEC,
                (packet, context) -> handleClient(context, () ->
                        com.alben.createhandmade.network.client.ClientPayloadHandler
                                .handlePressParticles(packet, context))
        );

        registrar.playToClient(
                FluidParticlesPacket.TYPE,
                FluidParticlesPacket.STREAM_CODEC,
                (packet, context) -> handleClient(context, () ->
                        com.alben.createhandmade.network.client.ClientPayloadHandler
                                .handleFluidParticles(packet, context))
        );

        registrar.playToClient(
                BellowsBlastPacket.TYPE,
                BellowsBlastPacket.STREAM_CODEC,
                (packet, context) -> handleClient(context, () ->
                        com.alben.createhandmade.network.client.ClientPayloadHandler
                                .handleBellowsBlast(packet, context))
        );

        registrar.playToClient(
                StirringStatePacket.TYPE,
                StirringStatePacket.STREAM_CODEC,
                (packet, context) -> handleClient(context, () ->
                        com.alben.createhandmade.network.client.ClientPayloadHandler
                                .handleStirringState(packet, context))
        );
    }
    
    private static void handleClient(
            net.neoforged.neoforge.network.handling.IPayloadContext context,
            Runnable task) {
        context.enqueueWork(task);
    }
}