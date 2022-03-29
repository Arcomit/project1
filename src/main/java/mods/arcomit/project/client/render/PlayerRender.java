package mods.arcomit.project.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mods.arcomit.project.Project1;
import mods.arcomit.project.animation.controller.entity.PlayerController;
import mods.arcomit.project.animation.controller.entity.ReplacedLivingController;
import mods.arcomit.project.client.model.PlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.util.RenderUtils;


/**
 * @Author Arcomit
 * @Update 2022/03/29-Arcomit Refactoring
 * 用与修改玩家渲染
 */
public class PlayerRender extends ReplacedLivingRenderer {
    private boolean firstPerson=false;
    public PlayerRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlayerModel(), new PlayerController(null));
    }

    public PlayerRender(EntityRendererProvider.Context renderManager,boolean firstPerson) {
        super(renderManager, new PlayerModel(), new PlayerController(null));
        this.firstPerson = firstPerson;
    }


    @Override
    protected void applyRotations(LivingEntity entityLiving, PoseStack matrixStackIn, float rotationYaw, float partialTicks) {
        if (firstPerson){
            float f = Mth.rotLerp(partialTicks, entityLiving.yBodyRotO, entityLiving.yBodyRot);
            float f2 = Mth.rotLerp(partialTicks, entityLiving.yHeadRotO, entityLiving.yHeadRot);
            float f3 = f-f2;
            super.applyRotations(entityLiving, matrixStackIn, 180F + f3, partialTicks);
        }else {
            super.applyRotations(entityLiving, matrixStackIn, rotationYaw, partialTicks);
        }
    }

    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
                                  int packedOverlayIn, float red, float green, float blue, float alpha) {
        //第一人称不渲染手,头
        if (bone.getName().equals("RightArmBone") || bone.getName().equals("LeftArmBone") || bone.getName().equals("HeadBone")) {
            if (firstPerson){
                return;
            }
        }

        stack.pushPose();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        //渲染手上的物品
        stack.pushPose();
        renderItem(bone,stack,packedLightIn,packedOverlayIn);
        stack.popPose();

        bufferIn = rtb.getBuffer(getRenderType(living,whTexture));

        if (!bone.isHidden) {
            for (GeoCube cube : bone.childCubes) {
                stack.pushPose();
                renderCube(cube, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
                stack.popPose();
            }
            for (GeoBone childBone : bone.childBones) {
                renderRecursively(childBone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
            }
        }
        stack.popPose();
    }


    public void renderItem(GeoBone bone, PoseStack stack, int packedLightIn,
                           int packedOverlayIn){
        if (bone.getName().equals("RightItem") || bone.getName().equals("LeftItem")){
            stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
            stack.mulPose(Vector3f.XP.rotationDegrees(-75));
            if (bone.getName().equals("RightItem")){
                this.renderStatic(mainHand, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                        packedLightIn, packedOverlayIn, stack, this.rtb, 0);
            }

            if (bone.getName().equals("LeftItem")){
                this.renderStatic(offHand, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                        packedLightIn, packedOverlayIn, stack, this.rtb, 0);
            }
        }
    }


    //获取渲染类型
    @Override
    public RenderType getRenderType(Entity entity,ResourceLocation textureLocation){
        if ((entity).isSpectator()){
            return RenderType.itemEntityTranslucentCull(textureLocation);
        }
        return RenderType.entityCutout(textureLocation);
    }

    //获取玩家模型格式
    @Override
    public ResourceLocation getModelLocation(Entity entity) {
        if (entity instanceof AbstractClientPlayer){
            AbstractClientPlayer player = ((AbstractClientPlayer) entity);
            if (player.getModelName().equals("slim")){
                return new ResourceLocation(Project1.MODID, "geo/player_slim.geo.json");
            }
        }
        return super.getModelLocation(entity);
    }

    //获取玩家皮肤
    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof AbstractClientPlayer){
            return ((AbstractClientPlayer) entity).getSkinTextureLocation();
        }
        return super.getTextureLocation(entity);
    }
}
