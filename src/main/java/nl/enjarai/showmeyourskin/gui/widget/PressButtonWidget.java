package nl.enjarai.showmeyourskin.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import nl.enjarai.showmeyourskin.ShowMeYourSkin;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class PressButtonWidget extends PressableWidget {
    protected static final ButtonTextures BACKGROUND_TEXTURES = new ButtonTextures(
            ShowMeYourSkin.id("button/background"),
            ShowMeYourSkin.id("button/background_disabled"),
            ShowMeYourSkin.id("button/background_highlighted"),
            ShowMeYourSkin.id("button/background_highlighted_disabled")
    );

    protected final ButtonTextures textures;
    private final Consumer<PressButtonWidget> pressAction;
    @Nullable
    protected final Text tooltip;

    public PressButtonWidget(int x, int y, int width, int height, ButtonTextures textures,
                             Consumer<PressButtonWidget> pressAction, @Nullable Text tooltip) {
        super(x, y, width, height, ScreenTexts.EMPTY);
        this.textures = textures;
        this.pressAction = pressAction;
        this.tooltip = tooltip;
    }

    @Override
    public void onPress() {
        pressAction.accept(this);
    }

    public boolean isEnabled() {
        return true;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.setShaderColor(1.0f, 1.0f, 1.0f, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        renderButtonBackground(context, mouseX, mouseY, delta);
        context.drawGuiTexture(textures.get(isEnabled(), isSelected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());

        context.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    protected void renderButtonBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawGuiTexture(BACKGROUND_TEXTURES.get(isEnabled(), isSelected()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        appendDefaultNarrations(builder);
    }

    public static ButtonTextures createTextures(String path) {
        return new ButtonTextures(
                ShowMeYourSkin.id("button/" + path), ShowMeYourSkin.id("button/" + path)
        );
    }
}
