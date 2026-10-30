package quitebetter.core;

import quitebetter.core.achivement.ModAchievements;
import quitebetter.core.block.ModBlocks;
import quitebetter.core.crafting.ModRecipes;
import quitebetter.core.feature.ModFeatures;
import quitebetter.core.item.ModItems;
import quitebetter.core.tileentity.TileEntityCrate;
import quitebetter.core.tileentity.TileEntityFan;
import quitebetter.core.tileentity.TileEntityGlued;
import quitebetter.core.tileentity.TileEntityPedestal;
import quitebetter.core.tool.ModMiningLevels;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.util.collection.NamespaceID;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModCore implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("quitebetter");
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String VERSION = "1.0.6";
	@Override
	public void onInitialize() {
		LOGGER.info("Initializing "+MOD_ID+" "+VERSION);
		Key key = Key.of(MOD_ID);
		CommonEvents.BEFORE_GAME_START.listen(key, ModCore::beforeGameStart);
		CommonEvents.RECIPES_NAMESPACE_INIT.listen(key, ModRecipes::SetupNamespace);
		CommonEvents.RECIPES_READY.listen(key, ModCore::onRecipesReady);
	}

	private static void beforeGameStart() {
		ModConfig.Setup();
		ModBlocks.Setup();
		ModItems.Setup();
		TileEntityDispatcher.addMapping(TileEntityFan.class, new NamespaceID(MOD_ID, "Fan"));
		TileEntityDispatcher.addMapping(TileEntityPedestal.class, new NamespaceID(MOD_ID, "Pedestal"));
		TileEntityDispatcher.addMapping(TileEntityGlued.class, new NamespaceID(MOD_ID, "Glued"));
		TileEntityDispatcher.addMapping(TileEntityCrate.class, new NamespaceID(MOD_ID, "Crate"));
		ModMiningLevels.Setup();
		ModFeatures.Setup();
	}

	public static void onStatInitialize() {
		ModAchievements.Setup();
	}

	private static void onRecipesReady() {
		ModRecipes.SetupCrafts();
		ModRecipes.SetupCorrections();
	}

}
