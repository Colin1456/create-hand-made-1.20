package com.alben.createhandmade.recipe;

import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

/**
 * 「独占配方」追加钩子（当前为空壳）。
 *
 * <p><b>将来的职责：</b>在这里集中实现「某个配方只属于某个工具」的能力，
 * 例如：把某条研磨配方从研钵那里排除（由 {@link HandMadeRecipeFilters} 负责），
 * 或反过来给碾钵额外追加一条原版数据包里不存在的配方。</p>
 *
 * <p>之所以和 {@link HandMadeRecipeFilters} 分成两个类，是因为两者的语义方向相反：</p>
 * <ul>
 *   <li>{@code Filters}：从「已有配方集合」里做减法，输入来自世界配方管理器；</li>
 *   <li>{@code Registry}：向结果里做加法，输入来自本模组自己注册的内容（将来可能来自
 *       代码、数据包或 {@code DeferredRegister}），因此它有自己独立的生命周期与缓存需求。</li>
 * </ul>
 *
 * <p><b>硬性约束：</b>本类不允许 import 任何 {@code mezz.jei.*}。</p>
 *
 * <p>被 {@link HandMadeRecipePool#getBaseRecipes} 在过滤之后调用，
 * 是「过滤后配方 -> 最终配方」这一段。</p>
 */
public final class HandMadeRecipeRegistry {

    private HandMadeRecipeRegistry() {
    }

    /**
     * 为某个 {@link HandMadeTool} 追加独占配方。
     *
     * <p>当前实现原样返回，即不追加任何内容，保证与重构前的行为完全一致。</p>
     *
     * @param tool    目标工具 + 配方类型组合
     * @param recipes 过滤后的候选配方（可能为空，但不会为 null）
     * @return 追加独占配方后的列表；空壳阶段直接返回入参本身
     */
    public static List<RecipeHolder<?>> appendExclusive(HandMadeTool tool, List<RecipeHolder<?>> recipes) {
        return recipes;
    }
}
