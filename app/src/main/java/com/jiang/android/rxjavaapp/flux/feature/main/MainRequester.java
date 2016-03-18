package com.jiang.android.rxjavaapp.flux.feature.main;

import com.github.bluzwong.myflux.lib.FluxFragmentRequester;
import com.jiang.android.rxjavaapp.database.alloperators;
import com.jiang.android.rxjavaapp.database.helper.DbUtil;
import com.jiang.android.rxjavaapp.database.operators;
import static com.jiang.android.rxjavaapp.flux.feature.main.Type.*;
import java.util.List;

public class MainRequester extends FluxFragmentRequester{

    public String fillOperators() {
        return doRequestIO(new RequestAction() {
            @Override
            public void request(String uuid) {
                try {
                    List<operators> operatorsList = DbUtil.getOperatorsService().queryAll();
                    newFluxResponse(FILL_OPERATOR_OK, uuid).putOnly(operatorsList)
                            .post();
                } catch (Exception e) {
                    newFluxResponse(FILL_OPERATOR_FAIL, uuid).putOnly(e)
                            .post();
                    e.printStackTrace();
                }
            }
        });
    }

    public String getOperatorById(final long parent_id) {
        return doRequestIO(new RequestAction() {
            @Override
            public void request(String uuid) {
                List<alloperators> query = DbUtil.getAllOperatorsService()
                        .query("where operators_id=?", String.valueOf(parent_id));
                newFluxResponse(GET_OP_BY_ID, uuid)
                        .putOnly(query).post();
            }
        });
    }
}
