package mods.arcomit.project;

import net.minecraft.client.renderer.entity.EntityRendererProvider;

public interface IMixinEntityRenderDispatcher {
    EntityRendererProvider.Context getContext();
}
