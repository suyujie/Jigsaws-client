package com.sw.jigsaws.net.msg;

import com.alibaba.fastjson.JSONObject;
import com.sw.jigsaws.data.GameData;

public abstract class AbstractMsg {

    private static final long serialVersionUID = -8930418998280305692L;

    public JSONObject toJson(Integer commandId) {
        JSONObject json = new JSONObject();
        json.put("commandId", commandId);
        json.put("sessionId", GameData.sessionId);
        return json;
    }


}
