package com.alben.createhandmade.compat.jei;

import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;

public class ModJeiTypes {

    public static final RecipeType<RecipeHolder<CuttingRecipe>> HAND_SAW_CUTTING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "hand_saw_cutting"));

    /** 冲压锤 · 置物台 / 传送带 */
    public static final RecipeType<RecipeHolder<PressingRecipe>> HAND_PRESS_DEPOT =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "hand_press_depot"));

    /** 冲压锤 · 工作盆 */
    public static final RecipeType<RecipeHolder<BasinRecipe>> HAND_PRESS_BASIN =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "hand_press_basin"));
    /** 灌注枪 · 注液 */
    public static final RecipeType<RecipeHolder<FillingRecipe>> HAND_INFUSION_GUN =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "hand_infusion_gun"));
    /** 研钵 · 研磨 */
    public static final RecipeType<RecipeHolder<MillingRecipe>> MORTAR_MILLING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "mortar_milling"));

    /** 碾钵 · 粉碎 */
    public static final RecipeType<RecipeHolder<CrushingRecipe>> CRUSHER_MORTAR_CRUSHING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "crusher_mortar_crushing"));
    /** 搅拌杖 · 混合 */
    public static final RecipeType<RecipeHolder<BasinRecipe>> STIRRING_STAFF_MIXING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "stirring_staff_mixing"));
    /** 指杆 · 物品应用（同时包含 DEPLOYING 和 ITEM_APPLICATION） */
    public static final RecipeType<RecipeHolder<ItemApplicationRecipe>> POINTER_APPLICATION =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "pointer_application"));

    /** 风箱 · 熔炼 */
    public static final RecipeType<RecipeHolder<AbstractCookingRecipe>> BELLOWS_BLASTING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "bellows_blasting"));

    /** 风箱 · 烟熏 */
    public static final RecipeType<RecipeHolder<AbstractCookingRecipe>> BELLOWS_SMOKING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "bellows_smoking"));

    /** 风箱 · 缠魂 */
    public static final RecipeType<RecipeHolder<HauntingRecipe>> BELLOWS_HAUNTING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "bellows_haunting"));

    /** 风箱 · 洗涤 */
    public static final RecipeType<RecipeHolder<SplashingRecipe>> BELLOWS_SPLASHING =
            RecipeType.createRecipeHolderType(
                    ResourceLocation.fromNamespaceAndPath("create_hand_made", "bellows_splashing"));
}