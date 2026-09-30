package com.alben.createhandmade.recipe;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 配方过滤层 L2 —— 「禁用某个工具的某条配方」的注册表。
 *
 * <p><b>职责边界：仅做配方集合过滤，不参与配方匹配。</b>
 * 本类只记录「哪些配方不该被本模组的工具看到」，具体剔除由
 * {@link HandMadeRecipePool#getBaseRecipes} 在收集完成后统一执行。
 * 它永远不会判断某条配方是否匹配某份输入 —— 匹配一律由调用方
 * （各工具自己的私有 helper）用配方自己的 {@code matches} 完成。</p>
 *
 * <p><b>只影响本模组的工具，不影响 Create 原版机器。</b>
 * 被禁用的配方仍然完整地留在 {@code RecipeManager} 里，Create 的机器照常使用它；
 * 只有 {@link HandMadeRecipePool} 这条读取路径会把它们剔除。</p>
 *
 * <p><b>两个互相独立的来源</b>（各自一份表，{@link #isDisabled} 查并集）：</p>
 * <ol>
 *   <li><b>数据包</b>：{@code data/<namespace>/create_hand_made/tool_filter/<tool_id>.json}，
 *       由 {@link HandMadeRecipeFilterLoader} 在<b>每次数据包重载</b>时
 *       {@link #clearDataPack()} 后重新灌入。格式：
 *       <pre>
 * {
 *   "disabled": ["create:compacting/andesite_alloy_from_zinc"],
 *   "disabled_by_mod": ["thermal", "mekanism"]
 * }
 *       </pre>
 *       两个字段都可缺省。{@code disabled} 精确匹配 recipe id；
 *       {@code disabled_by_mod} 按 recipe id 的 namespace 匹配。</li>
 *   <li><b>KubeJS</b>：脚本里的 {@code HandMadeEvents.toolFilter(event => ...)}，
 *       由 {@code HandMadeKubeJSPlugin.afterScriptsLoaded} 在<b>每次脚本重载</b>时
 *       {@link #clearKubeJS()} 后重放回调再灌入。</li>
 * </ol>
 *
 * <p>之所以分成两份表而不是共用一份：脚本只在「脚本重载」时求值，而数据包过滤在
 * 「数据包重载」时清空，两者时机不同。分开存储后，谁先谁后都不会互相清掉对方的项，
 * 时序耦合被彻底消除。</p>
 *
 * <p><b>线程安全：</b>用 {@link ConcurrentHashMap} + {@link ConcurrentHashMap#newKeySet()}。
 * 正常时序下读写在同一条主线程上，但这里不依赖这一点。</p>
 *
 * <p><b>硬性约束：</b>本类不允许 import 任何 {@code mezz.jei.*}，
 * 也不允许 import 任何 KubeJS 类（KubeJS 侧只是<b>调用</b>本类，
 * 方向是 {@code kubejs} 包 → {@code recipe} 包）。它是纯服务端可用的逻辑，
 * JEI 只是它的调用方之一。</p>
 */
public final class HandMadeRecipeFilters {

    /** 数据包来源：按「工具 + 精确 recipe id」禁用的表。 */
    private static final Map<HandMadeTool, Set<ResourceLocation>> DATAPACK_BY_ID = new ConcurrentHashMap<>();

    /** 数据包来源：按「工具 + mod namespace」整片禁用的表。 */
    private static final Map<HandMadeTool, Set<String>> DATAPACK_BY_MOD = new ConcurrentHashMap<>();

    /** KubeJS 来源：按「工具 + 精确 recipe id」禁用的表。 */
    private static final Map<HandMadeTool, Set<ResourceLocation>> KUBEJS_BY_ID = new ConcurrentHashMap<>();

    /** KubeJS 来源：按「工具 + mod namespace」整片禁用的表。 */
    private static final Map<HandMadeTool, Set<String>> KUBEJS_BY_MOD = new ConcurrentHashMap<>();

    private HandMadeRecipeFilters() {
    }

    // ==================== 数据包来源 ====================

    /**
     * 清空「数据包来源」的全部过滤项。数据包重载开始时先调用，然后由
     * {@link HandMadeRecipeFilterLoader} 重新灌入。
     *
     * <p>只清数据包那一半，不影响 KubeJS 来源 —— 两个来源各自独立重载，
     * 因此它们之间的执行顺序无关紧要。</p>
     */
    public static void clearDataPack() {
        DATAPACK_BY_ID.clear();
        DATAPACK_BY_MOD.clear();
    }

    /**
     * 数据包来源：精确禁用某个工具下的一条配方。
     *
     * @param tool     工具 + 配方类型组合
     * @param recipeId 要禁用的配方 id（例如 {@code create:compacting/andesite_alloy_from_zinc}）
     */
    public static void disableFromDataPack(HandMadeTool tool, ResourceLocation recipeId) {
        DATAPACK_BY_ID.computeIfAbsent(tool, key -> ConcurrentHashMap.newKeySet()).add(recipeId);
    }

    /**
     * 数据包来源：禁用某个工具下、某个 mod namespace 的全部配方。
     *
     * @param tool  工具 + 配方类型组合
     * @param modId 配方 id 的 namespace（例如 {@code thermal}）
     */
    public static void disableByModFromDataPack(HandMadeTool tool, String modId) {
        DATAPACK_BY_MOD.computeIfAbsent(tool, key -> ConcurrentHashMap.newKeySet()).add(modId);
    }

    // ==================== KubeJS 来源 ====================

    /**
     * 清空「KubeJS 来源」的全部过滤项。
     *
     * <p>由 {@code HandMadeKubeJSPlugin.afterScriptsLoaded} 在每次脚本重载时调用，
     * 紧接着重放脚本里的 {@code HandMadeEvents.toolFilter} 回调 —— 这样脚本里
     * 写的禁用项不会因为「脚本只求值一次」而在后续重载中丢失。</p>
     */
    public static void clearKubeJS() {
        KUBEJS_BY_ID.clear();
        KUBEJS_BY_MOD.clear();
    }

    /**
     * KubeJS 来源：精确禁用某个工具下的一条配方。
     *
     * <p>由 {@code HandMadeToolFilterKubeEvent.disable(String, String)} 调用，
     * 因此 {@code tool} 一定非 null、{@code recipeId} 已经过 id 校验。</p>
     */
    public static void disableFromKubeJS(HandMadeTool tool, ResourceLocation recipeId) {
        KUBEJS_BY_ID.computeIfAbsent(tool, key -> ConcurrentHashMap.newKeySet()).add(recipeId);
    }

    /**
     * KubeJS 来源：禁用某个工具下、某个 mod namespace 的全部配方。
     */
    public static void disableByModFromKubeJS(HandMadeTool tool, String modId) {
        KUBEJS_BY_MOD.computeIfAbsent(tool, key -> ConcurrentHashMap.newKeySet()).add(modId);
    }

    // ==================== 查询 ====================

    /**
     * 判断某条配方在某个工具下是否被禁用。
     *
     * <p>按顺序查四个表，任一命中即返回 true：</p>
     * <ol>
     *   <li>数据包 · 精确 id</li>
     *   <li>数据包 · namespace</li>
     *   <li>KubeJS · 精确 id</li>
     *   <li>KubeJS · namespace</li>
     * </ol>
     *
     * @param tool     工具 + 配方类型组合
     * @param recipeId 待判定的配方 id
     * @return 是否应把这条配方从该工具的候选列表里剔除
     */
    public static boolean isDisabled(HandMadeTool tool, ResourceLocation recipeId) {
        Set<ResourceLocation> datapackIds = DATAPACK_BY_ID.get(tool);
        if (datapackIds != null && datapackIds.contains(recipeId)) {
            return true;
        }

        Set<String> datapackMods = DATAPACK_BY_MOD.get(tool);
        if (datapackMods != null && datapackMods.contains(recipeId.getNamespace())) {
            return true;
        }

        Set<ResourceLocation> kubejsIds = KUBEJS_BY_ID.get(tool);
        if (kubejsIds != null && kubejsIds.contains(recipeId)) {
            return true;
        }

        Set<String> kubejsMods = KUBEJS_BY_MOD.get(tool);
        return kubejsMods != null && kubejsMods.contains(recipeId.getNamespace());
    }
}
