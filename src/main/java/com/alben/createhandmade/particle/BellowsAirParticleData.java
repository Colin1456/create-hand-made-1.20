package com.alben.createhandmade.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import com.mojang.brigadier.StringReader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * ★ Forge 1.20.1 版本：
 *   1. ParticleOptions 要求实现 writeToString()
 *   2. ParticleOptions 要求实现 writeToNetwork(FriendlyByteBuf)
 *   3. ICustomParticleData.getCodec 返回 Codec<T>，不是 MapCodec<T>
 *   4. ICustomParticleDataWithSprite 要求实现 getDeserializer()
 *   5. Deserializer 有 3 个抽象方法：fromNetwork / fromJson / fromCommand
 */
public class BellowsAirParticleData implements ParticleOptions,
        ICustomParticleDataWithSprite<BellowsAirParticleData> {

    public static final MapCodec<BellowsAirParticleData> CODEC = RecordCodecBuilder.mapCodec(i ->
            i.group(
                    BlockPos.CODEC.fieldOf("pos").forGetter(p -> p.pos),
                    Codec.BOOL.fieldOf("beam").forGetter(p -> p.beam),
                    Codec.FLOAT.fieldOf("dx").forGetter(p -> p.dx),
                    Codec.FLOAT.fieldOf("dy").forGetter(p -> p.dy),
                    Codec.FLOAT.fieldOf("dz").forGetter(p -> p.dz),
                    Codec.FLOAT.fieldOf("speed").forGetter(p -> p.speed),
                    Codec.STRING.fieldOf("type").forGetter(p -> p.typeId)
            ).apply(i, BellowsAirParticleData::new));

    public final BlockPos pos;
    public final boolean beam;
    public final float dx, dy, dz;
    public final float speed;
    public final String typeId;

    public BellowsAirParticleData(BlockPos pos, boolean beam,
                                  float dx, float dy, float dz,
                                  float speed, String typeId) {
        this.pos = pos;
        this.beam = beam;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.speed = speed;
        this.typeId = typeId;
    }

    /** 供 ModParticleTypes.createType() 使用的无参构造 */
    public BellowsAirParticleData() {
        this(BlockPos.ZERO, false, 0, 0, 0, 0, "");
    }

    /** 爆发模式便捷构造 */
    public static BellowsAirParticleData blast(String typeId) {
        return new BellowsAirParticleData(BlockPos.ZERO, false, 0, 0, 0, 0,
                typeId == null ? "" : typeId);
    }

    /** 射线模式便捷构造 */
    public static BellowsAirParticleData beam(float dx, float dy, float dz,
                                              float speed, String typeId) {
        return new BellowsAirParticleData(BlockPos.ZERO, true, dx, dy, dz, speed, typeId);
    }

    @Override
    public @NotNull ParticleType<?> getType() {
        return ModParticleTypes.BELLOWS_AIR.get();
    }

    /** ★ 1.20.1：ParticleOptions 要求实现 writeToString() */
    @Override
    public String writeToString() {
        return "bellows_air[beam=" + beam + ",type=" + typeId + "]";
    }

    /** ★ 1.20.1：ParticleOptions 要求实现 writeToNetwork() */
    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(beam);
        buf.writeFloat(dx);
        buf.writeFloat(dy);
        buf.writeFloat(dz);
        buf.writeFloat(speed);
        buf.writeUtf(typeId);
    }

    /** ★ 1.20.1：ICustomParticleData.getCodec 返回 Codec<T>，不是 MapCodec<T> */
    @Override
    public Codec<BellowsAirParticleData> getCodec(ParticleType<BellowsAirParticleData> type) {
        return CODEC.codec();
    }

    /** ★ 1.20.1：ICustomParticleDataWithSprite 要求实现 getDeserializer() */
    @Override
    public ParticleOptions.Deserializer<BellowsAirParticleData> getDeserializer() {
        return new ParticleOptions.Deserializer<BellowsAirParticleData>() {
            @Override
            public BellowsAirParticleData fromNetwork(ParticleType<BellowsAirParticleData> type,
                                                       FriendlyByteBuf buf) {
                return new BellowsAirParticleData(
                        buf.readBlockPos(),
                        buf.readBoolean(),
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readUtf()
                );
            }

            @Override
            public BellowsAirParticleData fromJson(ParticleType<BellowsAirParticleData> type,
                                                    com.google.gson.JsonElement json) {
                return CODEC.codec()
                        .parse(com.mojang.serialization.JsonOps.INSTANCE, json)
                        .getOrThrow(false, s -> {});
            }

            /** ★ 1.20.1：Deserializer 的第 3 个抽象方法，/particle 命令解析用 */
            @Override
            public BellowsAirParticleData fromCommand(ParticleType<BellowsAirParticleData> type,
                                                       StringReader reader) {
                return new BellowsAirParticleData();
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SpriteParticleRegistration<BellowsAirParticleData> getMetaFactory() {
        return BellowsAirParticleProvider::new;
    }
}