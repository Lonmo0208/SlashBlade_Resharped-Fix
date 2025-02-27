package jp.nyatla.nymmd;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MmdVmdMotionMc extends MmdVmdMotion_BasicClass {
    static private InputStream getStream(ResourceLocation loc) throws IOException {
        return new BufferedInputStream(Minecraft.getInstance().getResourceManager().open(loc));
    }

    public MmdVmdMotionMc(ResourceLocation loc) throws IOException, MmdException {
        super(getStream(loc));
    }
}