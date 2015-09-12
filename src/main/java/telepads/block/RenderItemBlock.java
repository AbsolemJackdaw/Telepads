package telepads.block;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import telepads.model.TelePadModel;

public class RenderItemBlock implements IItemRenderer {
	private TelePadModel pad;

	private static ResourceLocation loc = new ResourceLocation(
			"telepads:textures/telepad.png");

	public RenderItemBlock() {
		pad = new TelePadModel();
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(
				new TETelepad(), 0.0D, 0.0D, 0.0D, 0.0F);

	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}
}
