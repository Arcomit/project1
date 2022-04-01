package mods.arcomit.project.animation.render;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mods.arcomit.project.animation.controller.entity.ReplacedLivingController;
import mods.arcomit.project.animation.event.ReplacedLivingAnimationEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;
import software.bernie.geckolib3.compat.PatchouliCompat;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author Arcomit
 * @Update 2022/03/29-Arcomit Refactoring
 * 用与修改原版实体的渲染
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ReplacedLivingRenderer<T extends ReplacedLivingController> extends EntityRenderer implements IGeoRenderer {
    //默认模型获取器
    private final AnimatedGeoModel<IAnimatable> modelProvider;
    //默认动画控制器,不设定动画控制器的话则使用该控制器
    private final T defaultController;
    //Layer层渲染 如:玩家的盔甲,玩家被射中时身上插的箭等
    protected final List<GeoLayerRenderer> layerRenderers = Lists.newArrayList();

    //当前动画控制器
    private IAnimatable currentController;

    /**
     * @Class<? extends IAnimatable> 默认控制器的类型
     * @ReplacedEntityRenderer 该类型控制器的渲染
     */
    private static final Map<Class<? extends IAnimatable>, ReplacedLivingRenderer> renderers = new ConcurrentHashMap<>();
    /**
     * return该控制器对应的渲染的模型获取器
     * 详细见https://github.com/bernie-g/geckolib/wiki/Custom-Model-Providers
     */
    static {
        AnimationController.addModelFetcher((IAnimatable object) -> {
            ReplacedLivingRenderer renderer = getRenderer(object.getClass());
            return renderer == null ? null : renderer.getGeoModelProvider();
        });
    }

    /**
     * @renderManager 渲染管理器
     * @modelProvider 默认模型获取器
     * @defaultController 默认动画控制器
     * Render初始化
     */
    public ReplacedLivingRenderer(EntityRendererProvider.Context renderManager,
                                  AnimatedGeoModel<IAnimatable> modelProvider, T defaultController) {
        super(renderManager);
        this.modelProvider = modelProvider;
        this.defaultController = defaultController;
        //根据默认动画控制器类型添加对应的渲染(因此,非使用默认控制器的时应使用同一类创建的实例)
        if (!renderers.containsKey(defaultController.getClass())) {
            renderers.put(defaultController.getClass(), this);
        }
    }

    //根据控制器类型获取对应的渲染
    public static ReplacedLivingRenderer getRenderer(Class<? extends IAnimatable> controllerType) {
        return renderers.get(controllerType);
    }

    /**
     * @entity 渲染的实体
     * @entityYaw 实体Yaw(欧拉角)
     * 使用默认动画控制器
     */
    @Override
    public void render(Entity entity, float entityYaw, float partialTicks, PoseStack matrix,
                       MultiBufferSource buffer, int light) {
        if (entity instanceof LivingEntity){
            this.defaultController.living = (LivingEntity) entity;
            this.render(this.defaultController, entityYaw, partialTicks, matrix, buffer, light);
        }throw (new RuntimeException("Replaced renderer was not an instanceof LivingEntity"));
    }

    /**
     * @entity 渲染的实体
     * @controller 动画控制器
     * @entityYaw 实体Yaw(欧拉角)
     * 使用其他动画控制器
     */
    @SuppressWarnings("resource")
    public void render(ReplacedLivingController controller, float entityYaw, float partialTicks, PoseStack matrix,
                       MultiBufferSource buffer, int light) {
        this.currentController = controller;//Arcomit:设置当前的动画控制器

        LivingEntity livingEntity = controller.living;

        matrix.pushPose();
        //判断实体是否坐下
        boolean isSit = livingEntity.isPassenger()
                && (livingEntity.getVehicle() != null &&
                livingEntity.getVehicle().shouldRiderSit());

        //返回给模型获取器的数据
        EntityModelData entityModelData = this.getEntityModelData(livingEntity);
        entityModelData.isSitting = isSit;
        entityModelData.isChild = livingEntity.isBaby();

        //Arcomit:防止实体潜行时模型下沉
        if (livingEntity.isCrouching()){
            matrix.translate(0F,0.125D,0);
        }

        //Arcomit:rotLerp平滑过渡
        float bodyRot = Mth.rotLerp(partialTicks, livingEntity.yBodyRotO, livingEntity.yBodyRot);
        float headRot = Mth.rotLerp(partialTicks, livingEntity.yHeadRot, livingEntity.yHeadRot);
        float headYaw = headRot - bodyRot;//身体也会旋转,headRot包括了身体的旋转,如果不减去身体的旋转会鬼畜
        //Arcmoit:坐着别的实体时的旋转
        if (isSit && livingEntity.getVehicle() instanceof LivingEntity){
            LivingEntity vehicle = (LivingEntity) livingEntity.getVehicle();
            bodyRot = Mth.rotLerp(partialTicks,vehicle.yBodyRotO,vehicle.yBodyRot);
            headYaw = headRot - bodyRot;
            float headRot2 = Mth.wrapDegrees(headYaw);
            if (headRot2 < -85.0F) {
                headRot2 = -85.0F;
            }

            if (headRot2 >= 85.0F) {
                headRot2 = 85.0F;
            }

            bodyRot = headRot - headRot2;
            if (headRot2 * headRot2 > 2500.0F) {
                bodyRot += headRot2 * 0.2F;
            }

            headYaw = headRot - bodyRot;
        }
        float headPitch = Mth.lerp(partialTicks, livingEntity.getXRot(), livingEntity.getXRot());
        entityModelData.headPitch = -headPitch;
        entityModelData.netHeadYaw = -headYaw;
        if (livingEntity.getPose() == Pose.SLEEPING) {
            Direction direction = livingEntity.getBedOrientation();
            if (direction != null) {
                float eyeHeight = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;
                matrix.translate((float) (-direction.getStepX()) * eyeHeight, 0.0D, (float) (-direction.getStepZ()) * eyeHeight);
            }
        }

        this.applyRotations(livingEntity, matrix, bodyRot, partialTicks);

        float limbSwingAmount = 0.0F;
        float limbSwing = 0.0F;
        if (!isSit && livingEntity.isAlive()) {
            limbSwingAmount = Mth.lerp(partialTicks, livingEntity.animationSpeedOld, livingEntity.animationSpeed);
            limbSwing = livingEntity.animationPosition - livingEntity.animationSpeed * (1.0F - partialTicks);
            if (livingEntity.isBaby()) {
                limbSwing *= 3.0F;
            }

            if (limbSwingAmount > 1.0F) {
                limbSwingAmount = 1.0F;
            }
        }

        //获取模型
        ReplacedLivingAnimationEvent predicate = new ReplacedLivingAnimationEvent(controller, limbSwing, limbSwingAmount, partialTicks,Collections.singletonList(entityModelData));
        GeoModel model = modelProvider.getModel(this.getModelLocation(livingEntity));
        if (modelProvider instanceof IAnimatableModel) {
            ((IAnimatableModel) modelProvider).setLivingAnimations(controller, this.getUniqueID(livingEntity), predicate);
        }
        //GeoModel model = modelProvider.getModel(this.getModelLocation(livingEntity));

        matrixChange(matrix);

        RenderSystem.setShaderTexture(0, getTextureLocation(livingEntity));
        Color renderColor = getRenderColor(controller, partialTicks, matrix, buffer, null, light);
        boolean spectator = livingEntity.isSpectator();
        RenderType renderType = this.getRenderType(livingEntity,getTextureLocation(livingEntity));
        boolean invis = livingEntity.isInvisibleTo(Minecraft.getInstance().player);


        render(model, livingEntity, partialTicks, renderType, matrix, buffer, null, light,
                getPackedOverlay(livingEntity, this.getOverlayProgress(livingEntity, partialTicks)),
                (float) renderColor.getRed() / 255f, (float) renderColor.getGreen() / 255f,
                (float) renderColor.getBlue() / 255f, invis ? 0F : (float) renderColor.getAlpha() / 255);

        if (!spectator) {
            for (GeoLayerRenderer layerRenderer : this.layerRenderers) {
                layerRenderer.render(matrix, buffer, light, livingEntity, limbSwing, limbSwingAmount, partialTicks,
                        livingEntity.tickCount + partialTicks, headYaw, headPitch);
            }
        }
        if (ModList.get().isLoaded("patchouli")) {
            PatchouliCompat.patchouliLoaded(matrix);
        }
        matrix.popPose();
        super.render(livingEntity, entityYaw, partialTicks, matrix, buffer, light);
    }


    public ItemStack mainHand;
    public ItemStack offHand;
    public MultiBufferSource rtb;
    public ResourceLocation whTexture;
    public LivingEntity living;
    @Override
    public void renderEarly(Object animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                            int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        if (animatable instanceof LivingEntity){
            this.living = (LivingEntity) animatable;
            this.mainHand = living.getItemBySlot(EquipmentSlot.MAINHAND);
            this.offHand = living.getItemBySlot(EquipmentSlot.OFFHAND);
            this.rtb = renderTypeBuffer;
            this.whTexture = this.getTextureLocation(living);
        }
        IGeoRenderer.super.renderEarly(animatable,stackIn,ticks,renderTypeBuffer,vertexBuilder,packedLightIn,packedOverlayIn,red,green,blue,packedLightIn);
    }

    protected float getOverlayProgress(LivingEntity livingEntityIn, float partialTicks) {
        return 0.0F;
    }

    public static int getPackedOverlay(LivingEntity livingEntityIn, float uIn) {
        return OverlayTexture.pack(OverlayTexture.u(uIn),
                OverlayTexture.v(livingEntityIn.hurtTime > 0 || livingEntityIn.deathTime > 0));
    }

    protected void applyRotations(LivingEntity entityLiving, PoseStack matrixStackIn,
                                  float rotationYaw, float partialTicks) {
        Pose pose = entityLiving.getPose();
        if (pose != Pose.SLEEPING) {
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F - rotationYaw));
        }

        if (entityLiving.deathTime > 0) {
            float f = ((float) entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
            f = Mth.sqrt(f);
            if (f > 1.0F) {
                f = 1.0F;
            }

            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(f * this.getDeathMaxRotation(entityLiving)));
        } else if (entityLiving.isAutoSpinAttack()) {
            matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(-90.0F - entityLiving.getXRot()));
            matrixStackIn
                    .mulPose(Vector3f.YP.rotationDegrees(((float) entityLiving.tickCount + partialTicks) * -75.0F));
        } else if (pose == Pose.SLEEPING) {
            Direction direction = entityLiving.getBedOrientation();
            float f1 = direction != null ? getFacingAngle(direction) : rotationYaw;
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
            matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(this.getDeathMaxRotation(entityLiving)));
            matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(270.0F));
        } else if (entityLiving.hasCustomName() || entityLiving instanceof Player) {
            String s = ChatFormatting.stripFormatting(entityLiving.getName().getString());
            if (("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof Player)
                    || ((Player) entityLiving).isModelPartShown(PlayerModelPart.CAPE))) {
                matrixStackIn.translate(0.0D, entityLiving.getBbHeight() + 0.1F, 0.0D);
                matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            }
        }

    }

    private static float getFacingAngle(Direction facingIn) {
        switch (facingIn) {
            case SOUTH:
                return 90.0F;
            case WEST:
                return 0.0F;
            case NORTH:
                return 270.0F;
            case EAST:
                return 180.0F;
            default:
                return 0.0F;
        }
    }

    //死亡旋转角度
    protected float getDeathMaxRotation(LivingEntity entityLivingBaseIn) {
        return 90.0F;
    }

    @Override
    public boolean shouldShowName(Entity entity) {
        double d0 = this.entityRenderDispatcher.distanceToSqr(entity);
        float f = entity.isDiscrete() ? 32.0F : 64.0F;
        if (d0 >= (double) (f * f)) {
            return false;
        } else {
            return entity == this.entityRenderDispatcher.crosshairPickEntity && entity.hasCustomName();
        }
    }

    public void matrixChange(PoseStack matrix){
        matrix.translate(0, 0.01f, 0);
    }

    public EntityModelData getEntityModelData(LivingEntity entity){
        return new EntityModelData();
    }

    //模型获取器
    @Override
    public AnimatedGeoModel getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return getTextureLocation(currentController);
    }

    //获取默认贴图
    @Override
    public ResourceLocation getTextureLocation(Object instance) {
        return this.modelProvider.getTextureLocation((IAnimatable) instance);
    }

    public ResourceLocation getModelLocation(Entity entity) {
        return getModelLocation(currentController);
    }

    //获取默认模型
    public ResourceLocation getModelLocation(Object instance) {
        return this.modelProvider.getModelLocation((IAnimatable) instance);
    }

    public RenderType getRenderType(Entity entity,ResourceLocation textureLocation){
        return RenderType.entityCutout(textureLocation);
    }

    public void renderStatic(ItemStack itemStack, ItemTransforms.TransformType type, int packedLightIn, int packedOverlayIn, PoseStack stack, MultiBufferSource multiBufferSource, int pSeed) {
        boolean b = false;
        if (type == ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND || type == ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND){
            b = true;
        }
        Minecraft.getInstance().getItemRenderer().renderStatic((LivingEntity)null, itemStack, type, b, stack, multiBufferSource, (Level)null, packedLightIn, packedOverlayIn, pSeed);
    }

    //添加Layer层渲染
    public final boolean addLayer(GeoLayerRenderer<? extends LivingEntity> layer) {
        return this.layerRenderers.add(layer);
    }
}
