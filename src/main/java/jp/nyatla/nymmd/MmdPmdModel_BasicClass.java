package jp.nyatla.nymmd;

import com.google.common.collect.Maps;
import jp.nyatla.nymmd.core.PmdBone;
import jp.nyatla.nymmd.core.PmdFace;
import jp.nyatla.nymmd.core.PmdIK;
import jp.nyatla.nymmd.struct.DataReader;
import jp.nyatla.nymmd.struct.pmd.*;
import jp.nyatla.nymmd.types.MmdTexUV;
import jp.nyatla.nymmd.types.MmdVector3;
import jp.nyatla.nymmd.types.PmdMaterial;
import jp.nyatla.nymmd.types.PmdSkinInfo;
import net.minecraft.resources.ResourceLocation;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Stream;

class DataComparator implements Comparator<PmdIK> {
    public int compare(PmdIK o1, PmdIK o2) {
        return (int) (o1.getSortVal() - o2.getSortVal());
    }
}

public abstract class MmdPmdModel_BasicClass {
    private String _name;
    private int _number_of_vertex;

    private PmdFace[] m_pFaceArray;
    private PmdBone[] m_pBoneArray;
    private Map<String, PmdBone> boneMap = Maps.newHashMap();
    private PmdIK[] m_pIKArray;

    private MmdVector3[] _position_array;
    private MmdVector3[] _normal_array;
    private MmdTexUV[] _texture_uv;
    private PmdSkinInfo[] _skin_info_array;
    private PmdMaterial[] _materials;
    private IResourceProvider _res_provider;

    public interface IResourceProvider {
        ResourceLocation getTextureStream(String i_name) throws MmdException;
    }

    public MmdPmdModel_BasicClass(InputStream i_stream, IResourceProvider i_provider) throws MmdException {
        initialize(i_stream);
        this._res_provider = i_provider;
    }

    public int getNumberOfVertex() {
        return this._number_of_vertex;
    }

    public PmdMaterial[] getMaterials() {
        return this._materials;
    }

    public MmdTexUV[] getUvArray() {
        return this._texture_uv;
    }

    public MmdVector3[] getPositionArray() {
        return this._position_array;
    }

    public MmdVector3[] getNormatArray() {
        return this._normal_array;
    }

    public PmdSkinInfo[] getSkinInfoArray() {
        return this._skin_info_array;
    }

    public PmdFace[] getFaceArray() {
        return this.m_pFaceArray;
    }

    public PmdBone[] getBoneArray() {
        return this.m_pBoneArray;
    }

    public PmdIK[] getIKArray() {
        return this.m_pIKArray;
    }

    public PmdBone getBoneByName(String i_name) {
        return boneMap.get(i_name);
    }

    public PmdFace getFaceByName(String i_name) {
        for (PmdFace face : this.m_pFaceArray) {
            if (face.getName().equals(i_name)) {
                return face;
            }
        }
        return null;
    }

