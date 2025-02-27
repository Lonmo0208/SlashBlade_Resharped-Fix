package jp.nyatla.nymmd;

import net.minecraft.resources.ResourceLocation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MmdPmdModel extends MmdPmdModel_BasicClass {
	public MmdPmdModel(String i_pmd_file_path) throws FileNotFoundException, MmdException {
		super(new FileInputStream(i_pmd_file_path), new FileResourceProvider(i_pmd_file_path));
	}

	public MmdPmdModel(InputStream i_stream, IResourceProvider i_res_provider) throws MmdException {
		super(i_stream, i_res_provider);
	}

	protected static class FileResourceProvider implements IResourceProvider {
		String _dir;

		public FileResourceProvider(String i_pmd_file_path) {
			File f = new File(i_pmd_file_path);
			this._dir = f.getParentFile().getPath();
		}

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