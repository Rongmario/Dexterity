package zone.rong.dexterity.api.skill;

import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraft.client.renderer.ItemRenderer;

@FunctionalInterface
public interface ItemRenderInToast extends RenderInToast {

    void render(ItemRenderer itemRenderer, int heightOfToast, int widthOfToast);

    @Override
    default void render(IToast toast, ToastGui gui, int heightOfToast, int widthOfToast) {
        this.render(gui.getMinecraft().getItemRenderer(), heightOfToast, widthOfToast);
    }

}
