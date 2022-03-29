package mods.arcomit.project.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @Author Arcomit
 * @Update 2022/03/18-Arcomit
 * 用与修改属性上限下限
 */
@Mixin(RangedAttribute.class)
public class MixinRangedAttribute {
    @Final
    @Shadow
    private double minValue;

    @Final
    @Shadow
    private double maxValue;

    /**
     * @author Arcomit
     */
    @Overwrite
    public double getMinValue() {
        return Double.MIN_VALUE;
    }

    /**
     * @author Arcomit
     */
    @Overwrite
    public double getMaxValue() {
        return Double.MAX_VALUE;
    }

    /**
     * @author Arcomit
     */
    @Overwrite
    public double sanitizeValue(double pValue) {
        return Mth.clamp(pValue, getMinValue(), getMaxValue());
    }
}
