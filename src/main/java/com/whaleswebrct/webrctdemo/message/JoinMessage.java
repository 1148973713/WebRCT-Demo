package com.whaleswebrct.webrctdemo.message;

import java.util.List;

/**
 * @author wuk
 * @description:
 * @menu
 * @date 2023/2/11 14:17
 */
public class JoinMessage {
    private String type;

    private List<String> userIdList;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }

    public void addUserId(String userId){
        this.userIdList.add(userId);
    }
}
