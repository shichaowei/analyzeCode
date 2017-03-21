package study.filemoniter;  
  
import java.io.File;  
import java.util.Map;  
import java.util.Observable;  
import java.util.Set;  
import java.util.concurrent.ConcurrentHashMap;  
  
import org.apache.commons.lang3.StringUtils;  
  
  
/** 
 *文件监听器 观察者模式，该监听器是一个被观察者 
 *即主题
 * @author dWX207527 
 * 
 */  
public class FileMoniter  extends Observable implements Runnable  
{  
    /** 
     * 是否启动监听 
     */  
    private boolean monitorFlag = true;  
      
    /** 
     * 5秒监听一次 
     */  
    private int interval = 5000;  
      
    /** 
     * 文件容器 key放文件的名称，value放文件的最后修改时间 
     */  
    private Map<String,Long> fileMap = new ConcurrentHashMap<String, Long>();  
      
    /** 
     * 线程方法 
     */  
    @Override  
    public void run()   
    {  
        while(monitorFlag)  
        {  
            try   
            {  
                Thread.sleep(interval);  
                  
                checkFile();  
            } catch (Exception e)  
            {  
                e.printStackTrace();  
            }  
        }  
    }  
      
    /** 
     * 检查文件是否更新 
     */  
    private void checkFile()  
    {  
        Set<String> fileNames = fileMap.keySet();  
          
        for (String fileName : fileNames)  
        {  
            // 如果被修改过则通知被观察者  
            if (isModifide(fileName,fileMap.get(fileName)))  
            {  
                setChanged();  
                notifyObservers(fileName);  
            }  
                  
        }  
    }  
      
    /** 
     * 判断是被修改过 
     * @param fileName 
     * @param lastModifyTime 
     * @return 
     */  
    private boolean isModifide(String fileName,Long lastModifyTime)  
    {  
        File file = new File(fileName);  
        if (file ==null)  
        {  
            return false;  
        }  
          
        long time = lastModifyTime;  
          
        long lastTime = file.lastModified();  
          
        if (lastTime != time)  
        {  
              
            // 然后将文件map放置最新的时间  
            fileMap.put(fileName, lastTime);  
            return true;  
        }  
          
        return false;  
    }  
      
    /** 
     * 新增文件 
     * @param fileName 
     */  
    public void addFile(String fileName)  
    {  
        // 如果文件为空则直接返回  
        if (StringUtils.isEmpty(fileName))  
        {  
            return;  
        }  
          
        Long lastModifyTime = getLastModifyTime(fileName);  
          
        fileMap.put(fileName, lastModifyTime);  
    }  
      
    /** 
     * 得到文件的最后修改时间 
     * @param fileName 
     * @return 
     */  
    private Long getLastModifyTime(String fileName)  
    {  
        File file = new File(fileName);  
        if (null == file)  
        {  
            return 0L;  
        }  
        return file.lastModified();  
    }  
  
}  