package com.alben.createhandmade.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record MortarContents(ItemStack stack) {
    public static final Codec<MortarContents> CODEC =
            ItemStack.CODEC.xmap(MortarContents::new, MortarContents::stack);

    public static final StreamCodec<RegistryFriendlyByteBuf, MortarContents> STREAM_CODEC =
            ItemStack.STREAM_CODEC.map(MortarContents::new, MortarContents::stack);
}