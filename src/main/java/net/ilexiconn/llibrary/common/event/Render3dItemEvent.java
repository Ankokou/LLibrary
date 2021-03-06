package net.ilexiconn.llibrary.common.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.model.ModelBase;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * @author iLexiconn
 * @since 0.1.0
 */
@Cancelable
public class Render3dItemEvent extends Event
{
    public final Item item;
    public final ModelBase model;
    public final ResourceLocation texture;

    public final float x;
    public final float y;
    public final float z;

    private Render3dItemEvent(Item t, ModelBase m, ResourceLocation r, float i, float j, float k)
    {
        item = t;
        model = m;
        texture = r;

        x = i;
        y = j;
        z = k;
    }

    /**
     * @author iLexiconn
     * @since 0.2.0
     */
    public static class Pre extends Render3dItemEvent
    {
        public Pre(Item t, ModelBase m, ResourceLocation r, float i, float j, float k)
        {
            super(t, m, r, i, j, k);
        }
    }

    /**
     * @author iLexiconn
     * @since 0.2.0
     */
    public static class Post extends Render3dItemEvent
    {
        public Post(Item t, ModelBase m, ResourceLocation r, float i, float j, float k)
        {
            super(t, m, r, i, j, k);
        }
    }
}
