package quitebetter.core.item.glove;

import net.minecraft.core.item.Item;

public class ItemGloveSteel extends Item {
	public ItemGloveSteel(String name, String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	public int getArmorPiece() {
		return 2;
	}
}
