package ru.cbr.tomsk.dav.FilenetWebApplication.Filenet;

import com.filenet.api.collection.FolderSet;
import com.filenet.api.constants.RefreshMode;
import com.filenet.api.core.Factory;
import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.util.UserContext;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

@Log4j
@NoArgsConstructor
public class CPEFolder {

    private UserContext userContext = null;

    public void createUserContext(Subject subject){
        userContext = UserContext.get();
        userContext.pushSubject(subject);
    }

    public void rmUserContext(){
        userContext.popSubject();
        userContext=null;
    }

    private String rootPath(String rootPath){
        if (!rootPath.endsWith("/")) rootPath += "/";
        return rootPath;
    }

    private List<String> getAllSubFoldersNameFromPathRecurcive(ObjectStore os, String folderPath, List<String> buffer){
        folderPath = rootPath(folderPath);
        buffer.add(folderPath);
        for(String name : getSubFoldesName(os,folderPath)){
            getAllSubFoldersNameFromPathRecurcive(os, folderPath+name+"/", buffer);
        }
        return buffer;
    }

    public List<String> getAllSubFoldersNameFromPathRecurcive(ObjectStore os, String folderPath){
        folderPath = rootPath(folderPath);
        return getAllSubFoldersNameFromPathRecurcive(os, folderPath, new ArrayList<String>());
    }

    public List<String > getAllSubFoldersNameFromPathStack(ObjectStore os, String rootFolder) {
        rootFolder = rootPath(rootFolder);
        Stack<String> stack = new Stack();
        List<String> out = new ArrayList<>();
        out.add(rootFolder);
        stack.push(rootFolder);
        while (!stack.isEmpty()){
            String root = stack.pop();
            for (String folder : getSubFoldesName(os,root)){
                String dir = root + folder + "/";
                stack.push(dir);
                out.add(dir);
            }
        }
        return out;
    }

    public List<Folder> getAllFolderFromPathStack(ObjectStore os, String rootFolder){
        rootFolder = rootPath(rootFolder);
        Stack<String> stack = new Stack();
        List<Folder> out = new ArrayList<>();
        out.add(getFolder(os,rootFolder));
        stack.push(rootFolder);
        while (!stack.isEmpty()){
            String root = stack.pop();
            FolderSet folderSet = getSubFolder(os,root);
            Iterator<Folder> it = folderSet.iterator();
            while (it.hasNext()){
                Folder folder = it.next();
                String dir = root + folder.get_PathName() + "/";
                stack.push(dir);
                out.add(folder);
            }
        }
        return out;
    }


    public Folder getFolder(ObjectStore objectStore, String folderPath){
        return Factory.Folder.fetchInstance(objectStore, folderPath, null);
    }

    // Return FoldeSet with subfolder
    public FolderSet getSubFolder(ObjectStore objectStore, String folderPath){
        folderPath = rootPath(folderPath);
        Folder currentFolder = getFolder(objectStore, folderPath);
        return currentFolder.get_SubFolders();
    }

    //    Return subfolder name in current folderPath
    public List<String> getSubFoldesName(ObjectStore objectStore, String folderPath){
        folderPath = rootPath(folderPath);
        FolderSet subFolders = getSubFolder(objectStore,folderPath);
        List<String> foldersName = new ArrayList<>();
        Iterator<FolderSet> it = subFolders.iterator();
        while (it.hasNext()){
            Folder folder = (Folder) it.next();
            foldersName.add(folder.get_FolderName());
        }
        return foldersName;
    }


//  create new Folder or return Folder if folder already exists
    public Folder cretateFolder(ObjectStore objectStore, String rootPath, String folderName){
        Folder folder = null;
        rootPath = rootPath(rootPath);
        try{
            folder = Factory.Folder.createInstance(objectStore, null);
            Folder root = getFolder(objectStore,rootPath);
            folder.set_Parent(root);
            folder.set_FolderName(folderName);
            folder.save(RefreshMode.NO_REFRESH);
        }catch (EngineRuntimeException ere){
                        // Create failed.  See if it's because the folder exists.
            ExceptionCode code = ere.getExceptionCode();
            if (code != ExceptionCode.E_NOT_UNIQUE)
            {
                throw ere;
            }
            log.warn("Folder already exists: " + rootPath  + folderName);
            folder = getFolder(objectStore, rootPath + folderName);
//            folder = Factory.Folder.getInstance(objectStore, null, rootPath + folderName);
        }
        return folder;
    }

    public void removeFolder(ObjectStore objectStore,String folderName) {
        Folder folder = null;
        try {
            folder = getFolder(objectStore, folderName);
            folder.delete();
            folder.save(RefreshMode.REFRESH);
        } catch (EngineRuntimeException ere) {
            ExceptionCode code = ere.getExceptionCode();
            if (code != ExceptionCode.CONTENT_FCP_FSB_DIRECTORY_NOT_FOUND) {
                log.error("Folder not exists: " + folderName);
            }else{
                throw ere;
            }
        }
    }
}
