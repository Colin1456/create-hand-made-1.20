package com.alben.createhandmade.recipe;

/**
 * 「手工工具 + 配方类型」的组合枚举。
 *
 * <p>本模组的 8 个手工工具里，冲压锤与搅拌杖各自有多个来源不同的配方集合
 * （例如冲压锤既能压工作盆、又能压置物台/传送带；搅拌杖既能混合、又能跑自动无序合成与自动酿造），
 * 因此这里的枚举粒度是「工具 + 配方类型」而不是「工具」。</p>
 *
 * <p>每个枚举常量对应 {@link HandMadeRecipePool} 里的一个私有收集方法，
 * 也对应游戏内工具的一条查询路径（以及 JEI 的一个类别，风箱除外）。</p>
 *
 * <p><b>不属于本枚举的工具：</b>风箱（BellowsItem）。风箱走 Create 的
 * {@code FanProcessingType} / {@code AllFanProcessingTypes}，本身已经是干净的聚合入口，
 * 本次重构不改动它，也不为它建立 Pool 条目。</p>
 */
public enum HandMadeTool {

    // ==================== 冲压锤 (PressHammerItem) ====================

    /**
     * 冲压锤 · 工作盆 · 压缩（COMPACTING）。
     * 游戏内对应 {@code PressHammerItem.tryPressBasin} 中
     * {@code recipe.getType() == AllRecipeTypes.COMPACTING} 那一半。
     */
    PRESS_HAMMER_BASIN,

    /**
     * 冲压锤 · 工作盆 · 自动摆放（4/9 合 1 的可压缩工作台配方）。
     * 游戏内对应 {@code PressHammerItem.tryPressBasin} 中
     * 「可压缩 CraftingRecipe」那一半。
     */
    PRESS_HAMMER_AUTO_SQUARE,

    /**
     * 冲压锤 · 置物台 / 传送带（PRESSING）。
     * 游戏内对应 {@code PressHammerItem.findPressingRecipe}。
     */
    PRESS_HAMMER_DEPOT,

    // ==================== 研钵 / 碾钵 ====================

    /**
     * 研钵 · 研磨（MILLING）。
     * 游戏内对应 {@code MortarItem#use} 与 {@code MortarItem#finishUsingItem}
     * 里的 {@code AllRecipeTypes.MILLING.find(...)}。
     */
    MORTAR,

    /**
     * 碾钵 · 先粉碎后研磨（CRUSHING 优先，MILLING 兜底）。
     * 游戏内对应 {@code CrusherMortarItem.findRecipe}：
     * 先查 CRUSHING，匹配不到再查 MILLING。
     * Pool 返回的列表保证「CRUSHING 在前、MILLING 在后」，调用方取第一个匹配即可保持优先级。
     */
    CRUSHER_MORTAR,

    // ==================== 手锯 ====================

    /**
     * 手锯 · 切削（CUTTING）。
     * 游戏内对应 {@code HandSawItem.getCuttingRecipes}。
     */
    HAND_SAW,

    // ==================== 搅拌杖 (StirringStaffItem) ====================

    /**
     * 搅拌杖 · 混合（MIXING）。
     * 游戏内对应 {@code StirringStaffItem.findMatchingRecipe} 的第 1 步里
     * 「MIXING 类型」那一半。
     */
    STIRRING_STAFF,

    /**
     * 搅拌杖 · 自动无序合成（非 Shaped、多原料、不可压缩的工作台配方）。
     * 游戏内对应 {@code StirringStaffItem.findMatchingRecipe} 的第 1 步里
     * 「无序合成」那一半。
     */
    STIRRING_STAFF_AUTO_SHAPELESS,

    /**
     * 搅拌杖 · 自动酿造（PotionMixingRecipes）。
     * 游戏内对应 {@code StirringStaffItem.findMatchingRecipe} 的第 2 步
     * （该步逻辑保持不变，Pool 的这条主要用于 JEI 类别）。
     */
    STIRRING_STAFF_AUTO_BREWING,

    // ==================== 指杆 / 灌注枪 ====================

    /**
     * 指杆 · 应用（DEPLOYING + ITEM_APPLICATION）。
     * 游戏内对应 {@code PointerItem.findRecipe}：先部署，再物品应用。
     * Pool 返回的列表保证「DEPLOYING 在前、ITEM_APPLICATION 在后」。
     */
    POINTER,

    /**
     * 灌注枪 · 注液（FILLING）。
     * 游戏内对应 {@code InfusionGunItem.findFillingRecipe}。
     */
    INFUSION_GUN
}
