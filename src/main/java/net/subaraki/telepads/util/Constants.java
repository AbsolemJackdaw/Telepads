package net.subaraki.telepads.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Constants {

	public static final String MODID = "telepads";
	public static final String MOD_NAME = "Telepads";
	public static final String VERSION = "1.0.0";
	public static final String SERVER = "net.subaraki.telepads.common.CommonProxy";
	public static final String CLIENT = "net.subaraki.telepads.client.ClientProxy";
	public static final String FACTORY = "net.subaraki.telepads.client.gui.TelepadsGuiFactory";
	public static final String DEPENDENCY = "required-after:bookshelf";
	public static final Logger LOG = LogManager.getLogger(MOD_NAME);

	public static final int GUI_ID_TELEPORT = 0;
	public static final int GUI_ID_NAMEPAD = 1;
	public static final int GUI_ID_REMOVEPAD = 2;

}