package com.Fishmod.mod_LavaCow.enchantment;

import com.Fishmod.mod_LavaCow.mod_LavaCow;
import com.Fishmod.mod_LavaCow.client.Modconfig;
import com.Fishmod.mod_LavaCow.init.ModMobEffects;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.potion.PotionEffect;

public class EnchantmentCorrosive extends Enchantment {

	public EnchantmentCorrosive(String unlocalizedName, String registryName, EnumEnchantmentType type) {
		super(Rarity.VERY_RARE, type, new EntityEquipmentSlot[] {EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND});
		this.setName(mod_LavaCow.MODID + "." + unlocalizedName);
		this.setRegistryName(registryName);
	}
	
	@Override
	public int getMaxLevel() {
		return 5;
	}
	
    /**
     * Is this enchantment allowed to be enchanted on books via Enchantment Table
     * @return false to disable the vanilla feature
     */
	@Override
    public boolean isAllowedOnBooks()
    {
        return Modconfig.Enchantment_Enable;
    }
	
	@Override
	public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
		if(target instanceof EntityLivingBase)
			((EntityLivingBase)target).addPotionEffect(new PotionEffect(ModMobEffects.CORRODED, 4*20, level - 1));
	}
}
