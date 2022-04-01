package mods.arcomit.project.animation.controller.entity;

import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

public class ReplacedLivingController implements IAnimatable {
    public LivingEntity living;

    public ReplacedLivingController(LivingEntity living){
        this.living = living;
    }

    @Override
    public void registerControllers(AnimationData data) {}

    @Override
    public AnimationFactory getFactory() {
        return null;
    }
}
