package ru.cbr.tomsk.dav.FilenetWebApplication.Service;

import com.filenet.api.collection.DocumentSet;
import com.filenet.api.core.Document;
import com.filenet.api.core.ObjectStore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import ru.cbr.tomsk.dav.FilenetWebApplication.Filenet.CPEDocument;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@NoArgsConstructor
public class DocumentService {
    private CPEDocument cpeDocument = null;

    @Setter
    @Getter
    private Subject subject;

    @PostConstruct
    public void init(){
        cpeDocument = new CPEDocument();
    }

    public Map<String,String> getDocumentsIdAndName(ObjectStore objectStore, String rootPath){
        Map<String,String> out = new HashMap<>();
            cpeDocument.createUserContext(subject);
            DocumentSet documentSet = cpeDocument.getDocumentSetInFolder(objectStore,rootPath);
            Iterator<Document> iterator = documentSet.iterator();
            while (iterator.hasNext()) {
                 Document document =  iterator.next();
                 out.put(document.get_Id().toString(),document.get_Name());
            }
            cpeDocument.rmUserContext();
        return out;
    }
}
