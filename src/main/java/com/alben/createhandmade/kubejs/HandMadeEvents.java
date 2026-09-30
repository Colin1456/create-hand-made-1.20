package com.alben.createhandmade.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * 本模组向 KubeJS 脚本暴露的事件组。
 *
 * <p>脚本作者用法：</p>
 * <pre>
 * HandMadeEvents.toolFilter(event =&gt; {
 *     event.disable('press_hammer_basin', 'create:compacting/andesite_alloy_from_zinc');
 *     event.disableByMod('mortar', 'thermal');
 * });
 * </pre>
 *
 * <p>JS 里能这样写的原因：KubeJS 会把每个已注册事件组按
 * {@code group.name} 绑成全局对象（{@code BuiltinKubeJSPlugin.registerBindings}
 * 里的 {@code bindings.add(group.name, new EventGroupWrapper(...))}），
 * 而 {@code EventHandler extends BaseFunction}，所以 {@code 组名.事件名(回调)}
 * 就是注册监听。</p>
 *
 * <p>用 {@code server} 而不是 {@code common}/{@code startup}：配方过滤只对
 * 服务端权威数据有意义，且 {@code ScriptType.SERVER} 实现了
 * {@code ScriptTypeHolder}，因此 {@code TOOL_FILTER.post(event)} 不需要额外传 ScriptType。</p>
 */
public interface HandMadeEvents {

    EventGroup GROUP = EventGroup.of("HandMadeEvents");

    /**
     * 配方过滤事件。每次服务器脚本重载时由
     * {@link HandMadeKubeJSPlugin#afterScriptsLoaded} 派发一次。
     */
    EventHandler TOOL_FILTER = GROUP.server("toolFilter", () -> HandMadeToolFilterKubeEvent.class);
}
