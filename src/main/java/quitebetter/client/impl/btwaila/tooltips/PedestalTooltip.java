package quitebetter.client.impl.btwaila.tooltips;

import net.minecraft.core.item.ItemStack;
import quitebetter.core.tileentity.TileEntityPedestal;
import toufoumaster.btwaila.gui.components.AdvancedInfoComponent;
import toufoumaster.btwaila.tooltips.TileTooltip;

public class PedestalTooltip extends TileTooltip<TileEntityPedestal> {
	@Override
	public void initTooltip() {
		addClass(TileEntityPedestal.class);
	}

	@Override
	public void drawAdvancedTooltip(TileEntityPedestal pedestal, AdvancedInfoComponent c) {
		if (pedestal.displayItem == null) return;
		int lineY = c.getOffY() + 5;
		c.setOffY(lineY);
		c.drawItemList(new ItemStack[]{pedestal.displayItem}, 0);
		c.setOffY(lineY);
		c.drawStringWithShadow(""+pedestal.displayItem.getDisplayName(), 18);
		c.setOffY(lineY + 16);
	}
}
