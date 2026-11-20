package com.thiandz.client;

/** Màu nhấn + tùy chọn giao diện dùng chung. */
public final class Theme {
    private Theme() {}

    public static final int[] PRESETS = {
        0xA855F7, // tím (mặc định)
        0xD946EF, // hồng tím
        0x3B9BFF, // xanh dương
        0x3DDC84, // xanh lá
        0xFF5A5A, // đỏ
        0xFFA94D  // cam
    };

    public static int accent = PRESETS[0];
    /** Thay màn hình chính của Minecraft bằng màn hình tím của Thiandz Client. */
    public static boolean customTitle = true;

    public static void nextAccent() {
        int idx = 0;
        for (int i = 0; i < PRESETS.length; i++) if (PRESETS[i] == accent) idx = i;
        accent = PRESETS[(idx + 1) % PRESETS.length];
    }
}
