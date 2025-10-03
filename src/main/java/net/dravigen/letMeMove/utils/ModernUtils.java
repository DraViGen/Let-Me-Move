package net.dravigen.letMeMove.utils;

public class ModernUtils {
    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }
}
