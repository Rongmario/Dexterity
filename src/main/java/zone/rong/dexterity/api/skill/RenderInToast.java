package zone.rong.dexterity.api.skill;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;

@FunctionalInterface
public interface RenderInToast {

    void render(IToast toast, ToastGui gui, int heightOfToast, int widthOfToast);

}
