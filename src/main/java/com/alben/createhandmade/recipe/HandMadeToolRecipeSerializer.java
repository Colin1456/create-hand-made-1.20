package com.alben.createhandmade.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * 独占配方的 serializer：在 Create 的泛型 serializer 之上<b>只多读/写一个 {@code tool} 字段</b>。
 *
 * <p>委托对象是 {@code new StandardProcessingRecipe.Serializer<>(HandMadeToolRecipe::new)} ——
 * Create 那个 serializer 完全泛型、不含任何 RecipeType 检查
 * （{@code StandardProcessingRecipe.java:45-69}、{@code ProcessingRecipe.java:215-226}），
 * 所以 {@code ingredients} / {@code results} / {@code processing_time} /
 * {@code heat_requirement} 的解析<b>逐字复用</b>，本类不重复实现任何参数逻辑。</p>
 *
 * <p>而 {@code tool} 不在 {@code ProcessingRecipeParams} 里，Create 的 codec 会把
 * {@code RecordCodecBuilder} 未声明的键当未知字段忽略（DFU 标准行为），
 * 所以必须由本类自己在 {@code decode} 之后补读、在 {@code encode} 时补写。</p>
 */
public class HandMadeToolRecipeSerializer implements RecipeSerializer<HandMadeToolRecipe> {

    private static final String TOOL_FIELD = "tool";

    /** 本任务范围：只有 basin 家族的两个工具支持独占配方。 */
    private static boolean supportsExclusiveRecipes(HandMadeTool tool) {
        return tool == HandMadeTool.PRESS_HAMMER_BASIN || tool == HandMadeTool.STIRRING_STAFF;
    }

    private final StandardProcessingRecipe.Serializer<HandMadeToolRecipe> delegate =
            new StandardProcessingRecipe.Serializer<>(HandMadeToolRecipe::new);

    @Override
    public MapCodec<HandMadeToolRecipe> codec() {
        return new MapCodec<>() {

            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                // 声明出去：让数据包校验 / 导出工具知道本 schema 还有 tool 这个键
                return Stream.concat(delegate.codec().keys(ops), Stream.of(ops.createString(TOOL_FIELD)));
            }

            @Override
            public <T> DataResult<HandMadeToolRecipe> decode(DynamicOps<T> ops, MapLike<T> input) {
                // 1) 先交给 Create 的 codec 解析其余全部字段（它会忽略 tool）
                DataResult<HandMadeToolRecipe> baseResult = delegate.codec().decode(ops, input);
                if (baseResult.error().isPresent()) {
                    return baseResult;
                }

                // 2) 读 tool 字段
                T toolValue = input.get(TOOL_FIELD);
                if (toolValue == null) {
                    return DataResult.error(() -> "Missing required field '" + TOOL_FIELD
                            + "' for " + HandMadeRecipeTypes.TOOL_RECIPE.getId());
                }

                DataResult<String> toolIdResult = Codec.STRING.parse(ops, toolValue);
                if (toolIdResult.error().isPresent()) {
                    return DataResult.error(() -> "Invalid '" + TOOL_FIELD + "' field: expected a tool id string");
                }

                String toolId = toolIdResult.getOrThrow();

                // 3) tool id → 枚举。这里刻意不复用 HandMadeRecipeFilterLoader.parseTool：
                //    它的签名是 (String, ResourceLocation fileId)，且语义是「记 warning 后返回 null」，
                //    而 MapCodec.decode 拿不到配方 id（只能拿到 ops + MapLike），传 null 会打出误导性的
                //    "... in tool filter null" 日志。所以这里复制一份「只解析、不记日志」的实现，
                //    错误信息交给 DataResult 报给数据包加载器。
                HandMadeTool tool = parseToolOrNull(toolId);
                if (tool == null) {
                    return DataResult.error(() -> "Unknown tool id: '" + toolId + "'. Valid ids: "
                            + HandMadeRecipeFilterLoader.validToolIds());
                }

                // 4) 校验：只允许 basin 家族（本任务范围），其它工具家族还没有各自的配方类
                if (!supportsExclusiveRecipes(tool)) {
                    return DataResult.error(() -> "Tool '" + toolId
                            + "' does not support exclusive recipes yet. Only press_hammer_basin and stirring_staff.");
                }

                HandMadeToolRecipe recipe = baseResult.getOrThrow();
                recipe.setTool(tool);
                return DataResult.success(recipe);
            }

            @Override
            public <T> RecordBuilder<T> encode(HandMadeToolRecipe recipe, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                RecordBuilder<T> builder = delegate.codec().encode(recipe, ops, prefix);
                HandMadeTool tool = recipe.getTool();
                if (tool != null) {
                    builder.add(TOOL_FIELD, ops.createString(tool.name().toLowerCase(Locale.ROOT)));
                }
                return builder;
            }
        };
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, HandMadeToolRecipe> streamCodec() {
        return StreamCodec.of(
                (buf, recipe) -> {
                    delegate.streamCodec().encode(buf, recipe);
                    // 网络同步必须带 tool，否则客户端侧 JEI 等拿不到归属
                    buf.writeEnum(recipe.getTool() == null ? HandMadeTool.PRESS_HAMMER_BASIN : recipe.getTool());
                },
                buf -> {
                    HandMadeToolRecipe recipe = delegate.streamCodec().decode(buf);
                    recipe.setTool(buf.readEnum(HandMadeTool.class));
                    return recipe;
                }
        );
    }

    /**
     * 把 {@code tool_id} 解析成 {@link HandMadeTool}，失败返回 null（不记日志）。
     *
     * <p>与 {@link HandMadeRecipeFilterLoader#validToolIds()} 一样，枚举名与 tool id 的
     * 关系就是「全大写下划线 ↔ 全小写下划线」。</p>
     */
    @Nullable
    private static HandMadeTool parseToolOrNull(String toolId) {
        try {
            return HandMadeTool.valueOf(toolId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
