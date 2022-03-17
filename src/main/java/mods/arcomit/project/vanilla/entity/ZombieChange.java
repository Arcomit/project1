package mods.arcomit.project.vanilla.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

import java.util.UUID;

/**
 * @Author Arcomit
 * @Update 2022/03/17-Arcomit
 * 修改后的僵尸
 */
public class ZombieChange extends Zombie {
    protected static final UUID SPEED_MODIFIER_ZOMBIE_UUID = UUID.fromString("38BDBC53-75CB-4F27-BE51-65E73DA17074");
    private static final AttributeModifier SPEED_MODIFIER_ZOMBIE = new AttributeModifier(SPEED_MODIFIER_ZOMBIE_UUID, "Zombie speed boost", 0.65D, AttributeModifier.Operation.MULTIPLY_BASE);

    public ZombieChange(EntityType<? extends ZombieChange> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
    }

    @Override
    public void tick() {
        super.tick();
        AttributeInstance attributeinstance = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (!this.isBaby()){
            attributeinstance.addTransientModifier(SPEED_MODIFIER_ZOMBIE);
        }
    }
}
