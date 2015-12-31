package com.stan.common.dynamic;

import com.stan.common.dynamic.action.ModifyAction;
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

    private Map<String, ModifyAction> actionMap = new HashMap<>();


    static {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


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

    public void watchFile(File file, ModifyAction modifyAction) throws IOException, InterruptedException {
        actionMap.put(file.getName(), modifyAction);
        watchDir(Paths.get(file.getParent()));
    }

    /**
     * @param args
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