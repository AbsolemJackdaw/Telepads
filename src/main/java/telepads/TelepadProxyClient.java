package telepads;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import telepads.block.RenderItemBlock;
import telepads.block.TESRTelePad;
import telepads.block.TETelepad;
import cpw.mods.fml.client.registry.ClientRegistry;

public class TelepadProxyClient extends TelepadProxyServer{

	@Override
	public void registerItemRenderer(){
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Telepads.telepad), new RenderItemBlock());
	}

	@Override
	public void registerPacketHandlers() {
//		Telepads.Channel.register(new Clientpacket());
	}

	@Override
	public void registerTileEntity() {
		ClientRegistry.bindTileEntitySpecialRenderer(TETelepad.class, new TESRTelePad());
	}

	@Override
	public EntityPlayer getClientPlayer(){
		return Minecraft.getMinecraft().thePlayer;
	}
}
