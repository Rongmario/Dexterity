/*
 *   Copyright (c) 2020 Rongmario
 *
 *   Permission is hereby granted, free of charge, to any person obtaining
 *   a copy of this software and associated documentation files (the
 *   "Software"), to deal in the Software without restriction, including
 *   without limitation the rights to use, copy, modify, merge, publish,
 *   distribute, sublicense, and/or sell copies of the Software, and to
 *   permit persons to whom the Software is furnished to do so, subject to
 *   the following conditions:
 *
 *   The above copyright notice and this permission notice shall be
 *   included in all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *   EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 *   LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 *   OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 *   WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package zone.rong.dexterity.rpg.skill.client;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import zone.rong.dexterity.DexterityMain;
import zone.rong.dexterity.api.DexterityPackets;
import zone.rong.dexterity.rpg.skill.types.Skill;

@Environment(EnvType.CLIENT)
public class SkillScreen extends Screen {

    public static final Identifier TEXTURE = new Identifier("dexterity", "textures/gui/skill_menu.png");

    public static SkillScreen CACHE;
    private static Object2ObjectMap<Skill<?>, ImmutableSkillEntry> clientPlayerLevels = new Object2ObjectRBTreeMap<>();

    public static void receivePacket() {
        ClientSidePacketRegistry.INSTANCE.register(DexterityPackets.S2C_SKILLS_RESPONSE, (ctx, packet) -> {
            final Object2ObjectMap<Skill<?>, ImmutableSkillEntry> packetMap = DexterityPackets.readPacket(packet);
            ctx.getTaskQueue().execute(() -> {
                clientPlayerLevels = packetMap;
                if (CACHE == null) {
                    CACHE = new SkillScreen();
                }
                MinecraftClient.getInstance().openScreen(CACHE);
            });
        });
    }

    // Background (texture) starting from 0, 0 -> 194, 160
    private final int backgroundWidth = 195, backgroundHeight = 163;
    private final int buttonWidth = 160, buttonHeight = 20;
    private final int scrollX = 195, pressedScrollX = 207; // 12 wide (-> 207, -> 219 respectively)
    private final int scrollYEnd = 20;

    private boolean notYetPopulated = true;

    private int x, y, lastScrollY;
    private int scrollBarTop;
    private boolean scrollbarClicked = false;

    // 140 bar length, 20 = each element height

    public SkillScreen() {
        super(new LiteralText("Hey there narration"));
    }

    @Override
    public void init(MinecraftClient client, int width, int height) {
        this.client = client;
        this.itemRenderer = client.getItemRenderer();
        this.textRenderer = client.textRenderer;
        this.width = width;
        this.height = height;
        this.setFocused(null);
        this.init();
    }

    @Override
    public void init() {
        this.x = (this.width - backgroundWidth) / 2;
        this.y = (this.height - backgroundHeight) / 2;
        this.scrollBarTop = this.y + 12;
        this.lastScrollY = this.scrollBarTop;
        int index = -1;
        for (Object2ObjectMap.Entry<Skill<?>, ImmutableSkillEntry> entry : clientPlayerLevels.object2ObjectEntrySet()) {
            Skill<?> skill = entry.getKey();
            if (this.notYetPopulated) {
                ButtonWidget button = createButton(skill, this.x + 9, this.scrollBarTop + (++index * buttonHeight), buttonWidth, buttonHeight, getSkillEntryFormattedText(skill, entry.getValue()), action -> DexterityMain.LOGGER.info("Topkek"));
                this.addButton(button);
            } else {
                ButtonWidget button = (ButtonWidget) this.buttons.get(++index);
                button.setMessage(getSkillEntryFormattedText(skill, entry.getValue()));
                button.x = this.x + 9;
                button.y = this.scrollBarTop + (index * buttonHeight);
            }
        }
        this.notYetPopulated = false;
    }

    @Override
    public void onClose() {
        clientPlayerLevels.clear();
        super.onClose();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float delta) {
        this.renderBackground(stack);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(TEXTURE);
        this.drawTexture(stack, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        if (this.scrollbarClicked) {
            this.renderScroller(stack, mouseY);
        } else {
            this.renderScroller(stack, this.lastScrollY);
        }
        resolveAndRenderButtons(stack, mouseX, mouseY, delta);
    }

    // Fixme: dynamic 'gaps' - right now its set to '20' (height of the buttons)
    protected void resolveAndRenderButtons(MatrixStack stack, int mouseX, int mouseY, float delta) {
        int position = Math.floorDiv(this.lastScrollY - this.scrollBarTop, 20); // CORRECT - GETS THE 'INDEX"
        for (int i = 0; i < this.buttons.size(); i++) {
            AbstractButtonWidget button = this.buttons.get(i);
            if (i < position || i > position + 6) {
                button.visible = false;
                button.active = false;
            } else {
                button.visible = true;
                button.active = true;
                button.y = this.scrollBarTop + ((i - position) * buttonHeight);
                button.render(stack, mouseX, mouseY, delta);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hasMouseOnScrollBar(mouseX, mouseY)) {
            this.scrollbarClicked = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.scrollbarClicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected void renderScroller(MatrixStack stack, int drop) {
        this.lastScrollY = Math.min(this.y + 132, Math.max(this.scrollBarTop, drop));
        this.drawTexture(stack, this.x + 175, this.lastScrollY, this.scrollbarClicked ? this.pressedScrollX : this.scrollX, 0, 12, this.scrollYEnd);
    }

    protected boolean hasMouseOnScrollBar(double mouseX, double mouseY) {
        return mouseX >= this.x + 175 && mouseX <= this.x + 187 && mouseY >= this.y + 12 && mouseY <= this.y + 152;
    }

    private Text getSkillEntryFormattedText(Skill skill, ImmutableSkillEntry entry) {
        return skill.getName().shallowCopy().append(" Lvl: ").append(new LiteralText(String.valueOf(entry.totalLevels)).formatted(Formatting.GREEN));
    }

    private ButtonWidget createButton(Skill skill, int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPress) {
        return new ButtonWidget(x, y, width, height, message, onPress) {
            @Override
            public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
                super.renderButton(matrices, mouseX, mouseY, delta);
                SkillScreen.this.client.getItemRenderer().renderGuiItemIcon(skill.getDisplayItem(), x + 2, y + 2);
            }
        };
    }

}
