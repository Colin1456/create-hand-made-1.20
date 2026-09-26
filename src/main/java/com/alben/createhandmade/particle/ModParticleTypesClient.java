package com.alben.createhandmade.particle;

import com.alben.createhandmade.CreateHandMade;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(modid = CreateHandMade.MODID, value = Dist.CLIENT)
public class ModParticleTypesClient {

    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ModParticleTypes.BELLOWS_AIR.get(),
                BellowsAirParticleProvider::new
        );
    }
}