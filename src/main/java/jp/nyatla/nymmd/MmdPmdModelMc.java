package jp.nyatla.nymmd;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;

public class MmdPmdModelMc extends MmdPmdModel_BasicClass {
    public MmdPmdModelMc(ResourceLocation loc) throws IOException, MmdException {
        super(Minecraft.getInstance().getResourceManager().open(loc), new FileResourceProvider());
    }

    public MmdPmdModelMc(InputStream i_stream, IResourceProvider i_res_provider) throws MmdException {
        super(i_stream, i_res_provider);
    }

    protected static class FileResourceProvider implements IResourceProvider {
        @Override
        public ResourceLocation getTextureStream(String i_name) throws MmdException {
            try {
                return new ResourceLocation(i_name);
            } catch (Exception e) {
                throw new MmdException(e);
            }
        }
    }
}