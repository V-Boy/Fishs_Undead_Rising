package com.Fishmod.mod_LavaCow.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.Fishmod.mod_LavaCow.mod_LavaCow;
import com.Fishmod.mod_LavaCow.client.Modconfig;
import com.Fishmod.mod_LavaCow.entities.EntityFoglet;
import com.Fishmod.mod_LavaCow.entities.EntityParasite;
import com.Fishmod.mod_LavaCow.entities.EntityWendigo;
import com.Fishmod.mod_LavaCow.entities.flying.EntityFlyingMob;
import com.Fishmod.mod_LavaCow.entities.tameable.EntityRaven;
import com.Fishmod.mod_LavaCow.entities.tameable.EntityUnburied;
import com.Fishmod.mod_LavaCow.init.FishItems;
import com.Fishmod.mod_LavaCow.init.ModMobEffects;
import com.Fishmod.mod_LavaCow.init.Modblocks;
import com.Fishmod.mod_LavaCow.item.ItemFamineArmor;
import com.Fishmod.mod_LavaCow.item.ItemFelArmor;
import com.Fishmod.mod_LavaCow.item.ItemSwineArmor;
import com.Fishmod.mod_LavaCow.worldgen.WorldGenAquaMob;
import com.Fishmod.mod_LavaCow.worldgen.WorldGenCemeterySmall;
import com.Fishmod.mod_LavaCow.worldgen.WorldGenGlowShroom;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.SaveToFile;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.oredict.OreDictionary;

@EventBusSubscriber
@Optional.Interface(iface = "baubles.api.IBauble", modid = "baubles", striprefs = true)
public class ModEventHandler {
	
    /**
     * Custom entity death event, using for manipulating vanilla entities loots or onDeath triggers.
     * Example: Spawn swarm of parasites when a zombie dies (10% chance)
     */
    @SubscribeEvent
    public void onEDeath(LivingDeathEvent event)
    {
    	Entity entity = event.getEntity();
	    boolean Armor_Famine_lvl = false;
	    World world = event.getEntity().getEntityWorld();
	    
	    if(event.getSource().getTrueSource() != null)
			for(ItemStack S : event.getSource().getTrueSource().getArmorInventoryList()) {
				if(S.getItem() instanceof ItemFamineArmor && ((ItemFamineArmor)S.getItem()).getSetBonus() >= 4) {
					Armor_Famine_lvl = true;
					break;
				}
			}
		
		if(Armor_Famine_lvl && event.getSource().getTrueSource() instanceof EntityPlayer) {
			((EntityPlayer)event.getSource().getTrueSource()).heal(1.0F);
			((EntityPlayer)event.getSource().getTrueSource()).getFoodStats().addStats(4, 0.0F);
		}

    	/**
         * Give a chance to spawn horde of Parasites(WIP) when a zombie dies.
         **/
    	if (!world.isRemote && world.provider.isSurfaceWorld()  
    			&& entity instanceof EntityZombie && entity.ticksExisted >= 60
    			&& (new Random().nextInt(100) < Modconfig.pSpawnRate_Parasite || EntityParasite.gotParasite(entity.getPassengers()) != null)) 
    	{
    		int var2 = 3 + new Random().nextInt(3);
    		float var4,var5;
    		EntityParasite passenger = EntityParasite.gotParasite(entity.getPassengers());
    		
    		for(int var3 = 0; var3 < var2; ++var3)
    		{
    			var4 = ((float)(var3 % 2) - 0.5F) / 4.0F;
                var5 = ((float)(var3 / 2) - 0.5F) / 4.0F;
                
                //System.out.println("OAOAOAOAOAOAOAOAOAOAOAO456");
        		EntityParasite entityparasite = new EntityParasite(world);
        		if(passenger != null)entityparasite.setSkin(passenger.getSkin());
        		else if(entity instanceof EntityHusk)entityparasite.setSkin(1);
        		else if(BiomeDictionary.hasType(world.getBiome(entity.getPosition()), BiomeDictionary.Type.JUNGLE))entityparasite.setSkin(2);
        		else entityparasite.setSkin(0);
        		
                entityparasite.setLocationAndAngles(entity.posX + (double)var4, entity.posY + 1.0D, entity.posZ + (double)var5, entity.rotationYaw, entity.rotationPitch);
                world.spawnEntity(entityparasite);
    		}
    	}			
    }
    
    @SubscribeEvent
    public void onEntityDrop(LivingDropsEvent event) {
        
    	/**
         * Wolf's loot table seems broken, so this method is implemented instead. 
         **/
    	if (event.getEntityLiving() instanceof EntityWolf && event.getEntityLiving().getRNG().nextInt(5) == 1) {
            event.getEntityLiving().dropItem(FishItems.SHARPTOOTH, 1);
        }

    	/**
         * Add bonus loot (Intestine) to various entities.
         **/
    	if (event.getEntityLiving() instanceof EntityLiving && (event.getEntityLiving().width > 1.0F || event.getEntityLiving().height > 1.0F) && event.getEntityLiving().getRNG().nextFloat() < 0.01F * (float)Modconfig.General_Intestine) {
            event.getEntityLiving().dropItem(FishItems.INTESTINE, 1);
        }

    }
    
