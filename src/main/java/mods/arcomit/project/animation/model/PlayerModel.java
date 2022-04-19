package mods.arcomit.project.animation.model;

import mods.arcomit.project.Project1;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;
import software.bernie.geckolib3.resource.GeckoLibCache;
import software.bernie.shadowed.eliotlash.molang.MolangParser;

import javax.annotation.Nullable;

/**
 * @Author Arcomit
 * @Update 2022/03/19-Arcomit
 * 玩家模型,用于获取模型贴图动作文件等
 */
@SuppressWarnings("rawtypes")
public class PlayerModel extends AnimatedGeoModel {
    //默认史蒂夫模型格式
    @Override
    public ResourceLocation getModelLocation(Object object) {
        return new ResourceLocation(Project1.MODID, "geo/player.geo.json");
    }

    //默认史蒂夫
    @Override
    public ResourceLocation getTextureLocation(Object object) {
        return new ResourceLocation("minecraft", "textures/entity/steve.png");
    }
    @Override
    public ResourceLocation getAnimationFileLocation(Object animatable) {
        return new ResourceLocation(Project1.MODID, "animations/player.animation.json");
    }

    @Override
    public void setLivingAnimations(Object entity, Integer uniqueID,AnimationEvent customPredicate) {
        super.setLivingAnimations((IAnimatable) entity,uniqueID,customPredicate);
        IBone head = this.getAnimationProcessor().getBone("HeadBone");
        //处理头部旋转
        EntityModelData extraData = (EntityModelData) customPredicate.getExtraDataOfType(EntityModelData.class).get(0);
        head.setRotationX(extraData.headPitch * ((float) Math.PI / 180F));
        head.setRotationY(extraData.netHeadYaw * ((float) Math.PI / 180F));

        //处理手部旋转
        IBone rightArm = this.getAnimationProcessor().getBone("RightArmBone");
        IBone leftArm = this.getAnimationProcessor().getBone("LeftArmBone");


    }
}