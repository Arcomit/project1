package mods.arcomit.project;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * @Author Arcomit
 * @Update 2022/03/19-Arcomit
 * 用于给EntityRenderDispatcher添加方法getContext()
 */
public interface IMixinEntityRenderDispatcher {
    EntityRendererProvider.Context getContext();
}
