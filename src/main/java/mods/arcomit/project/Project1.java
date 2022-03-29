package mods.arcomit.project;

import mods.arcomit.project.client.config.ClientConfig;
import mods.arcomit.project.registry.ItemRegistry;
import mods.arcomit.project.vanilla.entity.register.ChangeEntityVanilla;
import mods.arcomit.project.vanilla.event.PlayerRenderEvent;
import mods.arcomit.project.vanilla.item.register.ChangeItemVanilla;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;



/**
 * @Author Arcomit
 * @Update 2022/03/13-Arcomit
 */
@Mod(Project1.MODID)
public class Project1 {
    public static final String MODID = "project1";

    public static ConcurrentHashMap<String, String> armorResourceLocationHashMap = new ConcurrentHashMap(20);


    public Project1(){
        //Geckolib动画库初始化
        GeckoLib.initialize();

        //注册配置文件
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        //盔甲资源初始化
        arrmorResourceInitialize();


        //MOD总线
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        //客户端
        bus.addListener(this::doClientStuff);

        //将用于替换原版物品的VANILLA_ITEMS注册进MOD总线
        ChangeItemVanilla.VANILLA_ITEMS.register(bus);
        //将用于替换原版实体的VANILLA_ENTITYS注册进MOD总线
        ChangeEntityVanilla.VANILLA_ENTITYS.register(bus);

        //将用于添加模组物品的ITEMS注册进MOD总线
        ItemRegistry.ITEMS.register(bus);
    }

    public void arrmorResourceInitialize(){
        for (String string : ClientConfig.armorModel.get()){
            StringTokenizer stringTokenizer = new StringTokenizer(string,"=");
            armorResourceLocationHashMap.put(stringTokenizer.nextToken(),stringTokenizer.nextToken());
        }
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerRenderEvent.class);
    }
}
