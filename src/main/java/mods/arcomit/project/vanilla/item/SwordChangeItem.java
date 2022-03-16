package mods.arcomit.project.vanilla.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

/**
 * @Author Arcomit
 * @Update 2022/03/16-Arcomit
 */
public class SwordChangeItem extends SwordItem {
    private final float attackDamage;
    private final float attackReach;
    public final Multimap<Attribute, AttributeModifier> defaultModifiers;
    //用于修改原版的剑
    public SwordChangeItem(Tier pTier, int pAttackDamageModifier, float pAttackSpeedModifier, float pAttackReachModifier , Item.Properties pProperties){
        super(pTier,pAttackDamageModifier,pAttackSpeedModifier,pProperties);
        attackDamage = pAttackDamageModifier;
        attackReach = pAttackReachModifier;
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        //伤害
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", pAttackDamageModifier, AttributeModifier.Operation.ADDITION));
        //攻击速度
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", pAttackSpeedModifier, AttributeModifier.Operation.ADDITION));
        // 攻击距离
        this.defaultModifiers = builder.build();
    }
    //修改伤害用的
    @Override
    public float getDamage() {
        return this.attackDamage;
    }

    public float getReach() {
        return this.attackReach;
    }

    //修改属性用的
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }
}
