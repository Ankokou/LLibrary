package net.ilexiconn.llibrary.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.client.gui.GuiSurvivalTab;
import net.ilexiconn.llibrary.client.render.entity.RenderLLibraryPlayer;
import net.ilexiconn.llibrary.common.block.IHighlightedBlock;
import net.ilexiconn.llibrary.common.config.LLibraryConfigHandler;
import net.ilexiconn.llibrary.common.survivaltab.SurvivalTab;
import net.ilexiconn.llibrary.common.survivaltab.TabHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.ilexiconn.llibrary.client.screenshot.ScreenshotHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class ClientEventHandler
{
    public static KeyBinding screenshotKeyBinding;
    private RenderPlayer prevRenderPlayer;
    private Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Specials.Post event)
    {
        if (event.entityPlayer == mc.thePlayer)
        {
            if (prevRenderPlayer != null)
            {
                RenderManager.instance.entityRenderMap.put(event.entityPlayer.getClass(), prevRenderPlayer);
            }
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event)
    {
        EntityPlayer player = event.entityPlayer;

        if (mc.thePlayer == player)
        {
            Render entityRenderObject = RenderManager.instance.getEntityRenderObject(event.entityPlayer);

            if (!(entityRenderObject instanceof RenderLLibraryPlayer))
            {
                prevRenderPlayer = (RenderPlayer) entityRenderObject;
                RenderManager.instance.entityRenderMap.put(player.getClass(), ClientProxy.renderCustomPlayer);
            }
        }
    }

    @SubscribeEvent
    public void blockHighlight(DrawBlockHighlightEvent event)
    {
        if (event.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
        {
            int x = event.target.blockX;
            int y = event.target.blockY;
            int z = event.target.blockZ;

            Block block = event.player.worldObj.getBlock(x, y, z);

            if (block instanceof IHighlightedBlock)
            {
                List<AxisAlignedBB> bounds = ((IHighlightedBlock) block).getHighlightedBoxes(event.player.worldObj, x, y, z, event.player);

                Vec3 pos = event.player.getPosition(event.partialTicks);

                GL11.glEnable(GL11.GL_BLEND);

                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                GL11.glColor4f(0f, 0f, 0f, 0.4f);
                GL11.glLineWidth(2f);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDepthMask(false);

                for (AxisAlignedBB box : bounds)
                {
                    RenderGlobal.drawOutlinedBoundingBox(box.copy().offset(x, y, z).offset(-pos.xCoord, -pos.yCoord, -pos.zCoord), -1);
                }

                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_BLEND);

                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void itemTooltip(ItemTooltipEvent event)
    {
        if (Minecraft.getMinecraft().gameSettings.advancedItemTooltips)
        {
            event.toolTip.add(EnumChatFormatting.DARK_GRAY + "" + Item.itemRegistry.getNameForObject(event.itemStack.getItem()));
        }
    }

    @SubscribeEvent
    public void initGui(GuiScreenEvent.InitGuiEvent.Post event)
    {
        for (SurvivalTab survivalTab : TabHelper.getSurvivalTabs())
        {
            if (survivalTab.getSurvivalTab().getContainerGuiClass() != null && survivalTab.getSurvivalTab().getContainerGuiClass().isInstance(event.gui))
            {
                int count = 2;

                for (SurvivalTab tab : TabHelper.getSurvivalTabs())
                {
                    event.buttonList.add(new GuiSurvivalTab(count, tab));
                    count++;
                }
            }
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if (LLibraryConfigHandler.threadedScreenshots && ClientEventHandler.screenshotKeyBinding.isPressed())
        {
            ScreenshotHelper.takeScreenshot();
        }
    }
}