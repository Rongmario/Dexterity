package zone.rong.dexterity.api.skill.client;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.ToastGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface RenderInToast {

    void render(IToast toast, ToastGui gui, int heightOfToast, int widthOfToast);

}
