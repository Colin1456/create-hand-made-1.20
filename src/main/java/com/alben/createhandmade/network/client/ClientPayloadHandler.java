package com.alben.createhandmade.network.client;

import com.alben.createhandmade.network.FluidParticlesPacket;
import com.alben.createhandmade.network.HighlightBlockPacket;
import com.alben.createhandmade.network.PressParticlesPacket;
import com.simibubi.create.AllSpecialTextures;
import net.createmod.catnip.math.VecHelper;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.simibubi.create.content.fluids.FluidFX;


import java.util.List;
import com.alben.createhandmade.client.BellowsClientSources;
import com.alben.createhandmade.network.BellowsBlastPacket;
import com.alben.createhandmade.particle.BellowsAirParticleData;
import com.alben.createhandmade.client.BellowsClientSources;
import com.alben.createhandmade.network.BellowsBlastPacket;
import com.alben.createhandmade.particle.BellowsAirParticleData;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import net.minecraft.core.particles.ParticleOptions;
import javax.annotation.Nullable;
import com.alben.createhandmade.network.StirringStatePacket;
import com.alben.createhandmade.client.ClientStirringState;

public class ClientPayloadHandler {

    public static void handleHighlight(HighlightBlockPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            BlockPos pos = packet.pos();
            if (!level.isLoaded(pos)) return;

            Object key = "pointer_highlight_" + pos.asLong() + "_" + System.nanoTime();

            Outliner.getInstance()
                    .showAABB(key, Shapes.block().bounds().move(pos), 20)
                    .lineWidth(1 / 32f)
                    .colored(0xFFD966)
                    .withFaceTexture(AllSpecialTextures.SELECTION);
        });
    }

    public static void handlePressParticles(PressParticlesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;
            if (packet.stacks().isEmpty()) return;

            Vec3 center = VecHelper.getCenterOf(packet.pos());
            RandomSource random = level.random;
            List<ItemStack> stacks = packet.stacks();

            switch (packet.style()) {
                case DEPLOY -> {
                    for (ItemStack s : stacks) {
                        spawnDeployParticles(level, center, random, s);
                    }
                }
                case PRESS -> {
                    for (ItemStack s : stacks) {
                        spawnPressParticles(level, center, random, s);
                    }
                }
                case STIR -> {
                    for (ItemStack s : stacks) {
                        spawnStirParticles(level, center, random, s);
                    }
                }
            }
        });
    }

    private static void spawnDeployParticles(Level level, Vec3 center, RandomSource random, ItemStack stack) {
        Vec3 location = center.add(0, 0.5, 0);
        for (int i = 0; i < 20; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, random, 1 / 8f);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    location.x, location.y, location.z, motion.x, motion.y, motion.z);
        }
    }

    private static void spawnPressParticles(Level level, Vec3 center, RandomSource random, ItemStack stack) {
        Vec3 location = center.add(0, 0.5, 0);
        for (int i = 0; i < 15; i++) {
            Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, random, .125f).multiply(1, 0, 1);
            motion = motion.add(0, 0.125f, 0);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    location.x, location.y - 0.25, location.z,
                    motion.x, motion.y, motion.z);
        }
    }

    private static void spawnStirParticles(Level level, Vec3 center, RandomSource random, ItemStack stack) {
        for (int i = 0; i < 3; i++) {
            float angle = random.nextFloat() * 360;
            Vec3 offset = new Vec3(0, 0, 0.25f);
            offset = VecHelper.rotate(offset, angle, Axis.Y);
            Vec3 target = VecHelper.rotate(offset, random.nextBoolean() ? 25 : -25, Axis.Y)
                    .add(0, .25f, 0);
            Vec3 particleCenter = offset.add(center).add(0, 0.5, 0);
            target = VecHelper.offsetRandomly(target.subtract(offset), random, 1 / 128f);
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    particleCenter.x, particleCenter.y, particleCenter.z,
                    target.x, target.y, target.z);
        }
    }
    public static void handleFluidParticles(FluidParticlesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;
            if (packet.fluid().isEmpty()) return;

            Vec3 center = VecHelper.getCenterOf(packet.pos()).add(0, 0.75, 0);
            RandomSource random = level.random;
            var particle = FluidFX.getFluidParticle(packet.fluid());

            // 参考 SpoutBlockEntity.spawnSplash：20 个向上飞溅
            for (int i = 0; i < 20; i++) {
                Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, random, 0.125f);
                motion = new Vec3(motion.x, Math.abs(motion.y), motion.z);  // 只向上
                level.addAlwaysVisibleParticle(particle,
                        center.x, center.y, center.z,
                        motion.x, motion.y, motion.z);
            }
        });
    }
    public static void handleBellowsBlast(BellowsBlastPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level == null) return;

            RandomSource random = level.random;
            FanProcessingType type = packet.typeId().isEmpty()
                    ? null
                    : FanProcessingType.parse(packet.typeId());

            Vec3 origin = new Vec3(packet.originX(), packet.originY(), packet.originZ());
            Vec3 target = new Vec3(packet.targetX(), packet.targetY(), packet.targetZ());

            // ===== 1. 射线粒子：无条件，全程着色 =====
            spawnBeamParticles(level, origin, target, packet.typeId(), random);

            // ===== 2. 末端爆发：只有 spawnBlast 才做 =====
            if (packet.spawnBlast() && packet.pos() != null) {
                BlockPos pos = packet.pos();
                Vec3 center = VecHelper.getCenterOf(pos).add(0, 0.5, 0);

                boolean hasMedia = type != null;
                int count = hasMedia ? 30 : 20;

                // 爆发气体粒子
                for (int i = 0; i < count; i++) {
                    level.addParticle(
                            BellowsAirParticleData.blast(packet.typeId()),
                            center.x, center.y, center.z,
                            0, 0, 0
                    );
                }

                // 物品碎片粒子
                for (ItemStack s : packet.stacks()) {
                    if (s.isEmpty()) continue;
                    for (int i = 0; i < 10; i++) {
                        Vec3 motion = VecHelper.offsetRandomly(Vec3.ZERO, random, 1 / 8f);
                        level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, s),
                                center.x, center.y, center.z,
                                motion.x, motion.y, motion.z);
                    }
                }
            }
        });
    }

    /** 沿 [origin, target] 撒射线粒子；typeId 为空时也发（白色） */
    private static void spawnBeamParticles(Level level, Vec3 origin, Vec3 target,
                                           String typeId, RandomSource random) {
        Vec3 delta = target.subtract(origin);
        double dist = delta.length();
        if (dist < 0.1) return;

        Vec3 dir = delta.normalize();
        int count = Math.max(6, (int) (dist * 6));
        float speed = 0.4f;

        for (int i = 0; i < count; i++) {
            double t = (double) i / count;
            Vec3 p = origin.add(delta.scale(t));
            Vec3 jitter = VecHelper.offsetRandomly(Vec3.ZERO, random, 0.1f);
            Vec3 spawn = p.add(jitter);

            level.addParticle(
                    BellowsAirParticleData.beam(
                            (float) dir.x, (float) dir.y, (float) dir.z,
                            speed, typeId
                    ),
                    spawn.x, spawn.y, spawn.z,
                    0, 0, 0
            );
        }
    }

    /** 球面均匀分布的随机方向 */
    private static Vec3 randomUnitVector(RandomSource random) {
        double theta = random.nextDouble() * Math.PI * 2;
        double cosPhi = 2 * random.nextDouble() - 1;
        double sinPhi = Math.sqrt(1 - cosPhi * cosPhi);
        return new Vec3(
                sinPhi * Math.cos(theta),
                cosPhi,
                sinPhi * Math.sin(theta)
        );
    }
    public static void handleStirringState(StirringStatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientStirringState.markStirring(packet.pos());
        });
    }

}