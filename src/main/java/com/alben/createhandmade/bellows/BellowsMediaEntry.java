package com.alben.createhandmade.bellows;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public record BellowsMediaEntry(ResourceLocation processingTypeId, List<Ingredient> ingredients) {
    public static final Codec<BellowsMediaEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
            ResourceLocation.CODEC.fieldOf("type").forGetter(BellowsMediaEntry::processingTypeId),
            Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(BellowsMediaEntry::ingredients)
    ).apply(i, BellowsMediaEntry::new));
}