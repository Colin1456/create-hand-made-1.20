package com.alben.createhandmade.network;

import com.alben.createhandmade.CreateHandMade;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

public record FluidParticlesPacket(BlockPos pos, FluidStack fluid)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<FluidParticlesPacket> TYPE =
            new CustomPacketPayload.Type<>(
                    ResourceLocation.fromNamespaceAndPath(CreateHandMade.MODID, "fluid_particles"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FluidParticlesPacket> STREAM_CODEC =
            StreamCodec.composite(
                    BlockPos.STREAM_CODEC, FluidParticlesPacket::pos,
                    FluidStack.STREAM_CODEC, FluidParticlesPacket::fluid,
                    FluidParticlesPacket::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}