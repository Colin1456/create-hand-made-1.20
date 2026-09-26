package com.alben.createhandmade.compat.jei;

import com.alben.createhandmade.CreateHandMade;
import com.alben.createhandmade.bellows.BellowsMediaRegistry;
import com.alben.createhandmade.compat.jei.category.BellowsCookingCategory;
import com.alben.createhandmade.compat.jei.category.BellowsHauntingCategory;
import com.alben.createhandmade.compat.jei.category.BellowsSplashingCategory;
import com.alben.createhandmade.compat.jei.category.CrusherMortarCrushingCategory;
import com.alben.createhandmade.compat.jei.category.HandInfusionGunCategory;
import com.alben.createhandmade.compat.jei.category.HandPressBasinCategory;
import com.alben.createhandmade.compat.jei.category.HandPressDepotCategory;
import com.alben.createhandmade.compat.jei.category.HandSawCategory;
import com.alben.createhandmade.compat.jei.category.MortarMillingCategory;
import com.alben.createhandmade.compat.jei.category.PointerApplicationCategory;
import com.alben.createhandmade.compat.jei.category.StirringStaffMixingCategory;
import com.alben.createhandmade.item.ModItems;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.compat.jei.CreateJEI;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.compat.jei.category.SpoutCategory;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.crusher.CrushingRecipe;
import com.simibubi.create.content.kinetics.deployer.ItemApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import com.simibubi.create.content.kinetics.fan.processing.HauntingRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.millstone.MillingRecipe;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.infrastructure.config.AllConfigs;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class CreateHandMadeJEI implements IModPlugin {

    private static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(CreateHandMade.MODID, "jei_plugin");

    private final List<CreateRecipeCategory<?>> allCategories = new ArrayList<>();
    private IIngredientManager ingredientManager;

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        allCategories.clear();   // ★ 照抄 Create
        // ==================== 手锯切削 ====================
        CreateRecipeCategory.Info<CuttingRecipe> handSawInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.HAND_SAW_CUTTING,
                Component.translatable("jei.create_hand_made.hand_saw"),
                new EmptyBackground(177, 55),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.HAND_SAW.get()),
                        () -> new ItemStack(Items.OAK_LOG)),
                CreateHandMadeJEI::collectHandSawRecipes,
                List.of(() -> new ItemStack(ModItems.HAND_SAW.get()))
        );
        allCategories.add(new HandSawCategory(handSawInfo));

        // ==================== 冲压锤 · 置物台 ====================
        CreateRecipeCategory.Info<PressingRecipe> handPressDepotInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.HAND_PRESS_DEPOT,
                Component.translatable("jei.create_hand_made.hand_press_depot"),
                new EmptyBackground(177, 55),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.PRESS_HAMMER.get()),
                        () -> new ItemStack(AllItems.IRON_SHEET.get())),
                CreateHandMadeJEI::collectPressingRecipes,
                List.of(() -> new ItemStack(ModItems.PRESS_HAMMER.get()))
        );
        allCategories.add(new HandPressDepotCategory(handPressDepotInfo));

        // ==================== 冲压锤 · 工作盆 ====================
        CreateRecipeCategory.Info<BasinRecipe> handPressBasinInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.HAND_PRESS_BASIN,
                Component.translatable("jei.create_hand_made.hand_press_basin"),
                new EmptyBackground(177, 103),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.PRESS_HAMMER.get()),
                        () -> new ItemStack(AllBlocks.BASIN.get())),
                CreateHandMadeJEI::collectCompactingRecipes,
                List.of(() -> new ItemStack(ModItems.PRESS_HAMMER.get()))
        );
        allCategories.add(new HandPressBasinCategory(handPressBasinInfo));

        // ==================== 灌注枪 · 注液 ====================
        CreateRecipeCategory.Info<FillingRecipe> handInfusionGunInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.HAND_INFUSION_GUN,
                Component.translatable("jei.create_hand_made.hand_infusion_gun"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.INFUSION_GUN.get()),
                        () -> new ItemStack(Items.WATER_BUCKET)),
                this::collectInfusionGunRecipes,
                List.of(() -> new ItemStack(ModItems.INFUSION_GUN.get()))
        );
        allCategories.add(new HandInfusionGunCategory(handInfusionGunInfo));

        // ==================== 研钵 · 研磨 ====================
        CreateRecipeCategory.Info<MillingRecipe> mortarMillingInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.MORTAR_MILLING,
                Component.translatable("jei.create_hand_made.mortar_milling"),
                new EmptyBackground(177, 55),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.MORTAR.get()),
                        () -> new ItemStack(AllItems.WHEAT_FLOUR.get())),
                CreateHandMadeJEI::collectMillingRecipes,
                List.of(() -> new ItemStack(ModItems.MORTAR.get()))
        );
        allCategories.add(new MortarMillingCategory(mortarMillingInfo));

        // ==================== 碾钵 · 粉碎 ====================
        CreateRecipeCategory.Info<CrushingRecipe> crusherMortarCrushingInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.CRUSHER_MORTAR_CRUSHING,
                Component.translatable("jei.create_hand_made.crusher_mortar_crushing"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.CRUSHER_MORTAR.get()),
                        () -> new ItemStack(AllItems.CRUSHED_GOLD.get())),
                CreateHandMadeJEI::collectCrushingRecipes,
                List.of(() -> new ItemStack(ModItems.CRUSHER_MORTAR.get()))
        );
        allCategories.add(new CrusherMortarCrushingCategory(crusherMortarCrushingInfo));

        // ==================== 搅拌杖 · 混合 ====================
        CreateRecipeCategory.Info<BasinRecipe> stirringStaffInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.STIRRING_STAFF_MIXING,
                Component.translatable("jei.create_hand_made.stirring_staff_mixing"),
                new EmptyBackground(177, 103),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.STIRRING_STAFF.get()),
                        () -> new ItemStack(AllBlocks.BASIN.get())),
                CreateHandMadeJEI::collectMixingRecipes,
                List.of(() -> new ItemStack(ModItems.STIRRING_STAFF.get()))
        );
        allCategories.add(new StirringStaffMixingCategory(stirringStaffInfo));

        // ==================== 指杆 · 应用（部署 + 物品应用） ====================
        CreateRecipeCategory.Info<ItemApplicationRecipe> pointerApplicationInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.POINTER_APPLICATION,
                Component.translatable("jei.create_hand_made.pointer_application"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.POINTER.get()),
                        () -> new ItemStack(AllBlocks.DEPOT.get())),
                CreateHandMadeJEI::collectPointerRecipes,
                List.of(() -> new ItemStack(ModItems.POINTER.get()))
        );
        allCategories.add(new PointerApplicationCategory(pointerApplicationInfo));

        // ==================== 风箱 · 熔炼 ====================
        CreateRecipeCategory.Info<AbstractCookingRecipe> bellowsBlastingInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.BELLOWS_BLASTING,
                Component.translatable("jei.create_hand_made.bellows_blasting"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.BELLOWS.get()),
                        () -> new ItemStack(Items.LAVA_BUCKET)),
                CreateHandMadeJEI::collectBellowsBlastingRecipes,
                List.of(() -> new ItemStack(ModItems.BELLOWS.get()))
        );
        allCategories.add(new BellowsCookingCategory(bellowsBlastingInfo,
                () -> BellowsMediaRegistry.getMediaFor(AllFanProcessingTypes.BLASTING)));

        // ==================== 风箱 · 烟熏 ====================
        CreateRecipeCategory.Info<AbstractCookingRecipe> bellowsSmokingInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.BELLOWS_SMOKING,
                Component.translatable("jei.create_hand_made.bellows_smoking"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.BELLOWS.get()),
                        () -> new ItemStack(Items.CAMPFIRE)),
                CreateHandMadeJEI::collectBellowsSmokingRecipes,
                List.of(() -> new ItemStack(ModItems.BELLOWS.get()))
        );
        allCategories.add(new BellowsCookingCategory(bellowsSmokingInfo,
                () -> BellowsMediaRegistry.getMediaFor(AllFanProcessingTypes.SMOKING)));

        // ==================== 风箱 · 缠魂 ====================
        CreateRecipeCategory.Info<HauntingRecipe> bellowsHauntingInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.BELLOWS_HAUNTING,
                Component.translatable("jei.create_hand_made.bellows_haunting"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.BELLOWS.get()),
                        () -> new ItemStack(Items.SOUL_CAMPFIRE)),
                CreateHandMadeJEI::collectBellowsHauntingRecipes,
                List.of(() -> new ItemStack(ModItems.BELLOWS.get()))
        );
        allCategories.add(new BellowsHauntingCategory(bellowsHauntingInfo,
                () -> BellowsMediaRegistry.getMediaFor(AllFanProcessingTypes.HAUNTING)));

        // ==================== 风箱 · 洗涤 ====================
        CreateRecipeCategory.Info<SplashingRecipe> bellowsSplashingInfo = new CreateRecipeCategory.Info<>(
                ModJeiTypes.BELLOWS_SPLASHING,
                Component.translatable("jei.create_hand_made.bellows_splashing"),
                new EmptyBackground(177, 70),
                new DoubleItemIcon(
                        () -> new ItemStack(ModItems.BELLOWS.get()),
                        () -> new ItemStack(Items.WATER_BUCKET)),
                CreateHandMadeJEI::collectBellowsSplashingRecipes,
                List.of(() -> new ItemStack(ModItems.BELLOWS.get()))
        );
        allCategories.add(new BellowsSplashingCategory(bellowsSplashingInfo,
                () -> BellowsMediaRegistry.getMediaFor(AllFanProcessingTypes.SPLASHING)));

        registration.addRecipeCategories(allCategories.toArray(CreateRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        this.ingredientManager = registration.getIngredientManager();
        allCategories.forEach(c -> c.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        allCategories.forEach(c -> c.registerCatalysts(registration));
    }

    // ==================== 配方收集 ====================

    private static List<RecipeHolder<CuttingRecipe>> collectHandSawRecipes() {
        List<RecipeHolder<CuttingRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.CUTTING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof CuttingRecipe c) {
                result.add(new RecipeHolder<>(h.id(), c));
            }
        }
        return result;
    }

    private static List<RecipeHolder<PressingRecipe>> collectPressingRecipes() {
        List<RecipeHolder<PressingRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.PRESSING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof PressingRecipe p) {
                result.add(new RecipeHolder<>(h.id(), p));
            }
        }
        return result;
    }

    /**
     * 冲压锤 · 工作盆。
     * 包含：
     *  - COMPACTING 类型配方（打包：如 4合1、9合1 的 CompactingRecipe）
     *  - 可压缩的 CraftingRecipe（4合1 / 9合1 的普通合成配方，参考 CreateJEI.autoSquare）
     */
    private static List<RecipeHolder<BasinRecipe>> collectCompactingRecipes() {
        List<RecipeHolder<BasinRecipe>> result = new ArrayList<>();

        // 1. COMPACTING 类型
        List<RecipeHolder<?>> compacting = CreateJEI.getTypedRecipes(AllRecipeTypes.COMPACTING.getType());
        for (RecipeHolder<?> h : compacting) {
            if (h.value() instanceof BasinRecipe b) {
                result.add(new RecipeHolder<>(h.id(), b));
            }
        }

        // 2. 可压缩的 CraftingRecipe（对应 CreateJEI 的 autoSquare 分类）
        if (!AllConfigs.server().recipes.allowShapedSquareInPress.get()) {
            return result;
        }

        List<RecipeHolder<?>> crafting = CreateJEI.getTypedRecipes(RecipeType.CRAFTING);
        for (RecipeHolder<?> h : crafting) {
            if (!(h.value() instanceof CraftingRecipe cr)) continue;
            if (cr instanceof MechanicalCraftingRecipe) continue;
            if (!MechanicalPressBlockEntity.canCompress(cr)) continue;
            if (AllRecipeTypes.shouldIgnoreInAutomation(h)) continue;

            result.add(BasinRecipe.convertShapeless(h));
        }

        return result;
    }

    private List<RecipeHolder<FillingRecipe>> collectInfusionGunRecipes() {
        List<RecipeHolder<FillingRecipe>> result = new ArrayList<>();

        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.FILLING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof FillingRecipe f) {
                result.add(new RecipeHolder<>(h.id(), f));
            }
        }

        if (ingredientManager != null) {
            SpoutCategory.consumeRecipes(result::add, ingredientManager);
        }

        return result;
    }

    private static List<RecipeHolder<MillingRecipe>> collectMillingRecipes() {
        List<RecipeHolder<MillingRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.MILLING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof MillingRecipe m) {
                result.add(new RecipeHolder<>(h.id(), m));
            }
        }
        return result;
    }

    private static List<RecipeHolder<CrushingRecipe>> collectCrushingRecipes() {
        List<RecipeHolder<CrushingRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.CRUSHING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof CrushingRecipe c) {
                result.add(new RecipeHolder<>(h.id(), c));
            }
        }
        return result;
    }

    private static List<RecipeHolder<BasinRecipe>> collectMixingRecipes() {
        List<RecipeHolder<BasinRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.MIXING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof BasinRecipe b) {
                result.add(new RecipeHolder<>(h.id(), b));
            }
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<RecipeHolder<ItemApplicationRecipe>> collectPointerRecipes() {
        List<RecipeHolder<ItemApplicationRecipe>> result = new ArrayList<>();

        // DEPLOYING
        List<RecipeHolder<?>> deploying = CreateJEI.getTypedRecipes(AllRecipeTypes.DEPLOYING.getType());
        for (RecipeHolder<?> h : deploying) {
            if (h.value() instanceof ItemApplicationRecipe r) {
                result.add(new RecipeHolder<>(h.id(), r));
            }
        }

        // ITEM_APPLICATION
        List<RecipeHolder<?>> itemApp = CreateJEI.getTypedRecipes(AllRecipeTypes.ITEM_APPLICATION.getType());
        for (RecipeHolder<?> h : itemApp) {
            if (h.value() instanceof ItemApplicationRecipe r) {
                result.add(new RecipeHolder<>(h.id(), r));
            }
        }

        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<RecipeHolder<AbstractCookingRecipe>> collectBellowsBlastingRecipes() {
        List<RecipeHolder<AbstractCookingRecipe>> result = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        if (level == null) return result;

        for (var h : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
            result.add((RecipeHolder) h);
        }
        for (var h : level.getRecipeManager().getAllRecipesFor(RecipeType.BLASTING)) {
            result.add((RecipeHolder) h);
        }
        return result;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static List<RecipeHolder<AbstractCookingRecipe>> collectBellowsSmokingRecipes() {
        List<RecipeHolder<AbstractCookingRecipe>> result = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        if (level == null) return result;

        for (var h : level.getRecipeManager().getAllRecipesFor(RecipeType.SMOKING)) {
            result.add((RecipeHolder) h);
        }
        return result;
    }

    private static List<RecipeHolder<HauntingRecipe>> collectBellowsHauntingRecipes() {
        List<RecipeHolder<HauntingRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.HAUNTING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof HauntingRecipe r) {
                result.add(new RecipeHolder<>(h.id(), r));
            }
        }
        return result;
    }

    private static List<RecipeHolder<SplashingRecipe>> collectBellowsSplashingRecipes() {
        List<RecipeHolder<SplashingRecipe>> result = new ArrayList<>();
        List<RecipeHolder<?>> all = CreateJEI.getTypedRecipes(AllRecipeTypes.SPLASHING.getType());
        for (RecipeHolder<?> h : all) {
            if (h.value() instanceof SplashingRecipe r) {
                result.add(new RecipeHolder<>(h.id(), r));
            }
        }
        return result;
    }
}