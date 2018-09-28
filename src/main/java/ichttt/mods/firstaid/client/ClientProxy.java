package ichttt.mods.firstaid.client;

import ichttt.mods.firstaid.FirstAid;
import ichttt.mods.firstaid.api.CapabilityExtendedHealthSystem;
import ichttt.mods.firstaid.client.gui.GuiHealthScreen;
import ichttt.mods.firstaid.client.util.EventCalendar;
import ichttt.mods.firstaid.common.FirstAidConfig;
import ichttt.mods.firstaid.common.IProxy;
import ichttt.mods.firstaid.common.config.ConfigEntry;
import ichttt.mods.firstaid.common.config.ExtraConfig;
import ichttt.mods.firstaid.common.config.ExtraConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.client.GuiModsMissing;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import java.util.List;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxy implements IProxy {
    public static final KeyBinding showWounds = new KeyBinding("keybinds.show_wounds", KeyConflictContext.IN_GAME, Keyboard.KEY_H, FirstAid.NAME);
    public static List<ConfigEntry<ExtraConfig.Advanced>> advancedConfigOptions;

    @Override
    public void preInit() {
        FirstAid.LOGGER.debug("Loading ClientProxy");
        MinecraftForge.EVENT_BUS.register(ClientEventHandler.class);
        ClientRegistry.registerKeyBinding(showWounds);
    }

    @Override
    public void init() {
        GuiIngameForge.renderHealth = FirstAidConfig.overlay.showVanillaHealthBar;
        EventCalendar.checkDate();
        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(HUDHandler.INSTANCE);
        advancedConfigOptions = ExtraConfigManager.getAnnotatedFields(ExtraConfig.Advanced.class, FirstAidConfig.class);
    }

    @Override
    public void showGuiApplyHealth(EnumHand activeHand) {
        Minecraft mc = Minecraft.getMinecraft();
        GuiHealthScreen.INSTANCE = new GuiHealthScreen(mc.player.getCapability(CapabilityExtendedHealthSystem.INSTANCE, null), activeHand);
        mc.displayGuiScreen(GuiHealthScreen.INSTANCE);
    }

    @Override
    public void throwWrongPlayerRevivalException() {
        throw new CustomModLoadingErrorDisplayException() {
            private final MissingModsException wrappedEx = new MissingModsException(FirstAid.MODID, FirstAid.NAME);
            private final GuiModsMissing base = new GuiModsMissing(wrappedEx);
            @Override
            public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
                wrappedEx.addMissingMod(VersionParser.parseVersionReference("playerrevive@[1.2.19,)"), new ArtifactVersion() {
                    @Override
                    public String getLabel() {
                        return null;
                    }

                    @Override
                    public String getVersionString() {
                        return "an old version";
                    }

                    @Override
                    public boolean containsVersion(ArtifactVersion source) {
                        return false;
                    }

                    @Override
                    public String getRangeString() {
                        return "any";
                    }

                    @Override
                    public int compareTo(ArtifactVersion o) {
                        return 0;
                    }
                }, false);
                ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
                int i = scaledresolution.getScaledWidth();
                int j = scaledresolution.getScaledHeight();
                base.setWorldAndResolution(Minecraft.getMinecraft(), i, j);
            }

            @Override
            public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
                base.drawScreen(mouseRelX, mouseRelY, 0);
            }
        };
    }
}
