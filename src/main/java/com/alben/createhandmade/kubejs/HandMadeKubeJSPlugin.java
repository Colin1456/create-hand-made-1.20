package com.alben.createhandmade.kubejs;

import com.alben.createhandmade.recipe.HandMadeRecipeFilters;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.ScriptManager;

/**
 * KubeJS 插件入口 —— 把 {@link HandMadeEvents#TOOL_FILTER} 事件组暴露给脚本作者。
 *
 * <p>由 KubeJS 通过 jar 根的 {@code kubejs.plugins.txt} 发现：
 * KubeJS 会 {@code Class.forName} 本类并调用 <b>public 无参构造</b>实例化，
 * 所以这个构造必须存在（下面显式声明）。</p>
 *
 * <p><b>为什么在 {@code afterScriptsLoaded} 里派发：</b>
 * {@code ScriptManager.reload()} 的末尾会调用它（{@code ScriptManager.java:69}），
 * 而 {@code reload()} 在每次服务器数据包重载
 * （{@code ServerScriptManager.createPackResources} → {@code manager.reload()}）
 * 以及 {@code /kubejs reload} 命令时都会执行 —— 也就是说<b>每次脚本重载都会触发一次</b>，
 * 不是只首次。</p>
 *
 * <p><b>为什么先清空再派发：</b>KubeJS 脚本只在脚本重载时求值一次，
 * 如果只依赖作者脚本顶层的一次性调用，那么下一次脚本重载（或数据包重载）之后
 * 之前注册的项就会失效。这里每次重载先 {@link HandMadeRecipeFilters#clearKubeJS()}，
 * 再重放一次作者的 {@code toolFilter} 回调，于是「脚本里的声明」等价于
 * 「每次重载都重新声明」，语义可预测。</p>
 *
 * <p>数据包来源与 KubeJS 来源在 {@link HandMadeRecipeFilters} 里是两份独立的表，
 * 因此本类的执行顺序与 {@code HandMadeRecipeFilterLoader}（数据包重载）之间
 * 不存在任何时序依赖。</p>
 */
public class HandMadeKubeJSPlugin implements KubeJSPlugin {

    /** KubeJS 通过反射实例化本插件，需要一个 public 无参构造。 */
    public HandMadeKubeJSPlugin() {
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(HandMadeEvents.GROUP);
    }

    @Override
    public void afterScriptsLoaded(ScriptManager manager) {
        HandMadeRecipeFilters.clearKubeJS();

        // hasListeners() 为假时直接跳过：postInternal 本来也会早退，这里只是省掉一次事件对象分配
        if (HandMadeEvents.TOOL_FILTER.hasListeners()) {
            HandMadeEvents.TOOL_FILTER.post(new HandMadeToolFilterKubeEvent());
        }
    }
}