    /**
     * Custom anvil event, using for making items into enchantment for weapons and tools
     * Example: Make glow shroom and parasite a good lure
     */
    @SubscribeEvent
    public void onAnvilUpdate(AnvilUpdateEvent event)
    {
        
    	ItemStack tool = event.getLeft();
    	ItemStack ench = event.getRight();
    	ItemStack outputStack = tool.copy();
    	Map<Enchantment, Integer> currentEnchantments = EnchantmentHelper.getEnchantments(tool);
    	int ench_lvl = 1;
    	
    	if(tool.getItem() instanceof ItemFishingRod && ench.getItem() == FishItems.PARASITE_ITEM)
    	{
    		ench_lvl = 1;
    		//outputStack.addEnchantment(Enchantment.getEnchantmentByLocation("lure"), ench_lvl);
    		event.setOutput(outputStack);
    		event.setCost(ench_lvl*2);
    		if (currentEnchantments.containsKey(Enchantment.getEnchantmentByLocation("lure"))) 
    		{
				event.setOutput(event.getLeft().copy());
				EnchantmentHelper.setEnchantments(currentEnchantments, event.getOutput());
			} 
    		else 
    		{
				event.setOutput(event.getLeft().copy());
				event.getOutput().addEnchantment(Enchantment.getEnchantmentByLocation("lure"), ench_lvl);
			}
    		//event.setCost(getEnchantmentCost(Enchantment.getEnchantmentByID(62), 3));
    		event.setMaterialCost(1);
    		//return;
    	}
    	else if(tool.getItem() instanceof ItemFishingRod && ench.getItem() == Modblocks.item_block_glowshroom)
    	{
    		ench_lvl = 3;
    		//outputStack.addEnchantment(Enchantment.getEnchantmentByLocation("lure"), ench_lvl);
    		event.setOutput(outputStack);
    		event.setCost(ench_lvl*2);
    		if (currentEnchantments.containsKey(Enchantment.getEnchantmentByLocation("lure"))) 
    		{
				event.setOutput(event.getLeft().copy());
				EnchantmentHelper.setEnchantments(currentEnchantments, event.getOutput());
			} 
    		else 
    		{
				event.setOutput(event.getLeft().copy());
				event.getOutput().addEnchantment(Enchantment.getEnchantmentByLocation("lure"), ench_lvl);
			}
    		//event.setCost(getEnchantmentCost(Enchantment.getEnchantmentByID(62), 3));
    		event.setMaterialCost(1);
    		//return;
    	}
    	else if(ench.getItem() == FishItems.POISONSPORE)
    	{
    		ench_lvl = 3;
    		event.setOutput(outputStack);
    		event.setCost(13);
    		if (currentEnchantments.containsKey(Enchantment.getEnchantmentByLocation("mod_lavacow:poisonous"))) 
    		{
				event.setOutput(event.getLeft().copy());
				EnchantmentHelper.setEnchantments(currentEnchantments, event.getOutput());
			} 
    		else 
    		{
				event.setOutput(event.getLeft().copy());
				event.getOutput().addEnchantment(Enchantment.getEnchantmentByLocation("mod_lavacow:poisonous"), ench_lvl);
			}
    		event.setMaterialCost(1);
    	}
    	else if(ench.getItem() == FishItems.UNDYINGHEART)
    	{
    		ench_lvl = 3;
    		event.setOutput(outputStack);
    		event.setCost(13);
    		if (currentEnchantments.containsKey(Enchantment.getEnchantmentByLocation("mod_lavacow:lifesteal"))) 
    		{
				event.setOutput(event.getLeft().copy());
				EnchantmentHelper.setEnchantments(currentEnchantments, event.getOutput());
			} 
    		else 
    		{
				event.setOutput(event.getLeft().copy());
				event.getOutput().addEnchantment(Enchantment.getEnchantmentByLocation("mod_lavacow:lifesteal"), ench_lvl);
			}
    		event.setMaterialCost(1);
    	}
    	else if(ench.getItem() == FishItems.ACIDICHEART)
    	{
    		ench_lvl = 1;
    		event.setOutput(outputStack);
    		event.setCost(4);
    		if (currentEnchantments.containsKey(Enchantment.getEnchantmentByLocation("mod_lavacow:corrosive"))) 
    		{
				event.setOutput(event.getLeft().copy());
				EnchantmentHelper.setEnchantments(currentEnchantments, event.getOutput());
			} 
    		else 
    		{
				event.setOutput(event.getLeft().copy());
				event.getOutput().addEnchantment(Enchantment.getEnchantmentByLocation("mod_lavacow:corrosive"), ench_lvl);
			}
    		event.setMaterialCost(1);
    	}

    }
    
