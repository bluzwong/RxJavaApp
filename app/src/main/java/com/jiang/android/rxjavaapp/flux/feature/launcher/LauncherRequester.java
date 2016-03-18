package com.jiang.android.rxjavaapp.flux.feature.launcher;

import com.github.bluzwong.myflux.lib.FluxFragmentRequester;
import com.jiang.android.rxjavaapp.database.alloperators;
import com.jiang.android.rxjavaapp.database.helper.DbUtil;
import com.jiang.android.rxjavaapp.database.operators;
import com.jiang.android.rxjavaapp.flux.action.DataAction;

import java.util.List;

import static com.jiang.android.rxjavaapp.flux.feature.launcher.Type.*;

public class LauncherRequester extends FluxFragmentRequester {

    public String fillData() {
        return doRequestComputation(new RequestAction() {
            @Override
            public void request(String uuid) {
                try {
                    List<operators> lists = DataAction.getOperatorsData();
                    List<alloperators> alloperatorses = DataAction.getAllOperators();
                    DbUtil.getOperatorsService().save(lists);
                    DbUtil.getAllOperatorsService().save(alloperatorses);
                    newFluxResponse(FILL_DATA_OK, uuid).post();
                } catch (Exception e) {
                    e.printStackTrace();
                    newFluxResponse(FILL_DATA_FAIL, uuid)
                            .putOnly(e)
                            .post();
                }
            }
        });
    }
}
