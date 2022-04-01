package mods.arcomit.project.animation.render.layer;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mods.arcomit.project.Project1;
import mods.arcomit.project.animation.render.PlayerRender;
import mods.arcomit.project.json.ArmorCustom;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoCube;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.RenderUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static mods.arcomit.project.util.FileLoader.getResourceAsString;
/**
 * @Author Arcomit
 * @Update 2022/04/1-Arcomit
 * 盔甲渲染层
 */
public class PlyaerArmorLayer extends GeoLayerRenderer {
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

    public PlyaerArmorLayer(IGeoRenderer<Player> entityRendererIn) {
        super(entityRendererIn);
    }

    public ItemStack helmet;
    public ItemStack chestplate;
    public ItemStack leggings;
    public ItemStack boots;
    @Override
    public void render(PoseStack matrix, MultiBufferSource buffer, int light,
                       Entity entity, float limbSwing, float limbSwingAmount,
                       float partialTicks, float ageInTicks, float netHeadYaw,
                       float headPitch) {
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

        if (entity instanceof AbstractClientPlayer){
            AbstractClientPlayer player = (AbstractClientPlayer) entity;
            this.helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            this.chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
            this.leggings = player.getItemBySlot(EquipmentSlot.LEGS);
            this.boots = player.getItemBySlot(EquipmentSlot.FEET);

            renderArmor(player,helmet,matrix,buffer,light);
            renderArmor(player,chestplate,matrix,buffer,light);
            renderArmor(player,leggings,matrix,buffer,light);
            renderArmor(player,boots,matrix,buffer,light);
        }
    }


    private ResourceLocation modelResource;
    private ResourceLocation textureResource;
    public void renderArmor(AbstractClientPlayer player, ItemStack armor,
                            PoseStack matrix, MultiBufferSource buffer, int light){
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
                RenderType renderType = RenderType.armorCutoutNoCull(textureResource);
                GeoModel model = this.getEntityModel().getModel(modelResource);
                VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
                matrix.pushPose();
                for (GeoBone group : model.topLevelBones) {
                    renderRecursively(group,matrix,vertexBuilder,light, OverlayTexture.NO_OVERLAY,1,1,1,1,armorType);
                }

                matrix.popPose();
            }
        }
    }


    public void renderRecursively(GeoBone bone, PoseStack matrix, VertexConsumer buffer, int light,
                                  int packedOverlayIn, float red, float green, float blue, float alpha,EquipmentSlot armorType) {
        //第一人称不渲染手,头
        if (bone.getName().equals("RightArmBone") || bone.getName().equals("LeftArmBone") || bone.getName().equals("HeadBone")) {
            if (this.getRenderer() instanceof PlayerRender){
                PlayerRender render = (PlayerRender) this.getRenderer();
                if (render.firstPerson){
                    return;
                }
            }
        }
        matrix.pushPose();
        RenderUtils.translate(bone, matrix);
        RenderUtils.moveToPivot(bone, matrix);
        RenderUtils.rotate(bone, matrix);
        RenderUtils.scale(bone, matrix);
        RenderUtils.moveBackFromPivot(bone, matrix);

        if (!bone.isHidden) {
            if (isShouldRender(bone,armorType)){
                for (GeoCube cube : bone.childCubes) {
                    matrix.pushPose();
                    this.getRenderer().renderCube(cube, matrix, buffer, light, packedOverlayIn, red, green, blue, alpha);
                    matrix.popPose();
                }
            }
            for (GeoBone childBone : bone.childBones) {
                renderRecursively(childBone, matrix, buffer, light, packedOverlayIn, red, green, blue, alpha,armorType);
            }
        }
        matrix.popPose();
    }

    public boolean isShouldRender(GeoBone bone,EquipmentSlot armorType){
        if (armorType == EquipmentSlot.HEAD){
            if (bone.getName().equals("Head")){
                return true;
            }
        }
        if (armorType == EquipmentSlot.CHEST){
            if (bone.getName().equals("BodyUp")
                    || bone.getName().equals("BodyMiddle")
                    || bone.getName().equals("BodyDown")
                    || bone.getName().equals("RightUpperArm")
                    || bone.getName().equals("RightLowerArm")
                    || bone.getName().equals("LeftUpperArm")
                    || bone.getName().equals("LeftLowerArm")){
                return true;
            }
        }
        if (armorType == EquipmentSlot.LEGS){
            if (bone.getName().equals("LeftUpperLeg") || bone.getName().equals("RightUpperLeg") || bone.getName().equals("LeftLowerLeg") || bone.getName().equals("RighyLowerLeg")){
                return true;
            }
        }
        if (armorType == EquipmentSlot.FEET){
            if (bone.getName().equals("LeftFeet") || bone.getName().equals("RighyFeet")){
                return true;
            }
        }
        return false;
    }
}
