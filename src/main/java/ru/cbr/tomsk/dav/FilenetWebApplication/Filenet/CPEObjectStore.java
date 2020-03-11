package ru.cbr.tomsk.dav.FilenetWebApplication.Filenet;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j
@NoArgsConstructor
public class CPEObjectStore extends CpeIserContext{

    public ObjectStoreSet getObjectStoreSet(Domain domain) {
        return domain.get_ObjectStores();
    }

    public ObjectStore getObjectStoreByName(Domain domain, String objectstoreName) {
        Iterator<ObjectStore> it = getObjectStoreSet(domain).iterator();
        while (it.hasNext()) {
            ObjectStore os = it.next();
            if(os.get_Name().equals(objectstoreName)) return os;
        }
        return null;
    }

    public List<String> getObjectStoreName(Domain domain){
        List<String> out = new ArrayList<>();
        Iterator<ObjectStore>  osIterator = getObjectStoreSet(domain).iterator();
        while (osIterator.hasNext()) {
            out.add(osIterator.next().get_Name());
        }
        return out;
    }
}
