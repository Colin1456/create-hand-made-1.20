package com.alben.createhandmade.kubejs;

import com.alben.createhandmade.recipe.HandMadeRecipeFilterLoader;
import com.alben.createhandmade.recipe.HandMadeRecipeFilters;
import com.alben.createhandmade.recipe.HandMadeTool;
import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceLocation;

import java.util.Locale;

/**
 * 派发给脚本作者的配方过滤事件。
 *
 * <p>脚本里的两个方法：</p>
 * <pre>
 * HandMadeEvents.toolFilter(event =&gt; {
 *     event.disable('hand_press_basin', 'create:compacting/andesite_alloy_from_zinc');
 *     event.disableByMod('mortar', 'thermal');
 * });
 * </pre>
 *
 * <p>{@link KubeEvent} 是<b>接口</b>（全部方法都有 default 实现），所以本类不需要
 * 调用任何 {@code super(...)} 构造，也没有必须实现的方法 —— 公开方法即为脚本可见的 API。</p>
 *
 * <p><b>与数据包加载器的错误处理不同：</b>数据包文件写错只记 warning 并跳过
 * （一个坏文件不该阻断整次 reload）；而脚本里写错工具 id 是作者笔误，
 * 应当<b>立刻抛异常</b>让作者在控制台看见，因此这里抛
 * {@link IllegalArgumentException}，不静默跳过。</p>
 */
public class HandMadeToolFilterKubeEvent implements KubeEvent {

    public HandMadeToolFilterKubeEvent() {
    }

    /**
     * 精确禁用某个工具下的一条配方。
     *
     * @param toolId   工具 id，即 {@link HandMadeTool} 常量名的小写下划线形式
     *                 （例如 {@code hand_press_basin}）
     * @param recipeId 配方 id（例如 {@code create:compacting/andesite_alloy_from_zinc}）
     * @throws IllegalArgumentException 工具 id 不认识时
     */
    public void disable(String toolId, String recipeId) {
        HandMadeRecipeFilters.disableFromKubeJS(parseTool(toolId), ResourceLocation.parse(recipeId));
    }

    /**
     * 禁用某个工具下、某个 mod namespace 的全部配方。
     *
     * @param toolId 工具 id（同上）
     * @param modId  配方 id 的 namespace（例如 {@code thermal}）
     * @throws IllegalArgumentException 工具 id 不认识时
     */
    public void disableByMod(String toolId, String modId) {
        HandMadeRecipeFilters.disableByModFromKubeJS(parseTool(toolId), modId);
    }

    /**
     * 把脚本给的 {@code tool_id} 解析成 {@link HandMadeTool}。
     *
     * <p>与 {@link HandMadeRecipeFilterLoader} 里那份的区别只在错误处理：
     * 那边返回 null 并由调用方记 warning 后跳过，这边直接抛异常。</p>
     *
     * @throws IllegalArgumentException 无法匹配任何 {@link HandMadeTool} 常量时
     */
    private static HandMadeTool parseTool(String toolId) {
        try {
            return HandMadeTool.valueOf(toolId.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Unknown HandMade tool id '" + toolId + "'. Valid ids: "
                            + HandMadeRecipeFilterLoader.validToolIds(), ex);
        }
    }
}
