#配置文件热加载工具
-----------------------------

CORE ： 通过jdk WatchService 实现变更监控，并提供接口实现变更方法

Demo 详见WatchService里的main方法


功能点：

    1.配置变更热加载
    2.动态运行指定环境的配置文件 （TODO）
    
NOTE:

    测试Windows环境下Sublime VIM变更事件触发正常，NotePad++，VSCode会有变更键值不对应的问题

