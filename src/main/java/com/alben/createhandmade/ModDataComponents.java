package com.alben.createhandmade;

import com.alben.createhandmade.item.InfusionGunContents;
import com.alben.createhandmade.item.MortarContents;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, CreateHandMade.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MortarContents>> MORTAR_CONTENTS =
            DATA_COMPONENTS.register("mortar_contents", () -> DataComponentType.<MortarContents>builder()
                    .persistent(MortarContents.CODEC)
                    .networkSynchronized(MortarContents.STREAM_CODEC)
                    .build());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InfusionGunContents>> INFUSION_GUN_CONTENTS =
            DATA_COMPONENTS.register("infusion_gun_contents", () -> DataComponentType.<InfusionGunContents>builder()
                    .persistent(InfusionGunContents.CODEC)
                    .networkSynchronized(InfusionGunContents.STREAM_CODEC)
                    .build());

    /** 手锯当前选中的切削配方索引 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> HAND_SAW_RECIPE_INDEX =
            DATA_COMPONENTS.register("hand_saw_recipe_index", () -> DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build());

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}