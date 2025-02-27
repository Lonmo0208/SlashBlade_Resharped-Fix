/* 
 * PROJECT: NyMmd
 * --------------------------------------------------------------------------------
 * The MMD for Java is Java version MMD Motion player class library.
 * NyMmd is modules which removed the ARToolKit origin codes from ARTK_MMD,
 * and was ported to Java. 
 *
 * This is based on the ARTK_MMD v0.1 by PY.
 * http://ppyy.if.land.to/artk_mmd.html
 * py1024<at>gmail.com
 * http://www.nicovideo.jp/watch/sm7398691
 *
 * 
 * The MIT License
 * Copyright (C)2008-2012 nyatla
 * nyatla39<at>gmail.com
 * http://nyatla.jp
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */
package jp.nyatla.nymmd;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import jp.nyatla.nymmd.core.PmdBone;
import jp.nyatla.nymmd.core.PmdFace;
import jp.nyatla.nymmd.core.PmdIK;
import jp.nyatla.nymmd.types.FaceData;
import jp.nyatla.nymmd.types.MmdMatrix;
import jp.nyatla.nymmd.types.MmdVector3;
import jp.nyatla.nymmd.types.MotionData;

import java.util.Map;
import java.util.stream.IntStream;

public abstract class MmdMotionPlayer {
    protected MmdPmdModel_BasicClass _ref_pmd_model;
    protected MmdVmdMotion_BasicClass _ref_vmd_motion;

    private PmdBone[] m_ppBoneList;
    private PmdFace[] m_ppFaceList;

    public MmdMatrix[] _skinning_mat;
    public Map<String, Integer> boneNameToIndex = Maps.newHashMap();

    public int getBoneIndexByName(String name) {
        return boneNameToIndex.getOrDefault(name, -1);
    }

    public PmdBone getBoneByName(String name) {
        int idx = getBoneIndexByName(name);
        return idx < 0 ? null : this._ref_pmd_model.getBoneArray()[idx];
    }

    private PmdBone m_pNeckBone;

    public MmdMotionPlayer() {}

    public void setPmd(MmdPmdModel_BasicClass i_pmd_model) throws MmdException {
        this._ref_pmd_model = i_pmd_model;
        PmdBone[] bone_array = i_pmd_model.getBoneArray();
        this._skinning_mat = MmdMatrix.createArray(bone_array.length);

        boneNameToIndex.clear();
        for (int i = 0; i < bone_array.length; i++) {
            boneNameToIndex.put(bone_array[i].getName(), i);
        }

        // 首^H頭のボーンを探しておく
        this.m_pNeckBone = null;
        int headIdx = boneNameToIndex.getOrDefault("頭", -1);
        if (headIdx >= 0 && headIdx < bone_array.length) {
            this.m_pNeckBone = bone_array[headIdx];
        }

        // PMD/VMDが揃った？
        if (this._ref_vmd_motion != null) {
            makeBoneFaceList();
        }
    }

    public void setVmd(MmdVmdMotion_BasicClass i_vmd_model) throws MmdException {
        if (this._ref_vmd_motion != i_vmd_model) {
            this._ref_vmd_motion = i_vmd_model;
            if (this._ref_pmd_model != null) {
                makeBoneFaceList();
            }
        }
    }

    private void makeBoneFaceList() {
        MotionData[] pMotionDataList = _ref_vmd_motion.refMotionDataArray();
        this.m_ppBoneList = new PmdBone[pMotionDataList.length];
        for (int i = 0; i < pMotionDataList.length; i++) {
            this.m_ppBoneList[i] = _ref_pmd_model.getBoneByName(pMotionDataList[i].szBoneName);
        }

        FaceData[] pFaceDataList = _ref_vmd_motion.refFaceDataArray();
        this.m_ppFaceList = new PmdFace[pFaceDataList.length];
        for (int i = 0; i < pFaceDataList.length; i++) {
            this.m_ppFaceList[i] = _ref_pmd_model.getFaceByName(pFaceDataList[i].szFaceName);
        }
    }

    public float getTimeLength() {
        return (float) (this._ref_vmd_motion.getMaxFrame() * (100.0 / 3));
    }

