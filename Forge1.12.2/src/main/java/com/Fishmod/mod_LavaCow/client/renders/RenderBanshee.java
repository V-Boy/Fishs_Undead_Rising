package com.Fishmod.mod_LavaCow.client.renders;

import com.Fishmod.mod_LavaCow.client.model.entity.ModelBanshee;
import com.Fishmod.mod_LavaCow.entities.EntityBanshee;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderBanshee extends RenderLiving<EntityBanshee>{
	
	private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
			new ResourceLocation("mod_lavacow:textures/mobs/banshee.png"),
	};
	
	static{
		for(ResourceLocation texture: TEXTURES)
			System.out.println(texture.getResourcePath());
    }

    public RenderBanshee(RenderManager rendermanagerIn) {
        super(rendermanagerIn, new ModelBanshee(), 0.0F);
    }
    
    @Override
    protected ResourceLocation getEntityTexture(EntityBanshee entity) {
    	return TEXTURES[0];
    }
    
    @Override
	protected void preRenderCallback(EntityBanshee entity, float partialTickTime) {
	}
}
