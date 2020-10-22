package cn.smbms.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ProperManager_ {

    private static ProperManager_ properManager;
    Properties params;

    /**
     * 私有的构造方法
     * 外部不能创建对象
     */
    private ProperManager_(){
        params=new Properties();
        String confingFile="database.properties";
        InputStream is= ProperManager_.class.getClassLoader().getResourceAsStream(confingFile);
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
    public  static ProperManager_ getInstance(){
        return ProperManager_Helper.properManager_();
   }

    /**
     * 静态内部类
     */
   public  static  class  ProperManager_Helper{
        /**
         * 提供一个方法创建对象
         * @return
         */
    public  static ProperManager_ properManager_(){
        return new ProperManager_();
    }

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
