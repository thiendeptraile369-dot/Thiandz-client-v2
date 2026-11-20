package com.thiandz.client.gui;

import com.thiandz.client.Lang;
import com.thiandz.client.Theme;
import com.thiandz.client.ThiandzClient;
import com.thiandz.client.config.ConfigManager;
import com.thiandz.client.module.CrosshairModule;
import com.thiandz.client.module.HudModule;
import com.thiandz.client.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/** Bảng điều khiển tím: HUD / Chat / Zoom / Giao diện / Khác. */
public class ThiandzScreen extends Screen {
    private enum Tab {
        HUD("tab.hud"), CHAT("tab.chat"), ZOOM("tab.zoom"), THEME("tab.theme"), CROSSHAIR("tab.crosshair"), OTHER("tab.other");
        final String key;
        Tab(String key) { this.key = key; }
    }

    private static final int PANEL_W = 380, PANEL_H = 250, PER_PAGE = 10;

    private final Screen parent;
    private Tab tab = Tab.HUD;
    private HudModule selected = null;
    private int page = 0;
    private int left, top;

    public ThiandzScreen() { this(null); }

    public ThiandzScreen(Screen parent) {
        super(Text.literal("Thiandz Client"));
        this.parent = parent;
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    protected void init() {
        left = (width - PANEL_W) / 2;
        top = Math.max(6, (height - PANEL_H) / 2);

        Tab[] tabs = Tab.values();
        for (int i = 0; i < tabs.length; i++) {
            Tab t = tabs[i];
            ButtonWidget b = ButtonWidget.builder(Lang.text(t.key), btn -> {
                tab = t;
                selected = null;
                page = 0;
                clearAndInit();
            }).dimensions(left + 10 + i * 60, top + 24, 58, 20).build();
            b.active = (t != tab);
            addDrawableChild(b);
        }

        switch (tab) {
            case HUD -> initHud();
            case CHAT -> initChat();
            case ZOOM -> initZoom();
            case THEME -> initTheme();
            case CROSSHAIR -> initCrosshair();
            case OTHER -> initOther();
        }

        addDrawableChild(ButtonWidget.builder(Lang.text("ui.done"), b -> close())
            .dimensions(left + PANEL_W / 2 - 50, top + PANEL_H - 28, 100, 20).build());
    }

    private int pages() {
        return Math.max(1, (ModuleManager.hudModules().size() + PER_PAGE - 1) / PER_PAGE);
    }

    // ---------------- Tab HUD ----------------
    private void initHud() {
        int cy = top + 54;
        if (selected == null) {
            var list = ModuleManager.hudModules();
            page = Math.max(0, Math.min(page, pages() - 1));
            int start = page * PER_PAGE;
            for (int i = 0; i < PER_PAGE && start + i < list.size(); i++) {
                HudModule m = list.get(start + i);
                int col = i % 2, row = i / 2;
                int x = left + 20 + col * 176;
                int y = cy + row * 24;
                addToggle(x, y, 146, m.getName(), m::isEnabled, m::setEnabled);
                addDrawableChild(ButtonWidget.builder(Text.literal("..."), b -> {
                    selected = m;
                    clearAndInit();
                }).dimensions(x + 148, y, 22, 20).build());
            }
            if (pages() > 1) {
                int ny = cy + 5 * 24 + 6;
                addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> {
                    page = Math.max(0, page - 1);
                    clearAndInit();
                }).dimensions(left + PANEL_W / 2 - 60, ny, 30, 20).build());
                addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> {
                    page = Math.min(pages() - 1, page + 1);
                    clearAndInit();
                }).dimensions(left + PANEL_W / 2 + 30, ny, 30, 20).build());
            }
        } else {
            HudModule m = selected;
            int sx = left + (PANEL_W - 200) / 2;
            int y = top + 64;
            addToggle(sx, y, 200, m.getName(), m::isEnabled, m::setEnabled);
            int maxX = client != null ? client.getWindow().getScaledWidth() : 480;
            int maxY = client != null ? client.getWindow().getScaledHeight() : 270;
            addDrawableChild(new ValueSlider(sx, y + 24, 200, "ui.x", 0, maxX, m.getX(),
                v -> m.setPosition((int) Math.round(v), m.getY())));
            addDrawableChild(new ValueSlider(sx, y + 48, 200, "ui.y", 0, maxY, m.getY(),
                v -> m.setPosition(m.getX(), (int) Math.round(v))));
            addDrawableChild(new ValueSlider(sx, y + 72, 200, "ui.opacity", 0, 100, m.getOpacity() * 100,
                v -> m.setOpacity((float) (v / 100.0))));
            addDrawableChild(new ValueSlider(sx, y + 96, 200, "ui.scale", 50, 200, m.getScale() * 100,
                v -> m.setScale((float) (v / 100.0))));
            addDrawableChild(ButtonWidget.builder(Lang.text("ui.back"), b -> {
                selected = null;
                clearAndInit();
            }).dimensions(sx, y + 124, 200, 20).build());
        }
    }

    // ---------------- Tab Chat ----------------
    private void initChat() {
        int sx = left + (PANEL_W - 240) / 2, cy = top + 54;
        var chat = ModuleManager.chat;
        addToggle(sx, cy, 240, Lang.t("module.chat"), chat::isEnabled, chat::setEnabled);
        addToggle(sx, cy + 24, 240, Lang.t("ui.timestamp"), chat::isTimestamp, chat::setTimestamp);
        addToggle(sx, cy + 48, 240, Lang.t("ui.antispam"), chat::isAntiSpam, chat::setAntiSpam);
    }

    // ---------------- Tab Zoom ----------------
    private void initZoom() {
        int sx = left + (PANEL_W - 240) / 2, cy = top + 54;
        var zoom = ModuleManager.zoom;
        addToggle(sx, cy, 240, Lang.t("module.zoom"), zoom::isEnabled, zoom::setEnabled);
        addDrawableChild(new ValueSlider(sx, cy + 24, 240, "ui.fov", 30, 70, zoom.getZoomFov(),
            v -> zoom.setZoomFov((int) Math.round(v))));
    }

    // ---------------- Tab Giao diện ----------------
    private void initTheme() {
        int sx = left + (PANEL_W - 240) / 2, cy = top + 54;
        addDrawableChild(ButtonWidget.builder(Lang.text("ui.accent"), b -> Theme.nextAccent())
            .dimensions(sx, cy, 240, 20).build());
        addDrawableChild(ButtonWidget.builder(Lang.text("ui.reset_pos"), b -> {
            for (HudModule m : ModuleManager.hudModules()) m.resetPosition();
        }).dimensions(sx, cy + 24, 240, 20).build());
        addToggle(sx, cy + 48, 240, Lang.t("ui.custom_title"),
            () -> Theme.customTitle, v -> Theme.customTitle = v);
    }

    // ---------------- Tab Tâm ngắm ----------------
    private Text styleText(CrosshairModule c) {
        return Text.literal(Lang.t("ui.cross_style") + ": §d" + Lang.t("crosshair.style." + c.getStyle()));
    }

    private void initCrosshair() {
        int sx = left + (PANEL_W - 240) / 2, cy = top + 54;
        var c = ModuleManager.crosshair;
        addToggle(sx, cy, 240, Lang.t("module.crosshair"), c::isEnabled, c::setEnabled);
        addDrawableChild(ButtonWidget.builder(styleText(c), b -> {
            c.nextStyle();
            b.setMessage(styleText(c));
        }).dimensions(sx, cy + 24, 240, 20).build());
        addDrawableChild(new ValueSlider(sx, cy + 48, 240, "ui.cross_size", 1, 20, c.getSize(),
            v -> c.setSize((int) Math.round(v))));
        addDrawableChild(new ValueSlider(sx, cy + 72, 240, "ui.cross_gap", 0, 10, c.getGap(),
            v -> c.setGap((int) Math.round(v))));
        addDrawableChild(new ValueSlider(sx, cy + 96, 240, "ui.cross_thick", 1, 4, c.getThickness(),
            v -> c.setThickness((int) Math.round(v))));
        addToggle(sx, cy + 120, 117, Lang.t("ui.cross_accent"), c::isUseAccent, c::setUseAccent);
        addToggle(sx + 123, cy + 120, 117, Lang.t("ui.cross_outline"), c::isOutline, c::setOutline);
    }

    // ---------------- Tab Khác ----------------
    private void initOther() {
        int sx = left + (PANEL_W - 240) / 2, cy = top + 54;
        var tip = ModuleManager.tooltip;
        var noti = ModuleManager.notifications;
        addToggle(sx, cy, 240, Lang.t("module.tooltip"), tip::isEnabled, tip::setEnabled);
        addDrawableChild(new ValueSlider(sx, cy + 24, 240, "ui.lowhp", 1, 10, noti.getLowHp() / 2.0,
            v -> noti.setLowHp((float) (v * 2.0))));
    }

    // ---------------- Helper ----------------
    private static Text toggleText(String name, boolean on) {
        return Text.literal(name + ": " + (on ? "§a" + Lang.t("ui.on") : "§c" + Lang.t("ui.off")));
    }

    private void addToggle(int x, int y, int w, String name, BooleanSupplier get, Consumer<Boolean> set) {
        addDrawableChild(ButtonWidget.builder(toggleText(name, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(toggleText(name, get.getAsBoolean()));
        }).dimensions(x, y, w, 20).build());
    }

    private static class ValueSlider extends SliderWidget {
        private final String labelKey;
        private final double min, max;
        private final Consumer<Double> setter;

        ValueSlider(int x, int y, int w, String labelKey, double min, double max, double current,
                    Consumer<Double> setter) {
            super(x, y, w, 20, Text.empty(), Math.max(0, Math.min(1, (current - min) / (max - min))));
            this.labelKey = labelKey;
            this.min = min;
            this.max = max;
            this.setter = setter;
            updateMessage();
        }

        private double real() { return min + value * (max - min); }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal(Lang.t(labelKey) + ": " + Math.round(real())));
        }

        @Override
        protected void applyValue() { setter.accept(real()); }
    }

    // ---------------- Vẽ ----------------
    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.renderBackground(ctx, mouseX, mouseY, delta); // nền mờ (blur)
        ctx.fillGradient(0, 0, width, height, 0x66200840, 0x99100420); // phủ tím nhẹ

        int a = 0xFF000000 | Theme.accent;
        int soft = 0x80000000 | Theme.accent;
        ctx.fillGradient(left, top, left + PANEL_W, top + PANEL_H, 0xF0281248, 0xF0120622);
        ctx.fill(left, top, left + PANEL_W, top + 2, a);                       // viền trên
        ctx.fill(left, top + PANEL_H - 1, left + PANEL_W, top + PANEL_H, soft); // viền dưới
        ctx.fill(left, top, left + 1, top + PANEL_H, soft);                    // viền trái
        ctx.fill(left + PANEL_W - 1, top, left + PANEL_W, top + PANEL_H, soft); // viền phải
        ctx.fill(left + 8, top + 47, left + PANEL_W - 8, top + 48, 0x40FFFFFF); // đường kẻ dưới tab
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        int cx = left + PANEL_W / 2;
        int a = 0xFF000000 | Theme.accent;

        ctx.drawText(textRenderer, "§l§dTHIANDZ §fCLIENT", left + 12, top + 9, 0xFFFFFFFF, true);
        String ver = "§71.0.0";
        ctx.drawText(textRenderer, ver, left + PANEL_W - 12 - textRenderer.getWidth(ver), top + 9, 0xFFFFFFFF, true);

        // gạch chân tab đang chọn
        int ti = tab.ordinal();
        ctx.fill(left + 10 + ti * 60, top + 45, left + 10 + ti * 60 + 58, top + 47, a);

        int cy = top + 54;
        switch (tab) {
            case HUD -> {
                if (selected != null) {
                    drawCentered(ctx, "§l" + selected.getName(), cx, top + 52);
                } else {
                    if (pages() > 1) drawCentered(ctx, "§7" + (page + 1) + "/" + pages(), cx, cy + 5 * 24 + 12);
                    drawCentered(ctx, "§7" + Lang.t("ui.hud_hint"), cx, top + 208);
                }
            }
            case ZOOM -> {
                String key = ThiandzClient.ZOOM_KEY.getBoundKeyLocalizedText().getString();
                drawCentered(ctx, "§7" + Lang.t("ui.zoom_key", key), cx, cy + 54);
            }
            case THEME -> {
                int sx = left + (PANEL_W - 240) / 2;
                ctx.fill(sx + 246, cy + 2, sx + 262, cy + 18, a);
            }
            case OTHER -> drawCentered(ctx, "§7" + Lang.t("ui.lang_hint"), cx, cy + 54);
            default -> {}
        }
    }

    private void drawCentered(DrawContext ctx, String s, int cx, int y) {
        ctx.drawText(textRenderer, s, cx - textRenderer.getWidth(s) / 2, y, 0xFFFFFFFF, true);
    }

    @Override
    public void removed() {
        ConfigManager.save();
    }
}
