package com.wq.backoffice.model;

import java.util.*;

public class UserExt {

    private User user;

    private List<User> userList = new ArrayList<User>();

    private Map<String,Object> mapInfo = new HashMap<String,Object>();

    public Map<String, Object> getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(Map<String, Object> mapInfo) {
        this.mapInfo = mapInfo;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public String toString() {
        return "UserExt{" +
                "user=" + user +
                '}';
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
