package study.filemoniter;  
  
import java.util.Observable;  
import java.util.Observer;  
  
/** 
 * 文件观察者 
 * @author dWX207527 
 * 
 */  
public class FileObserver implements Observer  
{  
  
    @Override  
    public void update(Observable o, Object arg)   
    {  
        if (!(o instanceof FileMoniter) || !(arg instanceof String))  
        {  
            return ;  
        }  
        String fileName  = (String)arg; 
        
        System.out.println(fileName+"文件有改动");  
        // 一旦有改动则通知了观察者 观察者进行重新配置文件  
         
        FileMap.getInstance().fillFileMap(fileName);  
    }  
  
}  