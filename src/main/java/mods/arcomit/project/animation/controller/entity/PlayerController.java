package mods.arcomit.project.animation.controller.entity;

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
public class PlayerController extends ReplacedLivingController{
    AnimationFactory factory = new AnimationFactory(this);

    public PlayerController(Player player){
        super(player);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void registerControllers(AnimationData data) {
        //上半身动画
        data.addAnimationController(new AnimationController(this, "UpBodyController", 2, this::predicate));
        //下半身动画
        data.addAnimationController(new AnimationController(this, "DownBodyController", 2, this::predicate2));

    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {

        if (living.isCrouching()){
            event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.player.swinging", true));
        }else {
            event.getController().setAnimation((new AnimationBuilder()).addAnimation("animation.player.fc", true));
        }
        return PlayState.CONTINUE;
    }

    private <P extends IAnimatable> PlayState predicate2(AnimationEvent<P> event) {

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }
}