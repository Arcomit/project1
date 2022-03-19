package mods.arcomit.project.animation.render;

import mods.arcomit.project.Project1;
import mods.arcomit.project.animation.controller.entity.PlayerController;
import mods.arcomit.project.animation.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * @Author Arcomit
 * @Update 2022/03/19-Arcomit
 * 用与修改玩家渲染
 */
public class PlayerRender extends ReplacedEntityRenderer{
    public PlayerRender(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PlayerModel(), new PlayerController());
    }

    //用于获取玩家模型格式
    @Override
    public ResourceLocation getModelLocation(Object instance) {
        if (instance instanceof AbstractClientPlayer){
            AbstractClientPlayer player = ((AbstractClientPlayer) instance);
            if (player.getModelName().equals("slim")){
                return new ResourceLocation(Project1.MODID, "model/player_slim.geo.json");
            }
        }
        return getGeoModelProvider().getModelLocation(instance);
    }

    //用于获取玩家皮肤
    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        if (entity instanceof AbstractClientPlayer){
            return ((AbstractClientPlayer) entity).getSkinTextureLocation();
        }
        return super.getTextureLocation(entity);
    }
}
