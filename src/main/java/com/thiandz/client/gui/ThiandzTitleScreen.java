package com.thiandz.client.gui;

import com.thiandz.client.Theme;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/** Màn hình chính tím thay cho logo Minecraft: nền mờ, tiêu đề THIANDZ CLIENT. */
public class ThiandzTitleScreen extends Screen {
    public ThiandzTitleScreen() { super(Text.literal("Thiandz Client")); }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 12;

        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"),
            b -> client.setScreen(new SelectWorldScreen(this)))
            .dimensions(cx - 100, y, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"),
            b -> client.setScreen(new MultiplayerScreen(this)))
            .dimensions(cx - 100, y + 24, 200, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"),
            b -> client.setScreen(new OptionsScreen(this, client.options)))
            .dimensions(cx - 100, y + 48, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("§dThiandz"),
            b -> client.setScreen(new ThiandzScreen(this)))
            .dimensions(cx + 2, y + 48, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"),
            b -> client.scheduleStop())
            .dimensions(cx - 100, y + 72, 200, 20).build());
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta);
        // Phủ màu tím chuyển sắc lên nền mờ
        ctx.fillGradient(0, 0, width, height, 0x88300A66, 0xCC0E0320);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = width / 2;
        int ty = height / 2 - 12 - 62;

        String title = "§lTHIANDZ CLIENT";
        float s = 3f;
        int w = textRenderer.getWidth(title);
        var m = ctx.getMatrices();
        m.pushMatrix();
        m.translate(cx - w * s / 2f, (float) ty);
        m.scale(s, s);
        ctx.drawText(textRenderer, title, 0, 0, 0xFF000000 | Theme.accent, true);
        m.popMatrix();

        String sub = "§7Minecraft 1.21.11  |  Fabric";
        ctx.drawText(textRenderer, sub, cx - textRenderer.getWidth(sub) / 2, ty + 32, 0xFFFFFFFF, true);
        ctx.fill(cx - 90, ty + 46, cx + 90, ty + 48, 0xFF000000 | Theme.accent);
    }
}
