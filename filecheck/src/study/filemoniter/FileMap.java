package study.filemoniter;  
  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.InputStream;  
import java.util.HashMap;  
import java.util.Map;  
import java.util.Properties;  
  
  
/** 
 * 文件的容器 
 * @author dWX207527 
 * 
 */  
public class FileMap   
{  
    private Map<String,String> map  = new HashMap<String, String>();  
      
    private static FileMap fileMap = new FileMap();  
      
    private FileMap()  
    {  
          
    }  
      
    public static FileMap getInstance()  
    {  
        return fileMap;  
    }  
      
    public void fillFileMap(String fileName)  
    {  
        File file = new File(fileName);  
        InputStream inStream = null;  
          
        try {  
             inStream = new FileInputStream(file);  
        if (null == file)  
        {  
            return ;  
        }  
          
        Properties properties = new Properties();  
        properties.load(inStream);  
          
        String value = properties.getProperty("name");  
        map.put("name", value);  
        }   
        catch (Exception e) {  
            e.printStackTrace();  
            return ;  
        }  
    }  
      
    public Map<String,String> getFileMap ()  
    {  
        return map;  
    }  
}  