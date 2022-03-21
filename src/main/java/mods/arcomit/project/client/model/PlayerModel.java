package mods.arcomit.project.client.model;

import mods.arcomit.project.Project1;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.model.AnimatedGeoModel;

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
}