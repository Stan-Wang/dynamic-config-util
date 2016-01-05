package com.stan.common.dynamic;

import com.stan.common.dynamic.action.ModifyAction;
import com.stan.common.dynamic.action.ModifyHandler;
import com.stan.common.dynamic.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.util.HashMap;
import java.util.Map;

/**
 * 封装JDK自带的文件监视服务
 * <p/>
 * Created by StanWang on 2015/12/29.
 */
public class WatchService {

    public static java.nio.file.WatchService watchService;

    /**
     * 文件名 及 变更处理 映射
     */
    private Map<String, ModifyAction> actionMap = new HashMap<>();


    /**
     * 属性Key 及 变更处理 映射
     */
    private Map<String, ModifyHandler> handlerMap = new HashMap<>();


    static {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册文件监听处理
     *
     * @param path 需要监听的目录
     * @throws IOException
     * @throws InterruptedException
     */
    public void watchDir(Path path) throws IOException, InterruptedException {

        path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

        while (true) {

            final WatchKey key = watchService.take();

            for (WatchEvent<?> watchEvent : key.pollEvents()) {

                final Kind<?> kind = watchEvent.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;
                final String filename = watchEventPath.context().toString();

                if (actionMap.containsKey(filename)) {

                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        actionMap.get(filename).OnModify(new File(path + File.separator + filename));
                    }

                }

            }
            boolean valid = key.reset();

            if (!valid) {
                break;
            }
        }

    }

    /**
     * 通用变更处理事件注册
     *
     * @param file              配置文件
     * @param modifyAction      处理方法
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void watchFile(File file, ModifyAction modifyAction) throws IOException, InterruptedException {
        actionMap.put(file.getName(), modifyAction);
        watchDir(Paths.get(file.getParent()));
    }


    /**
     * 基于 配置键 的变更事件注册入口
     *
     * @param file          配置文件
     * @param handlers      处理方法
     *
     * @throws IOException
     * @throws InterruptedException
     */
    public void addHandler(File file,  ModifyHandler ... handlers) throws IOException, InterruptedException {

        if(handlers.length > 0){
            for(ModifyHandler handler : handlers){
                handlerMap.put(handler.getKey(),handler);
            }


            actionMap.put(file.getName(), new ModifyAction() {
                @Override
                public void OnModify(String key, String value) {
                    ModifyHandler handler = handlerMap.get(key);
                    if(handler != null){
                        handler.handle(value);
                    }
                }
            });

            watchDir(Paths.get(file.getParent()));
        }

    }



    /**
     * 测试方法
     *
     * @param args gs
     */
    public static void main(String[] args) {

        File file = new File("G:\\temp\\20151229\\testfile.properties");

        Configuration configuration = new Configuration(file);

        WatchService watch = new WatchService();
        try {
            watch.watchFile(file, new ModifyAction() {

                @Override
                public void OnModify(String key, String value) {
                    System.out.println("检查到变更的KV ==> " + key + "|" + value);
                }
            });
        } catch (IOException | InterruptedException ex) {
            System.err.println(ex);
        }

    }

}