    public void updateMotion(float i_position_in_msec) throws MmdException {
        final PmdIK[] ik_array = this._ref_pmd_model.getIKArray();
        final PmdBone[] bone_array = this._ref_pmd_model.getBoneArray();
        assert i_position_in_msec >= 0;

        float frame = (float) (i_position_in_msec / (100.0 / 3));
        if (frame > this._ref_vmd_motion.getMaxFrame()) {
            frame = this._ref_vmd_motion.getMaxFrame();
        }

        this.updateFace(frame);

        for (PmdBone bone : bone_array) {
            bone.reset();
        }

        this.updateBone(frame);

        eventBus.post(new UpdateBoneEvent.Pre(bone_array, this));

        for (PmdBone bone : bone_array) {
            bone.updateMatrix();
        }

        for (PmdIK ik : ik_array) {
            ik.update();
        }

        eventBus.post(new UpdateBoneEvent.Post(bone_array, this));

        if (this._lookme_enabled) {
            this.updateNeckBone();
        }

        for (int i = 0; i < bone_array.length; i++) {
            bone_array[i].updateSkinningMat(this._skinning_mat[i]);
        }
        this.onUpdateSkinningMatrix(this._skinning_mat);
    }

    protected abstract void onUpdateSkinningMatrix(MmdMatrix[] i_skinning_mat) throws MmdException;

    public final EventBus eventBus = new EventBus();

    static public class UpdateBoneEvent {
        public final PmdBone[] bones;
        public final MmdMotionPlayer motionPlayer;

        public UpdateBoneEvent(PmdBone[] bones, MmdMotionPlayer motionPlayer) {
            this.bones = bones;
            this.motionPlayer = motionPlayer;
        }

        static public class Pre extends UpdateBoneEvent {
            public Pre(PmdBone[] bones, MmdMotionPlayer motionPlayer) {
                super(bones, motionPlayer);
            }
        }

        static public class Post extends UpdateBoneEvent {
            public Post(PmdBone[] bones, MmdMotionPlayer motionPlayer) {
                super(bones, motionPlayer);
            }
        }
    }

    private MmdVector3 _looktarget = new MmdVector3();
    private boolean _lookme_enabled = false;

    public void setLookVector(float i_x, float i_y, float i_z) {
        this._looktarget.x = i_x;
        this._looktarget.y = i_y;
        this._looktarget.z = i_z;
    }

    public void lookMeEnable(boolean i_enable) {
        this._lookme_enabled = i_enable;
    }

    private void updateNeckBone() {
        if (this.m_pNeckBone != null) {
            this.m_pNeckBone.lookAt(this._looktarget);
            for (PmdBone bone : this._ref_pmd_model.getBoneArray()) {
                bone.updateMatrix();
            }
        }
    }

    private void updateBone(float i_frame) throws MmdException {
        MotionData[] pMotionDataList = _ref_vmd_motion.refMotionDataArray();
        for (int i = 0; i < pMotionDataList.length; i++) {
            if (this.m_ppBoneList[i] != null) {
                pMotionDataList[i].getMotionPosRot(i_frame, this.m_ppBoneList[i]);
            }
        }
    }

    private void updateFace(float i_frame) throws MmdException {
        MmdVector3[] position_array = this._ref_pmd_model.getPositionArray();
        FaceData[] pFaceDataList = _ref_vmd_motion.refFaceDataArray();
        for (int i = 0; i < pFaceDataList.length; i++) {
            float fFaceRate = getFaceRate(pFaceDataList[i], i_frame);
            if (this.m_ppFaceList[i] != null && fFaceRate > 0.001f) {
                this.m_ppFaceList[i].blendFace(position_array, fFaceRate);
            }
        }
    }

    private float getFaceRate(FaceData pFaceData, float fFrame) {
        int ulNumKeyFrame = pFaceData.ulNumKeyFrames;
        if (fFrame > pFaceData.pKeyFrames[ulNumKeyFrame - 1].fFrameNo) {
            fFrame = pFaceData.pKeyFrames[ulNumKeyFrame - 1].fFrameNo;
        }

        int i = 0;
        while (i < ulNumKeyFrame && fFrame > pFaceData.pKeyFrames[i].fFrameNo) {
            i++;
        }

        int lKey0 = Math.max(i - 1, 0);
        int lKey1 = i == ulNumKeyFrame ? ulNumKeyFrame - 1 : i;

        float fTime0 = pFaceData.pKeyFrames[lKey0].fFrameNo;
        float fTime1 = pFaceData.pKeyFrames[lKey1].fFrameNo;

        if (lKey0 != lKey1) {
            float fLerpValue = (fFrame - fTime0) / (fTime1 - fTime0);
            return pFaceData.pKeyFrames[lKey0].fRate * (1.0f - fLerpValue) + pFaceData.pKeyFrames[lKey1].fRate * fLerpValue;
        } else {
            return pFaceData.pKeyFrames[lKey0].fRate;
        }
    }
}