    /**
     * Add custom loot to general fishing pool.
     * The list were setup in LootTableHandler.java
     */
    private static void addLoot(LootPool pool, Item item, int weight)
    {    	
    	pool.addEntry(new LootEntryItem(item, weight, 0, new LootFunction[0], new LootCondition[0], mod_LavaCow.MODID + item.getUnlocalizedName()));
    }
    
    /**
     * Apply custom loot to vanilla loot tables.
     * Example: Custom items loot in jungle temple/polar bear
     */
    @SubscribeEvent
    public static void addLoot(LootTableLoadEvent event)
    {
        if(event.getName().equals(LootTableList.GAMEPLAY_FISHING_JUNK))
        {
            LootPool pool = event.getTable().getPool("main");
            if(pool != null)for(Map.Entry<Item, Integer> entry : LootTableHandler.FISHABLE.entrySet())
                addLoot(pool, entry.getKey(), entry.getValue());
        }
        
        if(event.getName().equals(LootTableList.ENTITIES_POLAR_BEAR))
        {
            LootPool pool = event.getTable().getPool("main");
            if(pool != null)addLoot(pool, FishItems.SHARPTOOTH, 1);
        }

        if(event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE))
        {
            LootPool pool = event.getTable().getPool("main");
            if(pool != null)
            {
	            addLoot(pool, FishItems.HYPHAE, 6);
	            addLoot(pool, FishItems.PIRANHA, 6);
	            addLoot(pool, FishItems.SHARPTOOTH, 6);
            }
        }    
        

        if(event.getName().equals(LootTableList.CHESTS_IGLOO_CHEST))
        {
            LootPool pool = event.getTable().getPool("main");
            if(pool != null)
            {
	            addLoot(pool, FishItems.FROZENTHIGH, 1);
            }
        } 
        
