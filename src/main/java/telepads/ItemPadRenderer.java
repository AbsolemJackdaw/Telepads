package telepads;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import telepads.block.TETelepad;
import telepads.model.telepad;

public class ItemPadRenderer implements IItemRenderer {
	private telepad pad;

	private static ResourceLocation loc = new ResourceLocation("subaraki:pad/telepad.png");
	public ItemPadRenderer() {
		pad = new telepad();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(new TETelepad(), 0.0D, 0.0D, 0.0D, 0.0F);

	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}
}
