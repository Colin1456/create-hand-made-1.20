package com.alben.createhandmade.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import net.minecraft.client.particle.ParticleEngine.SpriteParticleRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraftforge.api.distmarker.Dist;        // 1. 替换
import net.minecraftforge.api.distmarker.OnlyIn;      // 2. 替换
import org.jetbrains.annotations.NotNull;

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

    public static final StreamCodec<RegistryFriendlyByteBuf, BellowsAirParticleData> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public BellowsAirParticleData decode(RegistryFriendlyByteBuf buf) {
                    BlockPos pos = BlockPos.STREAM_CODEC.decode(buf);
                    boolean beam = buf.readBoolean();
                    float dx = buf.readFloat();
                    float dy = buf.readFloat();
                    float dz = buf.readFloat();
                    float speed = buf.readFloat();
                    String typeId = buf.readUtf();
                    return new BellowsAirParticleData(pos, beam, dx, dy, dz, speed, typeId);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, BellowsAirParticleData p) {
                    BlockPos.STREAM_CODEC.encode(buf, p.pos);
                    buf.writeBoolean(p.beam);
                    buf.writeFloat(p.dx);
                    buf.writeFloat(p.dy);
                    buf.writeFloat(p.dz);
                    buf.writeFloat(p.speed);
                    buf.writeUtf(p.typeId);
                }
            };

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

    @Override
    public MapCodec<BellowsAirParticleData> getCodec(ParticleType<BellowsAirParticleData> type) {
        return CODEC;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, BellowsAirParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public SpriteParticleRegistration<BellowsAirParticleData> getMetaFactory() {
        return BellowsAirParticleProvider::new;
    }
}