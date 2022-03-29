package mods.arcomit.project.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static mods.arcomit.project.vanilla.event.PlayerRenderEvent.*;

/**
 * @Author Arcomit
 * @Update 2022/03/25-Arcomit
 * 用于更真实的第一人称
 */
@Mixin(LevelRenderer.class)
public class MixinWorldRender {
    @Final
    @Shadow
    private Minecraft minecraft;

    @Final
    @Shadow
    private RenderBuffers renderBuffers;

    @Final
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;checkPoseStack(Lcom/mojang/blaze3d/vertex/PoseStack;)V", ordinal = 0))
    public void render(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera,
                       GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        //判断是第一人称
        if(!camera.isDetached()){
            //判断是否为本地客户端玩家(自己)
            if(camera.getEntity() == minecraft.player){
                Player player = (Player) camera.getEntity();
                //判断是否为观察者和是否在睡觉
                if ((!player.isSpectator()) && (!player.isSleeping())) {
                    Vec3 vec3d = camera.getPosition();
                    double d0 = Mth.lerp((double)tickDelta, player.xOld, player.getX()) - vec3d.x();
                    double d1 = Mth.lerp((double)tickDelta, player.yOld, player.getY()) - vec3d.y();
                    double d2 = Mth.lerp((double)tickDelta, player.zOld, player.getZ()) - vec3d.z();
                    Vec3 vec3 = playerRender.getRenderOffset(player, tickDelta);
                    double d3 = d0 + vec3.x();
                    double d4 = d1 + vec3.y();
                    double d5 = d2 + vec3.z();
                    matrices.pushPose();
                    matrices.translate(d3, d4, d5);
                    MultiBufferSource.BufferSource immediate = this.renderBuffers.bufferSource();
                    float yaw = Mth.lerp(tickDelta, player.yRotO, player.getYRot());
                    matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F - yaw));
                    //如果玩家是摄像机实体,同时摄像机为第一人称
                    matrices.translate(0,0,0.35);
                    //渲染实体
                    firstPersonRender.render(playerControllerHashMap.get(player.getUUID()),yaw,tickDelta,matrices, immediate, this.entityRenderDispatcher.getPackedLightCoords(player, tickDelta));
                    matrices.popPose();
                }
            }
        }
    }
}
