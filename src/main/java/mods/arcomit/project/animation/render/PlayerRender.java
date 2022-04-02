package mods.arcomit.project.animation.render;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import mods.arcomit.project.Project1;
import mods.arcomit.project.animation.controller.entity.PlayerController;
import mods.arcomit.project.animation.model.PlayerModel;
import mods.arcomit.project.json.ArmorCustom;
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
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static mods.arcomit.project.util.FileLoader.getResourceAsString;


/**
 * @Author Arcomit
 * @Update 2022/03/29-Arcomit Refactoring
 * 用与修改玩家渲染
 */
public class PlayerRender extends ReplacedLivingRenderer {
    public boolean firstPerson=false;
    public PlayerRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlayerModel(), new PlayerController(null));
    }

    public PlayerRender(EntityRendererProvider.Context renderManager,boolean firstPerson) {
        this(renderManager);
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


    public AbstractClientPlayer player;
    @Override
    public void renderEarly(Object animatable, PoseStack stackIn, float ticks,
                            MultiBufferSource renderTypeBuffer, VertexConsumer vertexBuilder, int packedLightIn,
                            int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        if (animatable instanceof AbstractClientPlayer){
            player = (AbstractClientPlayer) animatable;
        }
        super.renderEarly(animatable,stackIn,ticks,renderTypeBuffer,vertexBuilder,packedLightIn,packedOverlayIn,red,green,blue,packedLightIn);
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
        if (bone.getName().equals("RightItem") || bone.getName().equals("LeftItem")){
            stack.pushPose();
            renderItem(bone,stack,packedLightIn,packedOverlayIn);
            stack.popPose();
        }

        //渲染盔甲
        if (bone.getName().equals("Head") || bone.getName().equals("BodyUp")
                || bone.getName().equals("BodyMiddle") || bone.getName().equals("BodyDown")
                || bone.getName().equals("RightUpperArm") || bone.getName().equals("RightLowerArm")
                || bone.getName().equals("LeftUpperArm") || bone.getName().equals("LeftLowerArm")
                || bone.getName().equals("LeftUpperLeg") || bone.getName().equals("LeftLowerLeg")
                || bone.getName().equals("RightUpperLeg") || bone.getName().equals("RighyLowerLeg")
        ){
            renderArmor(bone,stack,packedLightIn,packedOverlayIn);
        }

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


    //初始化判断
    private static boolean armorCustomInitialize = false;
    //用于自定义盔甲模型贴图的JSON
    private static final ResourceLocation armorCustomJson = new ResourceLocation(Project1.MODID, "jsons/armor_custom.json");

    //贴图资源
    public static final ConcurrentHashMap<String, ResourceLocation> textureResources = new ConcurrentHashMap<>(20);
    //模型资源-粗
    public static final ConcurrentHashMap<String, ResourceLocation> modelResources = new ConcurrentHashMap<>(20);
    //模型资源-细
    public static final ConcurrentHashMap<String, ResourceLocation> modelSlimResources = new ConcurrentHashMap<>(20);

    public void renderArmor(GeoBone bone,PoseStack stack,int light,
                            int packedOverlayIn){
        //初始化
        if (!armorCustomInitialize){
            List<ArmorCustom> ArmorCustomList = new Gson().fromJson(getResourceAsString(armorCustomJson), new TypeToken<List<ArmorCustom>>(){}.getType());
            for (ArmorCustom armorCustom : ArmorCustomList){
                for (String string : armorCustom.getItemName()){
                    if (!armorCustom.getTextureResource().equals("")){
                        textureResources.put(string,new ResourceLocation(armorCustom.getTextureResource()));
                    }
                    if (!armorCustom.getModelResource().equals("")){
                        modelResources.put(string,new ResourceLocation(armorCustom.getModelResource()));
                    }
                    if (!armorCustom.getModelSlimResource().equals("")){
                        modelSlimResources.put(string,new ResourceLocation(armorCustom.getModelSlimResource()));
                    }
                }
            }
            armorCustomInitialize = true;
        }

        if (bone.getName().equals("Head")){
            renderPartArmor(helmet,bone,stack,light,packedOverlayIn);
        }
        if (bone.getName().equals("BodyUp")
                || bone.getName().equals("BodyMiddle") || bone.getName().equals("BodyDown")
                || bone.getName().equals("RightUpperArm") || bone.getName().equals("RightLowerArm")
                || bone.getName().equals("LeftUpperArm") || bone.getName().equals("LeftLowerArm")){
            renderPartArmor(chestplate,bone,stack,light,packedOverlayIn);
        }
        if (bone.getName().equals("LeftUpperLeg") || bone.getName().equals("LeftLowerLeg")
                || bone.getName().equals("RightUpperLeg") || bone.getName().equals("RighyLowerLeg")){
            renderPartArmor(leggings,bone,stack,light,packedOverlayIn);
        }
        if (bone.getName().equals("LeftLowerLeg") || bone.getName().equals("RighyLowerLeg")){
            renderPartArmor(boots,bone,stack,light,packedOverlayIn);
        }

    }

    private ResourceLocation modelResource;
    private ResourceLocation textureResource;
    public ItemStack helmetOld;
    public ItemStack chestplateOld;
    public ItemStack leggingsOld;
    public ItemStack bootsOld;
    private RenderType helmetType;
    private RenderType chestType;
    private RenderType legsType;
    private RenderType feetType;
    private GeoModel helmetModel;
    private GeoModel chestModel;
    private GeoModel legsModel;
    private GeoModel feetModel;
    public void renderPartArmor(ItemStack armor,GeoBone bone,PoseStack stack,int light,
                                int packedOverlayIn){
        if (armor.getItem() != null && armor.getItem() instanceof ArmorItem){
            ArmorItem armorItem = (ArmorItem) armor.getItem();
            String armorName = armorItem.getRegistryName().toString();
            modelResource = modelResources.get(armorName);
            if (player.getModelName().equals("slim")){
                if (modelSlimResources.get(armorName) != null){
                    modelResource = modelSlimResources.get(armorName);
                }
            }
            textureResource = textureResources.get(armorName);
            if (modelResource != null && textureResource != null){
                EquipmentSlot armorType = armorItem.getSlot();
                if (armorType == EquipmentSlot.HEAD){
                    if (helmetOld != armor){
                        helmetOld=armor;
                        if (helmetType != RenderType.armorCutoutNoCull(textureResource)){
                            helmetType = RenderType.armorCutoutNoCull(textureResource);
                        }
                        if (helmetModel != GeckoLibCache.getInstance().getGeoModels().get(modelResource)){
                            helmetModel = GeckoLibCache.getInstance().getGeoModels().get(modelResource);
                        }
                    }
                    if (bone.getName().equals("Head")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(helmetType);
                        renderBone(helmetModel.getBone("Helmet").get(),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                    }
                }
                if (armorType == EquipmentSlot.CHEST){
                    if (chestplateOld != armor){
                        chestplateOld=armor;
                        if (chestType != RenderType.armorCutoutNoCull(textureResource)){
                            chestType = RenderType.armorCutoutNoCull(textureResource);
                        }
                        if (chestModel != GeckoLibCache.getInstance().getGeoModels().get(modelResource)){
                            chestModel = GeckoLibCache.getInstance().getGeoModels().get(modelResource);
                        }
                    }
                    GeoBone chest = chestModel.getBone("Chestplate").get();
                    if (bone.getName().equals("BodyUp")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestUp") != null){
                            renderBone(getChildBone(chest,"ChestUp"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("BodyMiddle")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestMiddle") != null){
                            renderBone(getChildBone(chest,"ChestMiddle"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("BodyDown")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestDown") != null){
                            renderBone(getChildBone(chest,"ChestDown"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("RightUpperArm")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestRightUpArm") != null){
                            renderBone(getChildBone(chest,"ChestRightUpArm"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("RightLowerArm")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestRightDownArm") != null){
                            renderBone(getChildBone(chest,"ChestRightDownArm"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                    }
                    if (bone.getName().equals("LeftUpperArm")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestLeftUpperArm") != null){
                            renderBone(getChildBone(chest,"ChestLeftUpperArm"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("LeftLowerArm")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(chestType);
                        if (getChildBone(chest,"ChestLeftDownArm") != null){
                            renderBone(getChildBone(chest,"ChestLeftDownArm"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                }
                if (armorType == EquipmentSlot.LEGS){
                    if (leggingsOld != armor){
                        leggingsOld=armor;
                        if (legsType != RenderType.armorCutoutNoCull(textureResource)){
                            legsType = RenderType.armorCutoutNoCull(textureResource);
                        }
                        if (legsModel != GeckoLibCache.getInstance().getGeoModels().get(modelResource)){
                            legsModel = GeckoLibCache.getInstance().getGeoModels().get(modelResource);
                        }
                    }
                    GeoBone chest = legsModel.getBone("Leggings").get();
                    if (bone.getName().equals("LeftUpperLeg")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(legsType);
                        if (getChildBone(chest,"LeggingsLeftUpLeg") != null){
                            renderBone(getChildBone(chest,"LeggingsLeftUpLeg"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("LeftLowerLeg")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(legsType);
                        if (getChildBone(chest,"LeggingsLeftDownLeg") != null){
                            renderBone(getChildBone(chest,"LeggingsLeftDownLeg"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("RightUpperLeg")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(legsType);
                        if (getChildBone(chest,"LeggingsRightUpLeg") != null){
                            renderBone(getChildBone(chest,"LeggingsRightUpLeg"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("RighyLowerLeg")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(legsType);
                        if (getChildBone(chest,"LeggingsRightDownLeg") != null){
                            renderBone(getChildBone(chest,"LeggingsRightDownLeg"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                }
                if (armorType == EquipmentSlot.FEET){
                    if (bootsOld != armor){
                        bootsOld=armor;
                        if (feetType != RenderType.armorCutoutNoCull(textureResource)){
                            feetType = RenderType.armorCutoutNoCull(textureResource);
                        }
                        if (feetModel != GeckoLibCache.getInstance().getGeoModels().get(modelResource)){
                            feetModel = GeckoLibCache.getInstance().getGeoModels().get(modelResource);
                        }
                    }
                    GeoBone chest = feetModel.getBone("Boots").get();
                    if (bone.getName().equals("LeftLowerLeg")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(feetType);
                        if (getChildBone(chest,"BootsLeftDownLeg") != null){
                            renderBone(getChildBone(chest,"BootsLeftDownLeg"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                    if (bone.getName().equals("RighyLowerLeg")){
                        VertexConsumer vertexBuilder = rtb.getBuffer(legsType);
                        if (getChildBone(chest,"BootsRightDownLeg") != null){
                            renderBone(getChildBone(chest,"BootsRightDownLeg"),stack,vertexBuilder,light,packedOverlayIn,1,1,1,1);
                        }
                        return;
                    }
                }

            }

        }
    }

    public GeoBone getChildBone(GeoBone bone,String name){
        for (GeoBone geoBone : bone.childBones){
            if (geoBone.getName().equals(name)){
                return geoBone;
            }
        }
        return null;
    }

    public void renderItem(GeoBone bone, PoseStack stack, int packedLightIn,
                           int packedOverlayIn){
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

    @Override
    public void matrixChange(PoseStack matrix){
        super.matrixChange(matrix);
        matrix.scale(0.9135F,0.9135F,0.9135F);//Arcomit:缩小玩家模型使之与碰撞箱大小匹配
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


    public void renderBone(GeoBone bone, PoseStack stack, VertexConsumer bufferIn, int packedLightIn,
                                  int packedOverlayIn, float red, float green, float blue, float alpha) {
        stack.pushPose();
        RenderUtils.translate(bone, stack);
        RenderUtils.moveToPivot(bone, stack);
        RenderUtils.rotate(bone, stack);
        RenderUtils.scale(bone, stack);
        RenderUtils.moveBackFromPivot(bone, stack);

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
}
