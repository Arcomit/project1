package mods.arcomit.project.mixin;

import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @Author Arcomit
 * @Update 2022/04/03-Arcomit
 * 修改疾跑判断
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer {
    @Shadow
    public Input input;

    @Overwrite
    private boolean hasEnoughImpulseToStartSprinting() {
        return this.isUnderWater() ? this.input.hasForwardImpulse() : (double)this.input.forwardImpulse >= 0.8D || (double)this.input.forwardImpulse <= -0.8D || (double)this.input.leftImpulse >= 0.8D || (double)this.input.leftImpulse <= -0.8D;//Arcomit:只要疾跑中即可
    }

    @Shadow
    protected abstract boolean isUnderWater();
}
