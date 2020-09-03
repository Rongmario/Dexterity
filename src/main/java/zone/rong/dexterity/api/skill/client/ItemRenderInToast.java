package zone.rong.dexterity.api.skill.client;

import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface ItemRenderInToast extends RenderInToast {

    void render(ItemRenderer itemRenderer, int heightOfToast, int widthOfToast);

    @Override
    default void render(IToast toast, ToastGui gui, int heightOfToast, int widthOfToast) {
        this.render(gui.getMinecraft().getItemRenderer(), heightOfToast, widthOfToast);
    }

}
