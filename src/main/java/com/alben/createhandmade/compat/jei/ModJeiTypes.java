package com.alben.createhandmade.compat.jei;

import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public class ModJeiTypes {

    public static final RecipeType<RecipeHolder<CuttingRecipe>> HAND_SAW_CUTTING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "hand_saw_cutting"));

    /** 冲压锤 · 置物台 / 传送带 */
    public static final RecipeType<RecipeHolder<PressingRecipe>> HAND_PRESS_DEPOT =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "hand_press_depot"));

    /** 冲压锤 · 工作盆 · 打包（COMPACTING） */
    public static final RecipeType<RecipeHolder<BasinRecipe>> HAND_PRESS_BASIN =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "hand_press_basin"));

    /** 冲压锤 · 工作盆 · 自动摆放（4/9 合 1） */
    public static final RecipeType<RecipeHolder<BasinRecipe>> HAND_PRESS_BASIN_AUTO_SQUARE =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "hand_press_basin_auto_square"));

    /** 灌注枪 · 注液 */
    public static final RecipeType<RecipeHolder<FillingRecipe>> HAND_INFUSION_GUN =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "hand_infusion_gun"));

    /** 研钵 · 研磨 */
    public static final RecipeType<RecipeHolder<MillingRecipe>> MORTAR_MILLING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "mortar_milling"));

    /** 碾钵 · 粉碎 */
    public static final RecipeType<RecipeHolder<CrushingRecipe>> CRUSHER_MORTAR_CRUSHING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "crusher_mortar_crushing"));

    /** 搅拌杖 · 混合（MIXING） */
    public static final RecipeType<RecipeHolder<BasinRecipe>> STIRRING_STAFF_MIXING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "stirring_staff_mixing"));

    /** 搅拌杖 · 自动无序合成 */
    public static final RecipeType<RecipeHolder<BasinRecipe>> STIRRING_STAFF_AUTO_SHAPELESS =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "stirring_staff_auto_shapeless"));

    /** 搅拌杖 · 自动酿造 */
    public static final RecipeType<RecipeHolder<BasinRecipe>> STIRRING_STAFF_AUTO_BREWING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "stirring_staff_auto_brewing"));

    /** 指杆 · 物品应用 */
    public static final RecipeType<RecipeHolder<ItemApplicationRecipe>> POINTER_APPLICATION =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "pointer_application"));

    /** 风箱 · 熔炼 */
    public static final RecipeType<RecipeHolder<AbstractCookingRecipe>> BELLOWS_BLASTING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "bellows_blasting"));

    /** 风箱 · 烟熏 */
    public static final RecipeType<RecipeHolder<AbstractCookingRecipe>> BELLOWS_SMOKING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "bellows_smoking"));

    /** 风箱 · 缠魂 */
    public static final RecipeType<RecipeHolder<HauntingRecipe>> BELLOWS_HAUNTING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "bellows_haunting"));

    /** 风箱 · 洗涤 */
    public static final RecipeType<RecipeHolder<SplashingRecipe>> BELLOWS_SPLASHING =
            RecipeType.createRecipeHolderType(
                    new ResourceLocation("create_hand_made", "bellows_splashing"));
}