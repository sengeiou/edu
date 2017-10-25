package com.ubt.alpha1e.business;

import com.ubt.alpha1e.business.ActionPlayer.Play_type;
import com.ubt.alpha1e.data.model.ActionInfo;

import java.util.List;

public interface ActionPlayerListener {
    void notePlayStart(List<String> mSourceActionNameList,
                       ActionInfo action, Play_type mCurrentPlayType);

    void notePlayPause(List<String> mSourceActionNameList,
                       Play_type mCurrentPlayType);

    void notePlayContinue(List<String> mSourceActionNameList,
                          Play_type mCurrentPlayType);

    void notePlayFinish(List<String> mSourceActionNameList,
                        Play_type mCurrentPlayType, String hashCode);

    void notePlayChargingError();

    void notePlayCycleNext(String action_name);
}
