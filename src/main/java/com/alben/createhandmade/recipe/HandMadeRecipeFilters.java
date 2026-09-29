package com.alben.createhandmade.recipe;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 配方过滤钩子（当前为空壳）。
 *
 * <p><b>职责边界：仅做配方集合过滤，不参与配方匹配。</b>
 * 本类只对「一批已经收集好的候选配方」做增删（做减法），
 * 永远不会去判断某条配方是否匹配某份输入 —— 匹配一律由调用方
 * （各工具自己的私有 helper）用配方自己的 {@code matches} 完成。
 * 因此本类<b>不提供</b>任何形式的方法匹配工具方法。</p>
 *
 * <p><b>将来的职责：</b>在这里集中实现「禁用某个配方」的能力，数据来源计划为两层：</p>
 * <ol>
 *   <li><b>数据包过滤</b>：读取本模组自己的 recipe 过滤数据（例如
 *       {@code data/create_hand_made/handmade_recipe_filter/...}），按
 *       {@link HandMadeTool} + 配方 id 决定是否从候选列表中剔除；</li>
 *   <li><b>NeoForge 事件过滤</b>：在采集完成后发一个可取消/可修改列表的事件，
 *       让其它模组或整合包脚本参与过滤。</li>
 * </ol>
 *
 * <p><b>硬性约束：</b>本类不允许 import 任何 {@code mezz.jei.*}。
 * 它是纯服务端可用的逻辑，JEI 只是它的调用方之一。</p>
 *
 * <p>被 {@link HandMadeRecipePool#getBaseRecipes} 在收集完成后调用，
 * 是「基础配方 -&gt; 过滤后配方」这一段。</p>
 */
public final class HandMadeRecipeFilters {

    private HandMadeRecipeFilters() {
    }

    /**
     * 对某个 {@link HandMadeTool} 收集到的候选配方应用过滤。
     *
     * <p>当前实现原样返回，即不做任何过滤，保证与重构前的行为完全一致。</p>
     *
     * @param tool    触发过滤的工具 + 配方类型组合
     * @param recipes 已收集的候选配方（可能为空，但不会为 null）
     * @param level   当前世界（可能为 null，调用方需容忍）
     * @return 过滤后的配方列表；空壳阶段直接返回入参本身
     */
    public static List<RecipeHolder<?>> apply(HandMadeTool tool, List<RecipeHolder<?>> recipes, Level level) {
        return recipes;
    }
}
