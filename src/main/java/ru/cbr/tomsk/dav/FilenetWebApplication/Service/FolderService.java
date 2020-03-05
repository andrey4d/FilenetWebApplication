package ru.cbr.tomsk.dav.FilenetWebApplication.Service;


import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.cbr.tomsk.dav.FilenetWebApplication.Service.Filenet.CPEConnection;
import ru.cbr.tomsk.dav.FilenetWebApplication.Service.Filenet.CPEFolder;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class FolderService {

    private CPEFolder cpeFolder = null;

    @Setter
    @Getter
    private Subject subject;

    @PostConstruct
    public void init(){
        cpeFolder = new CPEFolder();
    }

    public List<String> getAllSubFoldersInRoot(ObjectStore objectStore, String rootPath){
        List<String> allSubFoldersNameFromPathRecurcive;
        cpeFolder.createUserContext(subject);
        allSubFoldersNameFromPathRecurcive = cpeFolder.getAllSubFoldersNameFromPathRecurcive(objectStore, rootPath);
        cpeFolder.rmUserContext();
        return allSubFoldersNameFromPathRecurcive;
    }

    public Map<String,String> getAllSubFoldersWithIdAndName(ObjectStore objectStore, String rootPath){
        cpeFolder.createUserContext(subject);
        List<Folder> folderList = cpeFolder.getAllFolderFromPathStack(objectStore,rootPath);
        Map<String,String> out = new HashMap<>();
        for (Folder folder : folderList) {
            out.put(folder.get_Id().toString(),folder.get_PathName());
        }
        cpeFolder.rmUserContext();
        return out;
    }
}
