package jp.nyatla.nymmd;

import jp.nyatla.nymmd.struct.DataReader;
import jp.nyatla.nymmd.struct.vmd.VMD_Face;
import jp.nyatla.nymmd.struct.vmd.VMD_Header;
import jp.nyatla.nymmd.struct.vmd.VMD_Motion;
import jp.nyatla.nymmd.types.*;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

class BoneCompare implements Comparator<BoneKeyFrame> {
    public int compare(BoneKeyFrame o1, BoneKeyFrame o2) {
        return Float.compare(o1.fFrameNo, o2.fFrameNo);
    }
}

class FaceCompare implements Comparator<FaceKeyFrame> {
    public int compare(FaceKeyFrame o1, FaceKeyFrame o2) {
        return Float.compare(o1.fFrameNo, o2.fFrameNo);
    }
}

public class MmdVmdMotion_BasicClass {
    private MotionData[] _motion_data_array;
    private FaceData[] _face_data_array;
    private float _fMaxFrame;

    public MmdVmdMotion_BasicClass(InputStream i_stream) throws MmdException {
        initialize(i_stream);
    }

    public MotionData[] refMotionDataArray() {
        return this._motion_data_array;
    }

    public FaceData[] refFaceDataArray() {
        return this._face_data_array;
    }

    public float getMaxFrame() {
        return this._fMaxFrame;
    }

    private boolean initialize(InputStream i_st) throws MmdException {
        if (i_st == null) {
            // 默认初始化逻辑
            this._motion_data_array = new MotionData[2];
            this._motion_data_array[0] = createDefaultMotionData("すべての親", 1.0f, new MmdVector3(0, 0, 0));
            this._motion_data_array[1] = createDefaultMotionData("右足ＩＫ", 0.0f, new MmdVector3(0, 5, 5));
            this._face_data_array = new FaceData[0];
            this._fMaxFrame = 60.0f;
            return true;
        }

        DataReader reader = new DataReader(i_st);

        // 检查头部
        VMD_Header tmp_vmd_header = new VMD_Header();
        tmp_vmd_header.read(reader);
        if (!tmp_vmd_header.szHeader.equalsIgnoreCase("Vocaloid Motion Data 0002")) {
            throw new MmdException();
        }

        // 读取骨骼和表情数据
        float[] max_frame = new float[1];
        this._motion_data_array = createMotionDataList(reader, max_frame);
        this._fMaxFrame = max_frame[0];

        this._face_data_array = createFaceDataList(reader, max_frame);
        this._fMaxFrame = Math.max(this._fMaxFrame, max_frame[0]);

        return true;
    }

    private static MotionData createDefaultMotionData(String boneName, float frameNo, MmdVector3 position) {
        MotionData motionData = new MotionData();
        motionData.szBoneName = boneName;
        motionData.ulNumKeyFrames = 1;
        motionData.pKeyFrames = new BoneKeyFrame[1];
        motionData.pKeyFrames[0] = new BoneKeyFrame();
        motionData.pKeyFrames[0].fFrameNo = frameNo;
        motionData.pKeyFrames[0].vec3Position.setValue(position);

        // 修复 MmdVector4 的初始化问题
        MmdVector4 rotation = new MmdVector4();
        rotation.x = 0;
        rotation.y = 0;
        rotation.z = 0;
        rotation.w = 1;
        motionData.pKeyFrames[0].vec4Rotate.setValue(rotation);

        return motionData;
    }

    private static FaceData[] createFaceDataList(DataReader i_reader, float[] o_max_frame) throws MmdException {
        int ulNumFaceKeyFrames = i_reader.readInt();
        VMD_Face[] tmp_vmd_face = new VMD_Face[ulNumFaceKeyFrames];
        for (int i = 0; i < ulNumFaceKeyFrames; i++) {
            tmp_vmd_face[i] = new VMD_Face();
            tmp_vmd_face[i].read(i_reader);
        }

        Map<String, FaceData> faceDataMap = new HashMap<>();
        float max_frame = 0.0f;

        for (VMD_Face face : tmp_vmd_face) {
            max_frame = Math.max(max_frame, face.ulFrameNo);
            faceDataMap.computeIfAbsent(face.szFaceName, k -> {
                FaceData data = new FaceData();
                data.szFaceName = k;
                data.ulNumKeyFrames = 0;
                data.pKeyFrames = new FaceKeyFrame[0];
                return data;
            }).ulNumKeyFrames++;
        }

        FaceData[] result = faceDataMap.values().toArray(new FaceData[0]);
        for (FaceData data : result) {
            data.pKeyFrames = FaceKeyFrame.createArray(data.ulNumKeyFrames);
            data.ulNumKeyFrames = 0;
        }

        for (VMD_Face face : tmp_vmd_face) {
            FaceData data = faceDataMap.get(face.szFaceName);
            FaceKeyFrame keyFrame = data.pKeyFrames[data.ulNumKeyFrames++];
            keyFrame.fFrameNo = face.ulFrameNo;
            keyFrame.fRate = face.fFactor;
        }

        for (FaceData data : result) {
            Arrays.sort(data.pKeyFrames, new FaceCompare());
        }

        o_max_frame[0] = max_frame;
        return result;
    }

    private static MotionData[] createMotionDataList(DataReader i_reader, float[] o_max_frame) throws MmdException {
        int ulNumBoneKeyFrames = i_reader.readInt();
        VMD_Motion[] tmp_vmd_motion = new VMD_Motion[ulNumBoneKeyFrames];
        for (int i = 0; i < ulNumBoneKeyFrames; i++) {
            tmp_vmd_motion[i] = new VMD_Motion();
            tmp_vmd_motion[i].read(i_reader);
        }

        Map<String, MotionData> motionDataMap = new HashMap<>();
        float max_frame = 0.0f;

        for (VMD_Motion motion : tmp_vmd_motion) {
            max_frame = Math.max(max_frame, motion.ulFrameNo);
            motionDataMap.computeIfAbsent(motion.szBoneName, k -> {
                MotionData data = new MotionData();
                data.szBoneName = k;
                data.ulNumKeyFrames = 0;
                data.pKeyFrames = new BoneKeyFrame[0];
                return data;
            }).ulNumKeyFrames++;
        }

        MotionData[] result = motionDataMap.values().toArray(new MotionData[0]);
        for (MotionData data : result) {
            data.pKeyFrames = BoneKeyFrame.createArray(data.ulNumKeyFrames);
            data.ulNumKeyFrames = 0;
        }

        for (VMD_Motion motion : tmp_vmd_motion) {
            MotionData data = motionDataMap.get(motion.szBoneName);
            BoneKeyFrame keyFrame = data.pKeyFrames[data.ulNumKeyFrames++];
            keyFrame.fFrameNo = motion.ulFrameNo;
            keyFrame.vec3Position.setValue(motion.vec3Position);
            keyFrame.vec4Rotate.QuaternionNormalize(motion.vec4Rotate);
        }

        for (MotionData data : result) {
            Arrays.sort(data.pKeyFrames, new BoneCompare());
        }

        o_max_frame[0] = max_frame;
        return result;
    }
}