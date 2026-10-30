package quitebetter.core.util;

import net.minecraft.core.item.ItemStack;

import java.util.function.UnaryOperator;

public interface IBasketContents {
	void quitebetter$transferContents(UnaryOperator<ItemStack> transfer);
}
