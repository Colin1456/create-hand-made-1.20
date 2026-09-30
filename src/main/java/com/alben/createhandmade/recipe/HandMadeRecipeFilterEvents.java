package com.alben.createhandmade.recipe;

import com.alben.createhandmade.CreateHandMade;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

/**
 * 把 L2 的数据包加载器挂到 NeoForge 事件总线上。
 *
 * <p>注册方式沿用项目现有风格（见 {@code BellowsMediaEvents}）：
 * {@link EventBusSubscriber} 注解 + {@code @SubscribeEvent} 静态方法，
 * 注册到<b>游戏总线</b>（{@code NeoForge.EVENT_BUS}）—— {@link AddReloadListenerEvent}
 * 正是游戏总线事件。每次数据包重载（含 {@code /reload}）都会触发一次。</p>
 */
@EventBusSubscriber(modid = CreateHandMade.MODID)
public class HandMadeRecipeFilterEvents {

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new HandMadeRecipeFilterLoader());
    }
}
