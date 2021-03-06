package mods.arcomit.project.mixin;

import mods.arcomit.project.animation.render.IMixinEntityRenderDispatcher;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.*;
import software.bernie.geckolib3.resource.ResourceListener;

import java.util.Map;

/**
 * @Author Arcomit
 * @Update 2022/03/17-Arcomit
 * 什么也别问,mjsb
 */
@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher implements IMixinEntityRenderDispatcher {
    private EntityRendererProvider.Context context;

    @Override
    public EntityRendererProvider.Context getContext() {
        return context;
    }

    @Final
    @Shadow
    private ItemRenderer itemRenderer;

    @Final
    @Shadow
    private Font font;

    @Final
    @Shadow
    private EntityModelSet entityModels;

    @Shadow
    private Map<String, EntityRenderer<? extends Player>> playerRenderers;

    @Shadow
    public Map<EntityType<?>, EntityRenderer<?>> renderers;

    /**
     * @author Arcomit
     * 用于获取context
     */
    @Overwrite
    public void onResourceManagerReload(ResourceManager pResourceManager) {
        context = new EntityRendererProvider.Context((EntityRenderDispatcher) (Object) this, this.itemRenderer, pResourceManager, this.entityModels, this.font);
        this.renderers = EntityRenderers.createEntityRenderers(context);
        this.playerRenderers = EntityRenderers.createPlayerRenderers(context);
        net.minecraftforge.fml.ModLoader.get().postEvent(new net.minecraftforge.client.event.EntityRenderersEvent.AddLayers(renderers, playerRenderers));
    }



}
