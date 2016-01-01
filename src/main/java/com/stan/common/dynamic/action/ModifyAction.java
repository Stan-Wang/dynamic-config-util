package com.stan.common.dynamic.action;

import com.stan.common.dynamic.config.Configuration;

import java.io.File;
import java.util.*;

/**
 * 配置文件变更处理事件
 * <p/>
 * Created by StanWang on 2015/12/31.
 */
public abstract class ModifyAction {


    protected void OnModify(File file) {

        Configuration last = Configuration.getLastConfiguration();

        Configuration configuration = new Configuration(file);

        compareHandle(last, configuration);

    }

    /**
     * 比较更新前后的配置
     * <br>
     * <p/>
     * <p>默认只比较现有Keys,对于新增KV暂不处理,根据需要加入TodoList</p>
     *
     * @param last          原有配置
     * @param configuration 变更后的配置
     */
    protected void compareHandle(Configuration last, Configuration configuration) {

        List<String> keys = getAllKeys(last);

        for (String key : keys) {

            String newValue = configuration.get(key);

            if (newValue == null) {
                OnModify(key, "");
                return;
            }

            if (!last.get(key).equals(newValue)) {
                OnModify(key, newValue);
            }

        }

    }

    /**
     * 获取配置所有的键
     *
     * @param last  配置文件
     * @return      Keys列表
     */
    protected List<String> getAllKeys(Configuration last) {

        ArrayList<String> re = new ArrayList<>();
        Enumeration enumeration = last.getProperties().propertyNames();

        while (enumeration.hasMoreElements())
            re.add(enumeration.nextElement().toString());

        return re;
    }

    /**
     * 变更处理事件
     * <br>
     * <p>删除默认value为空字符串</p>
     *
     * @param key       变更的键
     * @param value     变更的值
     */
    public abstract void OnModify(String key, String value);

}
