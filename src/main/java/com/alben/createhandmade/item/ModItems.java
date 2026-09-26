package com.alben.createhandmade.item;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;

import java.util.List;

import static com.alben.createhandmade.CreateHandMade.REGISTRATE;

public class ModItems {

    public static final ItemEntry<PressHammerItem> PRESS_HAMMER = REGISTRATE
            .item("press_hammer", PressHammerItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(256)

                    .component(DataComponents.TOOL, new Tool(
                            List.of(),
                            1,
                            1
                    ))
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "press_hammer_damage"),
                                            8.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "press_hammer_speed"),
                                            -3.1,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())
            )
            .register();

    public static final ItemEntry<MortarItem> MORTAR = REGISTRATE
            .item("mortar", MortarItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(256))
            .register();

    public static final ItemEntry<CrusherMortarItem> CRUSHER_MORTAR = REGISTRATE
            .item("crusher_mortar", CrusherMortarItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(512))
            .register();

    public static final ItemEntry<PointerItem> POINTER = REGISTRATE
            .item("pointer", PointerItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(512)
                    .component(DataComponents.TOOL, new Tool(
                            List.of(),
                            1,
                            1
                    ))
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "pointer_damage"),
                                            3.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "pointer_speed"),
                                            -1.8,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            // ★ 方块交互距离 +2（放置、挖掘、与方块交互）
                            .add(Attributes.BLOCK_INTERACTION_RANGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "pointer_block_reach"),
                                            2.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            // ★ 实体交互距离 +2（攻击、与实体交互）
                            .add(Attributes.ENTITY_INTERACTION_RANGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "pointer_entity_reach"),
                                            2.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())
            )
            .register();

    public static final ItemEntry<StirringStaffItem> STIRRING_STAFF = REGISTRATE
            .item("stirring_staff", StirringStaffItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(256)
                    .component(DataComponents.TOOL, new Tool(
                            List.of(),
                            1,
                            1
                    ))
                    // ★ 新增攻击属性：中量武器
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "stirring_staff_damage"),
                                            5.0,   // +5 → 总 6.0，接近铁剑（+5 总 6）
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "stirring_staff_speed"),
                                            -2.5,  // -2.5 → 最终 1.5，和剑速（1.6）接近
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ENTITY_INTERACTION_RANGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "stirring_staff_reach"),
                                            1.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())
            )
            .register();
    public static final ItemEntry<BellowsItem> BELLOWS = REGISTRATE
            .item("bellows", BellowsItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(256))
            .register();

    public static final ItemEntry<InfusionGunItem> INFUSION_GUN = REGISTRATE
            .item("infusion_gun", InfusionGunItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(256))
            .register();
    public static final ItemEntry<HandSawItem> HAND_SAW = REGISTRATE
            .item("hand_saw", HandSawItem::new)
            .properties(p -> p.stacksTo(1)
                    .durability(512)
                    .component(DataComponents.TOOL, new Tool(
                            List.of(
                                    // ★ 给"可斧头挖掘"的方块 6.0 挖掘速度（= 铁斧）
                                    Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_AXE, 6.0f)
                            ),
                            1.0f,
                            1
                    ))
                    .attributes(ItemAttributeModifiers.builder()
                            .add(Attributes.ATTACK_DAMAGE,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "hand_saw_damage"),
                                            7.0,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .add(Attributes.ATTACK_SPEED,
                                    new AttributeModifier(
                                            ResourceLocation.fromNamespaceAndPath("create_hand_made", "hand_saw_speed"),
                                            -3.1,
                                            AttributeModifier.Operation.ADD_VALUE),
                                    EquipmentSlotGroup.MAINHAND)
                            .build())
            )
            .register();

    public static void register() {
    }
}