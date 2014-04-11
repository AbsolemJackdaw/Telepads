package telepads;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import telepads.block.TESRTelePad;
import telepads.block.TETelepad;
import telepads.util.sndmngr;
import cpw.mods.fml.client.registry.ClientRegistry;

public class CLProxy extends SProxy{

	@Override
	public void registerItemRenderer(){
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(Telepads.telepad), new ItemPadRenderer());
	}

	@Override
	public void registerPacketHandlers() {
		Telepads.Channel.register(new Clientpacket());
	}
	@Override
	public void registerSound(){

		MinecraftForge.EVENT_BUS.register(new sndmngr());
	}

	@Override
	public void registerTileEntity() {
		ClientRegistry.bindTileEntitySpecialRenderer(TETelepad.class, new TESRTelePad());
	}

}
