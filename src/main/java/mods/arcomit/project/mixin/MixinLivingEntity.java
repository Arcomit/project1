package mods.arcomit.project.mixin;

import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @Author Arcomit
 * @Update 2022/03/17-Arcomit
 * 用与修改LivingEntity类
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Shadow
    protected void hurtArmor(DamageSource pDamageSource, float pDamage) {}

    @Shadow
    public abstract int getArmorValue();

    //修改护甲减伤机制为1:1减伤
    @Overwrite
    protected float getDamageAfterArmorAbsorb(DamageSource pSource, float pDamage) {
        if (!pSource.isBypassArmor()) {
            this.hurtArmor(pSource, pDamage);
            pDamage = pDamage - ((float)this.getArmorValue());
        }
        return pDamage;
    }
}
