package ru.cbr.tomsk.dav.FilenetWebApplication.Service;


import com.filenet.api.core.Folder;
import com.filenet.api.core.ObjectStore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import ru.cbr.tomsk.dav.FilenetWebApplication.Filenet.CpeFolder;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
public class FolderService {

    private CpeFolder cpeFolder = null;

    @Setter
    @Getter
    private Subject subject;

    @PostConstruct
    public void init(){
        cpeFolder = new CpeFolder();
    }

    public List<String> getAllSubFoldersInRoot(ObjectStore objectStore, String rootPath){
        List<String> allSubFoldersNameFromPathRecurcive;
        cpeFolder.createUserContext(subject);
        allSubFoldersNameFromPathRecurcive = cpeFolder.getAllSubFoldersNameFromPathRecurcive(objectStore, rootPath);
        cpeFolder.rmUserContext();
        return allSubFoldersNameFromPathRecurcive;
    }

    public JSONObject getAllSubFoldersWithIdAndName(ObjectStore objectStore, String rootPath){
        cpeFolder.createUserContext(subject);
        List<Folder> folderList = cpeFolder.getAllFolderFromPathStack(objectStore,rootPath);
        JSONObject out = new JSONObject();
        for (Folder folder : folderList) {
            out.put(folder.get_Id().toString(),folder.get_PathName());
        }
        cpeFolder.rmUserContext();
        return out;
    }
}
