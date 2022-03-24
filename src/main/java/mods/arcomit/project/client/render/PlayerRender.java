package mods.arcomit.project.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mods.arcomit.project.Project1;
import mods.arcomit.project.animation.controller.entity.PlayerController;
import mods.arcomit.project.client.model.PlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
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
 * @Update 2022/03/24-Arcomit
 * 用与修改玩家渲染
 */
public class PlayerRender extends ReplacedEntityRenderer{
    public PlayerRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlayerModel(), new PlayerController(null));

    }

    //用于获取玩家模型格式
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

    //用于获取玩家皮肤
    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof AbstractClientPlayer){
            return ((AbstractClientPlayer) entity).getSkinTextureLocation();
        }
        return super.getTextureLocation(entity);
    }

    public ItemStack mainHand;
    public ItemStack offHand;
    public ItemStack helmet;
    public ItemStack chestplate;
    public ItemStack leggings;
    public ItemStack boots;
    public MultiBufferSource rtb;
    public ResourceLocation whTexture;
    public float partialTicks;
    public Player player;

    @Override
    public void renderEarly(Object animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                            int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        if (animatable instanceof Player){
            this.player = (Player) animatable;
            this.mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);
            this.offHand = player.getItemBySlot(EquipmentSlot.OFFHAND);
            this.helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            this.chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
            this.leggings = player.getItemBySlot(EquipmentSlot.LEGS);
            this.boots = player.getItemBySlot(EquipmentSlot.FEET);
            this.rtb = renderTypeBuffer;
            this.whTexture = this.getTextureLocation(player);
            this.partialTicks = partialTicks;
        }
        super.renderEarly(animatable,stackIn,ticks,renderTypeBuffer,vertexBuilder,packedLightIn,packedOverlayIn,red,green,blue,packedLightIn);
    }


    @Override
    public void renderRecursively(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
                                  int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.pushPose();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

        //渲染身上物品
        stack.pushPose();
        renderItem(bone,stack,packedLightIn,packedOverlayIn);
        stack.popPose();

        bufferIn = rtb.getBuffer(getRenderType(player,whTexture));

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
        switch (bone.getName()){
            //右手
            case "RightItem":
                stack.pushPose();
                stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
                stack.mulPose(Vector3f.XP.rotationDegrees(-75));
                stack.mulPose(Vector3f.YP.rotationDegrees(0));
                stack.mulPose(Vector3f.ZP.rotationDegrees(0));

                stack.scale(1.0f, 1.0f, 1.0f);
                this.renderStatic(mainHand, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                        packedLightIn, packedOverlayIn, stack, this.rtb, 0);
                stack.popPose();
                break;
            //左手
            case "LeftItem":
                stack.pushPose();
                stack.translate(bone.getPivotX() / 16, bone.getPivotY() / 16, bone.getPivotZ() / 16);
                stack.mulPose(Vector3f.XP.rotationDegrees(-75));
                stack.mulPose(Vector3f.YP.rotationDegrees(0));
                stack.mulPose(Vector3f.ZP.rotationDegrees(0));

                stack.scale(1.0f, 1.0f, 1.0f);

                this.renderStatic(offHand, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                        packedLightIn, packedOverlayIn, stack, this.rtb, 0);
                stack.popPose();
                break;
        }


    }

    public void renderStatic(ItemStack itemStack, ItemTransforms.TransformType type, int packedLightIn, int packedOverlayIn, PoseStack stack, MultiBufferSource multiBufferSource, int pSeed) {
        boolean b = false;
        if (type == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || type == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND){
            b = true;
        }
        Minecraft.getInstance().getItemRenderer().renderStatic((LivingEntity)null, itemStack, type, b, stack, multiBufferSource, (Level)null, packedLightIn, packedOverlayIn, pSeed);
    }

    @Override
    public RenderType getRenderType(Entity entity,ResourceLocation textureLocation){
        if ((entity).isSpectator()){
            return RenderType.itemEntityTranslucentCull(textureLocation);
        }
        return RenderType.entityCutout(textureLocation);
    }

}
