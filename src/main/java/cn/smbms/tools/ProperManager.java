package cn.smbms.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProperManager {

    private static ProperManager properManager;
    Properties params;

    /**
     * 私有的构造方法
     * 外部不能创建对象
     */
    private  ProperManager(){
        params=new Properties();
        String confingFile="database.properties";
        InputStream is=ProperManager.class.getClassLoader().getResourceAsStream(confingFile);
        try {
            params.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向外界提供
     * 公共方法
     */
    public synchronized static  ProperManager  getInstance(){
        if (properManager==null){
            //自己创建对象
            properManager=new ProperManager();
        }
        return properManager;
   }

    /**
     * 实例方法
     * @param key
     * @return
     */
   public  String getValueByKey(String key){
        return params.getProperty(key);
   }

}