        if(event.getName().equals(LootTableList.ENTITIES_HUSK))
        {
            LootPool pool = event.getTable().getPool("main");
            if(pool != null)addLoot(pool, FishItems.CURSED_FABRIC, 1);
        }
    }
    
    /**
     * Custom biome generate event, using for generating custom blocks to vanilla biomes.
     * Example: Custom biome generation for glow shroom
     */
    @SubscribeEvent
    public void decorate(DecorateBiomeEvent.Decorate event) {
    	World world = event.getWorld();
    	Biome biome = world.getBiomeForCoordsBody(event.getChunkPos().getBlock(0, 0, 0));
    	Random rand = event.getRand();
    	BlockPos pos = world.getHeight(event.getChunkPos().getBlock(8, 0, 8));
		//int x = rand.nextInt(16) + 8;
		//int z = rand.nextInt(16) + 8;
		
    	if (!BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD) && world.provider.isSurfaceWorld() && event.getType() == DecorateBiomeEvent.Decorate.EventType.SHROOM) {
    		WorldGenGlowShroom gen = new WorldGenGlowShroom();
    		gen.generate(Modblocks.GLOWSHROOM, world, rand, world.getHeight(event.getChunkPos().getBlock(8, 0, 8)));
    		//event.setResult(Event.Result.DENY);
    	}
    	
    	if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER) && event.getType() == DecorateBiomeEvent.Decorate.EventType.SHROOM) {
    		WorldGenGlowShroom gen = new WorldGenGlowShroom();
    		gen.generate(Modblocks.BLOODTOOTH_SHROOM, world, rand, world.getHeight(event.getChunkPos().getBlock(8, 0, 8)));
    	}
    	
    	if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) && event.getType() == DecorateBiomeEvent.Decorate.EventType.SHROOM) {
    		WorldGenGlowShroom gen = new WorldGenGlowShroom();
    		gen.generate(Modblocks.CORDY_SHROOM, world, rand, world.getHeight(event.getChunkPos().getBlock(8, 0, 8)));
    	}
    	
    	if ((BiomeDictionary.hasType(biome, BiomeDictionary.Type.MUSHROOM) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)) && event.getType() == DecorateBiomeEvent.Decorate.EventType.SHROOM) {
    		WorldGenGlowShroom gen = new WorldGenGlowShroom();
    		gen.generate(Modblocks.VEIL_SHROOM, world, rand, world.getHeight(event.getChunkPos().getBlock(8, 0, 8)));
    	}
    }
    
    /**
     * Player on update event, repeat once per second
     * Add auto repairing effect for Golden Heart
     */
    @SubscribeEvent
    public void playerTick(final TickEvent.PlayerTickEvent event) {
    	if (event.phase == TickEvent.Phase.START) {
            return;
        }
        final EntityPlayer player = event.player;
        if (player.world.isRemote) {
            return;
        }

        if ((player.world.getTotalWorldTime() & 0x1FL) > 0L) {
            return;
        }
        
        /*if(player.getHeldItemMainhand().getItem() == FishItems.GOLDENHEART || player.getHeldItemOffhand().getItem() == FishItems.GOLDENHEART)
        {
        	if(((EntityLivingBase)player).getActivePotionEffect(MobEffects.REGENERATION) == null && player.getHealth() < player.getMaxHealth())
            	((EntityLivingBase)player).addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 8*20, 0));
        	else if(player.getHealth() == player.getMaxHealth())
	        	for(ItemStack item : player.getEquipmentAndArmor())
		        {
		        	if(!item.getItem().equals(FishItems.GOLDENHEART) && item.getMaxDamage() != 0 && item.getItem().isDamageable() && (item.isItemEnchantable() || item.isItemEnchanted()))item.setItemDamage(java.lang.Math.max(item.getItemDamage()-1, 0));
		        }
        }*/
        
        //if (player.motionY < -0.1D)player.motionY *= 0.125D;
    }
    
    @SubscribeEvent
    public static void onBlockDestroyed(HarvestDropsEvent event) {
    	if(event.getDrops() != null && event.getHarvester() != null && event.getHarvester().getHeldItemMainhand().getItem() == FishItems.MOLTENAXE)
    	{
    		List<ItemStack> ores = OreDictionary.getOres("logWood");
    		List<ItemStack> to_be_removed = new ArrayList<ItemStack>();
    		for(ItemStack item: event.getDrops())
	    	{
    			for(ItemStack ore: ores)
    		    	if(OreDictionary.itemMatches(ore, item, false))
    		    	{ 
    		    		to_be_removed.add(item);
    		    	}	    					 
	    	}
    		
    		for(ItemStack item: to_be_removed) {
    			event.getDrops().add(new ItemStack(Items.COAL, item.getCount() + new Random().nextInt(2) + event.getFortuneLevel(), 1));
    			event.getDrops().remove(item);
    		}
    		//System.out.println("OAOAOAOAOAOAOAOAOAOAOAO");    		
    	}
    	
    	if(event.getState().getMaterial() == Material.SAND 
    		&& BiomeDictionary.hasType(event.getWorld().getBiome(event.getPos()), BiomeDictionary.Type.DRY) 
    		&& new Random().nextInt(100) < Modconfig.Parasite_SandSpawn
    		) {
    		//System.out.println("OAOAOAOAOAOAOAOAOAOAOAO");    
    		EntityParasite entityparasite = new EntityParasite(event.getWorld());
    		entityparasite.setSkin(1);
            entityparasite.setLocationAndAngles(event.getPos().getX(), event.getPos().getY()+1.0D, event.getPos().getZ(), 0.0F, 0.0F);
            entityparasite.addVelocity(0.0D, 0.4D, 0.0D);
            event.getWorld().spawnEntity(entityparasite);    		
    	}
    	
    	if(Modconfig.Shattered_Ice && event.getHarvester() != null && event.getState().getMaterial() == Material.ICE && !event.isSilkTouching()) {
    			event.getDrops().add(new ItemStack(FishItems.SHATTERED_ICE, new Random().nextInt(4) + event.getFortuneLevel()));
        	}

    }
    
    @SubscribeEvent
    public void onEDamage(LivingDamageEvent event) {
        /*if(event.getEntityLiving().isBeingRidden() && event.getEntityLiving().getPassengers().get(0) instanceof EntityRaven && event.getSource().equals(DamageSource.FALL)) {
        	event.setAmount(0.0F);
        }*/
    	float effectlevel = 1.0F;
	    boolean Armor_Famine_lvl = false;
	    if(event.getSource().getTrueSource() != null)
			for(ItemStack S : event.getSource().getTrueSource().getArmorInventoryList()) {
				if(S.getItem() instanceof ItemFamineArmor && ((ItemFamineArmor)S.getItem()).getSetBonus() >= 2) {
					Armor_Famine_lvl = true;
					break;
				}
			}
		
		if(Armor_Famine_lvl && event.getSource().getTrueSource() instanceof EntityLivingBase && ((EntityLivingBase) event.getSource().getTrueSource()).isPotionActive(MobEffects.HUNGER)) {
			event.setAmount(event.getAmount() + 2.0F);
		}
    	
    	if(event.getEntityLiving().isBurning() && event.getSource().getTrueSource() instanceof EntityPlayer) {   		
    		for(ItemStack S : event.getSource().getTrueSource().getEquipmentAndArmor()) {
    			if(S.getItem() instanceof ItemFelArmor)effectlevel += ((ItemFelArmor)S.getItem()).effectlevel;
    		}
    	}
    	
    	if(event.getEntityLiving() instanceof EntityPlayer && !event.getEntityLiving().isImmuneToFire() && (event.getSource().isFireDamage())) {    		
    		for(ItemStack S : event.getEntityLiving().getEquipmentAndArmor()) {
    			if(S.getItem() instanceof ItemFelArmor)effectlevel -= ((ItemFelArmor)S.getItem()).fireprooflevel;
    		}
    	}
    	
    	if(event.getSource().isExplosion() && event.getSource().getTrueSource() instanceof EntityFoglet){
    		if(event.getEntityLiving().getCreatureAttribute().equals(EnumCreatureAttribute.UNDEAD))
    			event.setAmount(event.getAmount() * 0.45F);
    		else
    			event.setAmount(event.getAmount() * 0.15F);
    	}
    	
    	if(event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer && ((EntityPlayer)event.getSource().getTrueSource()).getHeldItemMainhand().getItem().equals(FishItems.BONESWORD))
    		event.setAmount(event.getAmount() + event.getEntityLiving().getMaxHealth() * Math.min((float)Modconfig.BoneSword_DamageCap, ((float)Modconfig.BoneSword_Damage * 0.01F)));
    	
    	if(event.getSource().getTrueSource() != null 
    			&& event.getSource().getTrueSource() instanceof EntityPlayer
    			&& event.getEntityLiving().getCreatureAttribute().equals(EnumCreatureAttribute.UNDEAD)) {
    		
    		boolean have_Necklace = false;
    		if(Loader.isModLoaded("baubles")) {
    			if(baubles.api.BaublesApi.isBaubleEquipped((EntityPlayer) event.getSource().getTrueSource(), FishItems.HALO_NECKLACE) != -1)
    				have_Necklace = true;
    		}
    		
    		for(int i = 0; i < 9 ; i++)
    			if(((EntityPlayer)event.getSource().getTrueSource()).inventory.getStackInSlot(i).getItem().equals(FishItems.HALO_NECKLACE))
					have_Necklace = true;
    		//if(((EntityPlayer)event.getSource().getTrueSource()).getHeldItemMainhand().getItem().equals(FishItems.HALO_NECKLACE) || ((EntityPlayer)event.getSource().getTrueSource()).getHeldItemOffhand().getItem().equals(FishItems.HALO_NECKLACE))
    			//have_Necklace = true;
    		
    		if(have_Necklace)
    			event.setAmount(event.getAmount() * ((float)(100 + Modconfig.HaloNecklace_Damage))/100.0F);
    					
    	}
    	
    	if(event.getEntityLiving().isPotionActive(ModMobEffects.CORRODED))
    		event.setAmount(event.getAmount() * (1.0F + 0.1F * (1 + event.getEntityLiving().getActivePotionEffect(ModMobEffects.CORRODED).getAmplifier())));
    	
    	event.setAmount(event.getAmount() * effectlevel);
    	
    	/*if(event.getEntityLiving() instanceof EntityPlayer)
    		System.out.println("OAO " + event.getAmount());*/
    }
    
    @SubscribeEvent
    public void onEFall(LivingFallEvent event) {
        if(Modconfig.Raven_Slowfall && event.getEntityLiving().isBeingRidden()) {
        	//System.out.println("OAO");
        	for(Entity E : event.getEntityLiving().getPassengers()) {
        		if(E instanceof EntityRaven) {
        			//System.out.println("QAQ");
        			event.setDistance(0.0F);
        			break;
        		}
        	}
    	} 
    }
    
    @SubscribeEvent
    public void onPLogout(SaveToFile event) {
    	if(event.getEntityPlayer().isBeingRidden())
    		for(Entity E : event.getEntityPlayer().getPassengers())
    			if(E instanceof EntityRaven || E instanceof EntityParasite)E.dismountRidingEntity();
    }
    
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
    	if(Modconfig.Wendigo_AnimalAttack && event.getEntity() != null && event.getEntity() instanceof EntityAgeable && event.getEntity() instanceof EntityTameable) {
    		((EntityAgeable)event.getEntity()).tasks.addTask(1, new EntityAIAvoidEntity<>(((EntityAgeable)event.getEntity()), EntityWendigo.class, 8.0F, 0.8D, 1.2D));
    	}
    	
    	if(event.getEntity() != null && event.getEntity() instanceof EntityVillager)
    		((EntityVillager)event.getEntity()).tasks.addTask(1, new EntityAIAvoidEntity<>(((EntityVillager)event.getEntity()), EntityUnburied.class, 8.0F, 0.8D, 1.2D));
    }
    
    @SubscribeEvent
    public void onEAttack(LivingAttackEvent event) {
    	if(event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer && ((EntityPlayer)event.getSource().getTrueSource()).getHeldItemMainhand().getItem().equals(FishItems.REAPERS_SCYTHE)) {
    		event.getSource().setDamageBypassesArmor();
    		//System.out.println("OAOAOAOAOAOAOAOAOAOAOAO");
    	}
    }
    
    /*private static BlockPos getRandomChunkPosition(World worldIn, int x, int z)
    {
        Chunk chunk = worldIn.getChunkFromChunkCoords(x, z);
        int i = x * 16 + worldIn.rand.nextInt(16);
        int j = z * 16 + worldIn.rand.nextInt(16);
        int k = MathHelper.roundUp(chunk.getHeight(new BlockPos(i, 0, j)) + 1, 16);
        int l = worldIn.rand.nextInt(k > 0 ? k : chunk.getTopFilledSegment() + 16 - 1);
        return new BlockPos(i, l, j);
    }*/
    
    @SubscribeEvent
    public void onEWakeup(PlayerWakeUpEvent event) {
		ItemStack have_DreamCatcher = null;
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getEntityPlayer().getEntityWorld();
		
		if(Loader.isModLoaded("baubles")) {
			int i = baubles.api.BaublesApi.isBaubleEquipped(player, FishItems.DREAMCATCHER);
			if(i != -1)
				have_DreamCatcher = baubles.api.BaublesApi.getBaublesHandler(player).getStackInSlot(i);
		}
		
		for(int i = 0; i < 9 ; i++)
			if(player.inventory.getStackInSlot(i).getItem().equals(FishItems.DREAMCATCHER))
				have_DreamCatcher = player.inventory.getStackInSlot(i);
		/*if(player.getHeldItemMainhand().getItem().equals(FishItems.DREAMCATCHER))
			have_DreamCatcher = player.getHeldItemMainhand();
		else if(player.getHeldItemOffhand().getItem().equals(FishItems.DREAMCATCHER))
			have_DreamCatcher = player.getHeldItemOffhand();*/
		
		if(!world.isRemote && have_DreamCatcher != null/* && new Random().nextFloat() < 0.4F*/) {
			Biome.SpawnListEntry Result = ((Biome.SpawnListEntry)WeightedRandom.getRandomItem(world.rand, LootTableHandler.DREAMCATCHER_LIST));
			//System.out.println("OxO " + Result.entityClass.getName());
			Entity entityliving = EntityRegistry.getEntry(Result.entityClass).newInstance(world);
			int min  = Result.minGroupCount;
			int max  = Result.maxGroupCount;
			boolean has_spawn = false;
			
			if(entityliving instanceof EntityLiving) {
				for(int i = 0; i < new Random().nextInt(MathHelper.abs(max-min) + 1) + min; i++) {
					/*BlockPos blockpos = getRandomChunkPosition(player.getEntityWorld(), player.chunkCoordX, player.chunkCoordZ);
					int k1 = blockpos.getX();
					int l1 = blockpos.getY();
					int i2 = blockpos.getZ();*/
					
					double k1 = player.posX + (world.rand.nextDouble() * 32.0D) - 16.0D;
					double l1 = player.posY + (world.rand.nextDouble() * 4.0D) - 2.0D;
					double i2 = player.posZ + (world.rand.nextDouble() * 32.0D) - 16.0D;
					
					entityliving.setLocationAndAngles(k1, l1, i2, 0.0F, 0.0F);
					//ItemFishCustomWeapon.LavaBurst(world, k1, l1, i2, 1.0D, EnumParticleTypes.PORTAL);
					//System.out.println("OAO " + entityliving.getName() + k1 + " " + (l1 + 0.2F) + " " + i2);
		            /*for (int i1 = 0; i1 < 8; ++i1)
		            {
		                int j = world.rand.nextInt(2) * 2 - 1;
		                int k = world.rand.nextInt(2) * 2 - 1;
		                double d0 = k1 + 0.5D + 0.25D * (double)j;
		                double d1 = (double)(l1 + world.rand.nextFloat());
		                double d2 = i2 + 0.5D + 0.25D * (double)k;
		                double d3 = (double)(world.rand.nextFloat() * (float)j);
		                double d4 = ((double)world.rand.nextFloat() - 0.5D) * 0.125D;
		                double d5 = (double)(world.rand.nextFloat() * (float)k);
		                world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		            }*/
					if(((EntityLiving)entityliving).isNotColliding()/* && ((EntityLiving)entityliving).getCanSpawnHere()*/) {
						world.spawnEntity(entityliving);
						has_spawn = true;
					}
				}
				
				if(has_spawn) {
					//player.playSound(SoundEvents.AMBIENT_CAVE, 1.0F, 1.0F);
					world.playSound((EntityPlayer)null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_PORTAL_TRIGGER, SoundCategory.BLOCKS, 1.0F, (1.0F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2F) * 0.7F);
					//ItemFishCustomWeapon.LavaBurst(world, player.posX, player.posY, player.posZ, 1.5D, EnumParticleTypes.PORTAL);
					
		            /*for (int i = 0; i < 8; ++i)
		            {
		                int j = world.rand.nextInt(2) * 2 - 1;
		                int k = world.rand.nextInt(2) * 2 - 1;
		                double d0 = player.posX + 0.5D + 0.25D * (double)j;
		                double d1 = (double)(player.posY + world.rand.nextFloat());
		                double d2 = player.posZ + 0.5D + 0.25D * (double)k;
		                double d3 = (double)(world.rand.nextFloat() * (float)j);
		                double d4 = ((double)world.rand.nextFloat() - 0.5D) * 0.125D;
		                double d5 = (double)(world.rand.nextFloat() * (float)k);
		                world.spawnParticle(EnumParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5);
		            }*/
					
					if(!player.isCreative())
						have_DreamCatcher.damageItem(20, event.getEntityLiving());
				}
			}            	
		}
			
    }
    
    @SubscribeEvent
    public void onCollidBox(GetCollisionBoxesEvent event) {
    	if((event.getEntity() instanceof EntityWendigo && !event.getEntity().onGround) || event.getEntity() instanceof EntityFlyingMob) {
    		
    		List<AxisAlignedBB> list = new ArrayList<>();
    		World world = event.getWorld();
    		
    		for(AxisAlignedBB B : event.getCollisionBoxesList()) {
    			BlockPos pos = new BlockPos((B.maxX + B.minX) * 0.5D, (B.maxY + B.minY) * 0.5D, (B.maxZ + B.minZ) * 0.5D);
    			IBlockState state = world.getBlockState(pos);
                //Block blo﻿ck = world.getBlockState(pos).getBlock();
                //System.out.println("OAO" + state.toString());
                if(state.getMaterial() != Material.LEAVES) {
                	list.add(B);
                	
                }
    		}
    		
    		event.getCollisionBoxesList().clear();
    		event.getCollisionBoxesList().addAll(list);
    	}
    }
    
    @SubscribeEvent
    public void onActiveItemUseStart(LivingEntityUseItemEvent.Start event) {
	    boolean Armor_Famine_lvl = false;
		for(ItemStack S : event.getEntityLiving().getArmorInventoryList()) {
			if(S.getItem() instanceof ItemFamineArmor && ((ItemFamineArmor)S.getItem()).getSetBonus() >= 4) {
				Armor_Famine_lvl = true;
				break;
			}
		}
		
    	if((event.getEntityLiving().isPotionActive(ModMobEffects.SOILED) || Armor_Famine_lvl) && event.getItem().getItem() instanceof ItemFood) {
    		event.setCanceled(true);
    		//System.out.println("OAO");
    	}
    	
    	if(event.getEntityLiving().isPotionActive(ModMobEffects.SOILED)  && event.getItem().getItem() instanceof ItemPotion) {
    		event.setCanceled(true);
    		//System.out.println("OAO");
    	}
    }
    
    @SubscribeEvent
    public void onActiveItemUseTick(LivingEntityUseItemEvent.Tick event) {
	    boolean Armor_Swine_lvl = false;
		for(ItemStack S : event.getEntityLiving().getArmorInventoryList()) {
			if(S.getItem() instanceof ItemSwineArmor && ((ItemSwineArmor)S.getItem()).getSetBonus() >= 2) {
				Armor_Swine_lvl = true;
				break;
			}
		}
    	
    	if(Armor_Swine_lvl && event.getItem().getItem() instanceof ItemFood) {
    		event.setDuration((int) (event.getDuration() * 0.8F));
    	}
    }
    
    @SubscribeEvent
    public void onActiveItemUseFinish(LivingEntityUseItemEvent.Finish event) {
	    boolean Armor_Swine_lvl = false;
		for(ItemStack S : event.getEntityLiving().getArmorInventoryList()) {
			if(S.getItem() instanceof ItemSwineArmor && ((ItemSwineArmor)S.getItem()).getSetBonus() >= 4) {
				Armor_Swine_lvl = true;
				break;
			}
		}
    	
    	if(!event.getEntityLiving().world.isRemote && Armor_Swine_lvl && event.getItem().getItem() instanceof ItemFood) {
    		event.getEntityLiving().curePotionEffects(new ItemStack(Items.MILK_BUCKET));
    	}
    }
    
    /**
     * Young Simba:Everything the light touches... But what about that dark greeny place?
     * Mufasa:That's beyond our borders. You must never go there Simba.
     */
    
    /*@SubscribeEvent
    public void onExplosion(ExplosionEvent event) {
    	if(event.getExplosion().getExplosivePlacedBy() instanceof EntityFoglet) {
    		//System.out.println("OAO");
    	}
    }*/
    
    /*@SubscribeEvent
    public void onItemCrafted(ItemCraftedEvent event) {
    	int pivot = -1;
    	for(int i = 0; i < 3; i++ ) {
    		if(event.craftMatrix.getStackInSlot(i).getItem().equals(FishItems.POISONSTINGER))
    			pivot = i;
    		else
    			return ;
    	}
    	
		List<ItemStack> ores = OreDictionary.getOres("stickWood");
		for(ItemStack ore: ores)
		    if(!OreDictionary.itemMatches(ore, event.craftMatrix.getStackInSlot(pivot + 3), false))
		    { 
		    	return ;
		    }	    					 
		
		ores = OreDictionary.getOres("feather");
		for(ItemStack ore: ores)
		    if(!OreDictionary.itemMatches(ore, event.craftMatrix.getStackInSlot(pivot + 6), false))
		    { 
		    	return ;
		    }
		
        PotionUtils.addPotionToItemStack(event.crafting, PotionTypes.STRONG_POISON);
        //PotionUtils.appendEffects(event.crafting, PotionUtils.getFullEffectsFromItem(itemstack));
		
    }*/
    
    /**
     * Add custom loot to biome specific fishing pool.
     * This will replace the original loot player were to caught
     */
    /*@SubscribeEvent
    public void onPlayerFish(ItemFishedEvent event) 
    {
    	EntityFishHook hook = event.getHookEntity();
    	World world = hook.getEntityWorld();
    	Biome biome = world.getBiomeForCoordsBody(new BlockPos(hook));
    	//System.out.println("Got'em");
    	List<ItemStack> drops = Lists.newArrayList();
    	boolean editedList = false;
    	for(ItemStack stack : event.getDrops())
    	{
    		if(BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE) && new Random().nextInt(10) == 1)
    		{
    		  	drops.add(new ItemStack(FishItems.PIRANHA));
    		  	editedList = true;
    		  	//System.out.println("Hot!!");
      			continue;  
    		}
    		else if(!(BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.BEACH) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) && new Random().nextInt(30) == 1)
    		{
    		  	drops.add(new ItemStack(FishItems.CHEIROLEPIS));
    		  	editedList = true;
      			continue;  
    		}
    		drops.add(stack);
    	}
      
    	if(editedList)
    	{
    		event.damageRodBy(1);
    		for (ItemStack itemstack : drops)
    	          {
    	              EntityItem entityitem = new EntityItem(hook.world, hook.posX, hook.posY, hook.posZ, itemstack);
    	              double d0 = event.getEntityPlayer().posX - hook.posX;
    	              double d1 = event.getEntityPlayer().posY - hook.posY;
    	              double d2 = event.getEntityPlayer().posZ - hook.posZ;
    	              double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    	              entityitem.motionX = d0 * 0.1D;
    	              entityitem.motionY = d1 * 0.1D + (double)MathHelper.sqrt(d3) * 0.08D;
    	              entityitem.motionZ = d2 * 0.1D;
    	              hook.world.spawnEntity(entityitem);
    	              event.getEntityPlayer().world.spawnEntity(new EntityXPOrb(event.getEntityPlayer().world, event.getEntityPlayer().posX, event.getEntityPlayer().posY + 0.5D, event.getEntityPlayer().posZ + 0.5D, new Random().nextInt(6) + 1));
    	              Item item = itemstack.getItem();

    	              if (item == Items.FISH || item == Items.COOKED_FISH || item == FishItems.PIRANHA || item == FishItems.CHEIROLEPIS)
    	              {
    	              	event.getEntityPlayer().addStat(StatList.FISH_CAUGHT, 1);
    	              }
    	          }
    		event.setCanceled(true);
    	}
    }*/
    
    /**
     * Add tooltips to custom items
     * I18n takes the text contents from lang.json
     */
	/*@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onTooltip(ItemTooltipEvent event)
	{
		ArrayList<String> tooltip = (ArrayList<String>) event.getToolTip();
		ItemStack stack = event.getItemStack();
				
		if (stack.getItem() instanceof ItemBoneSword)
		{
			tooltip.add(TextFormatting.BLUE + "" + TextFormatting.ITALIC + I18n.format("tootip.mod_lavacow.BoneSword"));
		}
		else if (stack.getItem() == FishItems.CANEBEEF || stack.getItem() == FishItems.CANEPORK ||stack.getItem() == FishItems.CANEROTTENMEAT)
		{
			tooltip.add(TextFormatting.YELLOW + I18n.format("tootip.mod_lavacow.Canefood"));
		}
		else if (stack.getItem() instanceof ItemNetherStew)
		{
			tooltip.add(TextFormatting.YELLOW + I18n.format("tootip.mod_lavacow.NetherStew"));
		}	

	}*/
    
    /*@SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FogDensity event)
    {
    	//List<EntityZombieMushroom> players = event.getEntity().getEntityWorld().getEntitiesWithinAABB(EntityZombieMushroom.class, event.getEntity().getEntityBoundingBox().expand(40, 40, 2));
    	Entity players = event.getEntity().getEntityWorld().findNearestEntityWithinAABB(EntityFoglet.class, event.getEntity().getEntityBoundingBox().expand(15.0D, 15.0D, 10.0D), event.getEntity());
    	
    	if (players != null && !players.isBurning() && event.getEntity().isInsideOfMaterial(Material.AIR))
    	{
    		Double distance = event.getEntity().getPosition().getDistance(players.getPosition().getX(), players.getPosition().getY(), players.getPosition().getZ());
    		event.setDensity((float) (0.003D/(distance*distance + 0.1D)));
    	} 
    	else
    	{
    		event.setDensity(0.0F);
    	}
  
    	event.setCanceled(true); // must cancel event for event handler to take effect
    }

    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority=EventPriority.NORMAL, receiveCanceled=true)
    public void onEvent(FogColors event)
    {
    	Entity players = event.getEntity().getEntityWorld().findNearestEntityWithinAABB(EntityFoglet.class, event.getEntity().getEntityBoundingBox().expand(15.0D, 15.0D, 10.0D), event.getEntity());
    	
    	if (players != null && !players.isBurning())
    	{
    		Color theColor = new Color(180, 255, 180);
    		event.setRed(theColor.getRed());
    		event.setGreen(theColor.getGreen());
    		event.setBlue(theColor.getBlue());
    	}   
    }*/
}
