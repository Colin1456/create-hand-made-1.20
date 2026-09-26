package com.alben.createhandmade.particle;

import com.alben.createhandmade.CreateHandMade;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticleTypes {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, CreateHandMade.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<BellowsAirParticleData>> BELLOWS_AIR =
            PARTICLE_TYPES.register("bellows_air",
                    () -> new BellowsAirParticleData().createType());

    public static void register(IEventBus modEventBus) {
        PARTICLE_TYPES.register(modEventBus);
    }

    /** 客户端工厂注册（mod 总线，仅客户端） */
    @EventBusSubscriber(modid = CreateHandMade.MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ModParticleTypes.BELLOWS_AIR.get(),
                    BellowsAirParticleProvider::new);
        }
    }
}