package mods.arcomit.project.animation.controller.entity;

import mods.arcomit.project.animation.event.ReplacedAnimationEvent;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

/**
 * @Author Arcomit
 * @Update 2022/03/19-Arcomit
 * 玩家动画控制器,用与控制玩家动作
 */
public class PlayerController implements IAnimatable {
    AnimationFactory factory = new AnimationFactory(this);
    Player player;

    public PlayerController(Player player){
        this.player = player;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController(this, "controller", 2, this::predicate));
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {

        if (player.isCrouching()){
            event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.player.new", true));
        }else {
            event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.player.swinging", true));
        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}