package quitebetter.client.itemmodel;

import net.minecraft.client.render.item.model.ItemModel;
import quitebetter.core.item.ModItems;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.item.Item;
import org.jetbrains.annotations.NotNull;

import static quitebetter.core.ModCore.MOD_ID;

public class ItemModels {
	enum ModelTypes {
		ITEM, TOOL, SACK
	}
	private static ItemModel Add(@NotNull Item item, String texture, @NotNull ItemModels.ModelTypes modeltype) {
		switch (modeltype) {
			case TOOL:
				return new ItemModelStandard(item, false)
					.setIcon(MOD_ID+":item/"+texture)
					.setDisplayPos("firstperson_righthand", ItemModelDispatcher.HANDHELD_FIRST_PERSON_RIGHT_HAND)
					.setDisplayPos("firstperson_lefthand", ItemModelDispatcher.HANDHELD_FIRST_PERSON_LEFT_HAND)
					.setDisplayPos("thirdperson_righthand", ItemModelDispatcher.HANDHELD_THIRD_PERSON_RIGHT_HAND)
					.setDisplayPos("thirdperson_lefthand", ItemModelDispatcher.HANDHELD_THIRD_PERSON_LEFT_HAND);
			case SACK:
				return new ItemModelSack(item, false)
				.setIcon(MOD_ID+":item/"+texture);
			case ITEM:
			default:
				return new ItemModelStandard(item, false)
				.setIcon(MOD_ID+":item/"+texture);
		}
	}
	public static void Setup(ItemModelDispatcher d) {
		d.addDispatch(Add(ModItems.CLIMBING_HOOK,"climbing_hook", ModelTypes.TOOL));
		d.addDispatch(Add(ModItems.WRENCH,"wrench", ModelTypes.TOOL));
		d.addDispatch(Add(ModItems.META_WRENCH,"meta_wrench", ModelTypes.TOOL));
		d.addDispatch(Add(ModItems.SEASHELL,"seashell", ModelTypes.ITEM));
		d.addDispatch(new ItemModelSteelFishingRod(ModItems.TOOL_STEEL_FISHINGROD)
			.setDisplayPos("firstperson_righthand", ItemModelDispatcher.HANDHELD_ROD_FIRST_PERSON_RIGHT_HAND)
			.setDisplayPos("firstperson_lefthand", ItemModelDispatcher.HANDHELD_ROD_FIRST_PERSON_LEFT_HAND)
			.setDisplayPos("thirdperson_righthand", ItemModelDispatcher.HANDHELD_ROD_THIRD_PERSON_RIGHT_HAND)
			.setDisplayPos("thirdperson_lefthand", ItemModelDispatcher.HANDHELD_ROD_THIRD_PERSON_LEFT_HAND));
		d.addDispatch(Add(ModItems.PEDESTAL,"pedestal", ModelTypes.ITEM));
		d.addDispatch(Add(ModItems.AMMO_ARROW_TORCH,"ammo_arrow_torch", ModelTypes.ITEM));
		d.addDispatch(Add(ModItems.SACK,"sack", ModelTypes.SACK));
	}
}