    private void initialize(InputStream i_stream) throws MmdException {
        DataReader reader = new DataReader(i_stream);
        PMD_Header pPMDHeader = new PMD_Header();
        pPMDHeader.read(reader);
        if (!pPMDHeader.szMagic.equalsIgnoreCase("PMD")) {
            throw new MmdException();
        }

        this._name = pPMDHeader.szName;

        this._number_of_vertex = reader.readInt();
        if (this._number_of_vertex < 0) {
            throw new MmdException();
        }

        this._position_array = MmdVector3.createArray(this._number_of_vertex);
        this._normal_array = MmdVector3.createArray(this._number_of_vertex);
        this._texture_uv = MmdTexUV.createArray(this._number_of_vertex);
        this._skin_info_array = new PmdSkinInfo[this._number_of_vertex];

        PMD_Vertex tmp_pmd_vertex = new PMD_Vertex();
        for (int i = 0; i < _number_of_vertex; i++) {
            tmp_pmd_vertex.read(reader);
            _position_array[i].setValue(tmp_pmd_vertex.vec3Pos);
            _normal_array[i].setValue(tmp_pmd_vertex.vec3Normal);
            _texture_uv[i].setValue(tmp_pmd_vertex.uvTex);

            this._skin_info_array[i] = new PmdSkinInfo();
            this._skin_info_array[i].fWeight = tmp_pmd_vertex.cbWeight / 100.0f;
            this._skin_info_array[i].unBoneNo_0 = tmp_pmd_vertex.unBoneNo[0];
            this._skin_info_array[i].unBoneNo_1 = tmp_pmd_vertex.unBoneNo[1];
        }

        short[] indices_array = createIndicesArray(reader);

        int number_of_materials = reader.readInt();
        this._materials = new PmdMaterial[number_of_materials];

        PMD_Material tmp_pmd_material = new PMD_Material();
        int indices_ptr = 0;
        for (int i = 0; i < number_of_materials; i++) {
            tmp_pmd_material.read(reader);
            PmdMaterial pmdm = new PmdMaterial();
            pmdm.unknown = tmp_pmd_material.unknown;
            final int num_of_indices = tmp_pmd_material.ulNumIndices;

            pmdm.indices = new short[num_of_indices];
            System.arraycopy(indices_array, indices_ptr, pmdm.indices, 0, num_of_indices);
            indices_ptr += num_of_indices;

            pmdm.col4Diffuse.setValue(tmp_pmd_material.col4Diffuse);

            pmdm.col4Specular.r = tmp_pmd_material.col3Specular.r;
            pmdm.col4Specular.g = tmp_pmd_material.col3Specular.g;
            pmdm.col4Specular.b = tmp_pmd_material.col3Specular.b;
            pmdm.col4Specular.a = 1.0f;

            pmdm.col4Ambient.r = tmp_pmd_material.col3Ambient.r;
            pmdm.col4Ambient.g = tmp_pmd_material.col3Ambient.g;
            pmdm.col4Ambient.b = tmp_pmd_material.col3Ambient.b;
            pmdm.col4Ambient.a = 1.0f;

            pmdm.fShininess = tmp_pmd_material.fShininess;
            pmdm.texture_name = tmp_pmd_material.szTextureFileName.isEmpty() ? null : tmp_pmd_material.szTextureFileName;
            this._materials[i] = pmdm;
        }

        this.m_pBoneArray = createBoneArray(reader);
        boneMap.clear();
        for (PmdBone bone : this.m_pBoneArray) {
            this.boneMap.put(bone.getName(), bone);
        }

        this.m_pIKArray = createIKArray(reader, this.m_pBoneArray);
        this.m_pFaceArray = createFaceArray(reader);

        if (this.m_pFaceArray != null && this.m_pFaceArray.length > 0) {
            this.m_pFaceArray[0].setFace(this._position_array);
        }
    }

    private static short[] createIndicesArray(DataReader i_reader) throws MmdException {
        int num_of_indeces = i_reader.readInt();
        short[] result = new short[num_of_indeces];
        for (int i = 0; i < num_of_indeces; i++) {
            result[i] = i_reader.readShort();
        }
        return result;
    }

    private static PmdBone[] createBoneArray(DataReader i_reader) throws MmdException {
        final int number_of_bone = i_reader.readShort();
        PMD_Bone tmp_pmd_bone = new PMD_Bone();
        PmdBone[] result = new PmdBone[number_of_bone];
        for (int i = 0; i < number_of_bone; i++) {
            tmp_pmd_bone.read(i_reader);
            result[i] = new PmdBone(tmp_pmd_bone, result);
        }
        for (int i = 0; i < number_of_bone; i++) {
            result[i].recalcOffset();
        }
        return result;
    }

    private static PmdIK[] createIKArray(DataReader i_reader, PmdBone[] i_ref_bone_array) throws MmdException {
        final int number_of_ik = i_reader.readShort();
        PMD_IK tmp_pmd_ik = new PMD_IK();
        PmdIK[] result = new PmdIK[number_of_ik];
        if (number_of_ik > 0) {
            for (int i = 0; i < number_of_ik; i++) {
                tmp_pmd_ik.read(i_reader);
                result[i] = new PmdIK(tmp_pmd_ik, i_ref_bone_array);
            }
            Arrays.sort(result, new DataComparator());
        }
        return result;
    }

    private static PmdFace[] createFaceArray(DataReader i_reader) throws MmdException {
        final int number_of_face = i_reader.readShort();
        PMD_FACE tmp_pmd_face = new PMD_FACE();
        PmdFace[] result = new PmdFace[number_of_face];
        if (number_of_face > 0) {
            for (int i = 0; i < number_of_face; i++) {
                tmp_pmd_face.read(i_reader);
                result[i] = new PmdFace(tmp_pmd_face, result[0]);
            }
        }
        return result;
    }

    public String getModelName() {
        return this._name;
    }

    public IResourceProvider getResourceProvider() {
        return this._res_provider;
    }
}