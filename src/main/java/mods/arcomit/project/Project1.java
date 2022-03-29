package mods.arcomit.project;

import mods.arcomit.project.registry.ItemRegistry;
import mods.arcomit.project.vanilla.entity.register.ChangeEntityVanilla;
import mods.arcomit.project.vanilla.item.register.ChangeItemVanilla;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import software.bernie.geckolib3.GeckoLib;


/**
 * @Author Arcomit
 * @Update 2022/03/13-Arcomit
 */
@Mod(Project1.MODID)
public class Project1 {
    public static final String MODID = "project1";

    public Project1(){
        //Geckolib动画库初始化
        GeckoLib.initialize();

        //MOD总线
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        //将用于替换原版物品的VANILLA_ITEMS注册进MOD总线
        ChangeItemVanilla.VANILLA_ITEMS.register(bus);
        //将用于替换原版实体的VANILLA_ENTITYS注册进MOD总线
        ChangeEntityVanilla.VANILLA_ENTITYS.register(bus);

        //将用于添加模组物品的ITEMS注册进MOD总线
        ItemRegistry.ITEMS.register(bus);
    }

}
