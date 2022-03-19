package mods.arcomit.project.animation.event;

import net.minecraft.world.entity.Entity;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

import java.util.List;

public class ReplacedAnimationEvent extends AnimationEvent {
    private final Entity entity;

    public ReplacedAnimationEvent(Entity entity, IAnimatable animatable, float limbSwing, float limbSwingAmount, float partialTick, boolean isMoving, List extraData) {
        super(animatable, limbSwing, limbSwingAmount, partialTick, isMoving, extraData);
        this.entity = entity;
    }

    public Entity getEntity(){
        return entity;
    }

}
