package com.stan.common.dynamic.action;

/**
 * Created by StanWang on 2016/1/4.
 */
public interface ModifyHandler {

    public String getKey();

    public void handle(String value);

}
