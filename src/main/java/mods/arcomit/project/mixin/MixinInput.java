package mods.arcomit.project.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * @Author Arcomit
 * @Update 2022/04/03-Arcomit
 * 修改疾跑判断
 */
@Mixin(Input.class)
public class MixinInput {

    @Shadow
    public float leftImpulse;
    @Shadow
    public float forwardImpulse;

    public boolean hasForwardImpulse() {
        return this.forwardImpulse != 0.0f || leftImpulse != 0.0f;//Arcomit:只要运动中即可
    }
}
