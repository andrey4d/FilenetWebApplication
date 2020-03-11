package ru.cbr.tomsk.dav.FilenetWebApplication.Service;

import com.filenet.api.admin.StorageArea;
import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.collection.StorageAreaSet;
import com.filenet.api.core.Domain;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import com.filenet.api.property.Properties;
import com.filenet.api.property.Property;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import ru.cbr.tomsk.dav.FilenetWebApplication.Filenet.CPEObjectStore;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
@NoArgsConstructor
public class ObjectStoreService {
    private CPEObjectStore cpeObjectStore = null;

    @Setter
    @Getter
    private Subject subject;

    @PostConstruct
    public void init(){
        cpeObjectStore = new CPEObjectStore();
    }

    public JSONObject getObjectStoresIdAndName(Domain domain){
        JSONObject out = new JSONObject();
            cpeObjectStore.createUserContext(subject);
            ObjectStoreSet objectStoreSet = cpeObjectStore.getObjectStoreSet(domain);
            cpeObjectStore.rmUserContext();

            Iterator iterator = objectStoreSet.iterator();
            while (iterator.hasNext()) {
                 ObjectStore objectStore = (ObjectStore) iterator.next();
                 out.put(objectStore.get_Id().toString(),objectStore.get_Name());
            }
        return out;
    }

    public  JSONObject getObjectStoreIdByName(Domain domain, String osName) {
        JSONObject out = new JSONObject();
            cpeObjectStore.createUserContext(subject);
            ObjectStore objectStore= cpeObjectStore.getObjectStoreByName(domain,osName);
            cpeObjectStore.rmUserContext();
            out.put(objectStore.get_Id().toString(),objectStore.get_Name());
        return out;
    }

    public  ObjectStore getObjectStoreByName(Domain domain, String osName) {
        cpeObjectStore.createUserContext(subject);
        ObjectStore objectStore= cpeObjectStore.getObjectStoreByName(domain,osName);
        cpeObjectStore.rmUserContext();
        return objectStore;
    }

    public JSONObject getObjectStoreInfo(Domain domain, String osName){
        JSONObject out = new JSONObject();
        JSONObject jsonObject = new JSONObject();

        cpeObjectStore.createUserContext(subject);
        ObjectStore objectStore= cpeObjectStore.getObjectStoreByName(domain,osName);
        StorageAreaSet storageAreaSet = objectStore.get_StorageAreas();

        jsonObject.put("Id",objectStore.get_ObjectStoreId().toString());
        jsonObject.put("Name",objectStore.get_Name());
        jsonObject.put("Created",objectStore.get_DateCreated().toString());
        jsonObject.put("Locale",objectStore.get_LocaleName());
        jsonObject.put("DB Schema name",objectStore.get_DatabaseSchemaName());
        jsonObject.put("DB Connaction", objectStore.get_DatabaseConnection().get_DisplayName());
        out.put("ObjectStore",jsonObject);
        out.put("StorageAreasInfo", getStorageAreasInfo(storageAreaSet));
        out.put("StorageAreasPropertiesName",getStorageAreasProperties(storageAreaSet));
        cpeObjectStore.rmUserContext();
        return out;
    }

    public JSONArray getStorageAreasInfo(StorageAreaSet storageAreaSet) {
        JSONArray out = new JSONArray();
        Iterator<StorageArea> it = storageAreaSet.iterator();
        while (it.hasNext()) {
            StorageArea storageArea =  it.next();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id",storageArea.get_Id().toString());
            jsonObject.put("name",storageArea.get_DisplayName());
            jsonObject.put("status",storageArea.get_ResourceStatus().toString());
            jsonObject.put("use",storageArea.get_ContentElementKBytes().toString());
            jsonObject.put("elemets",storageArea.get_ContentElementCount().toString());

            String root = "";
            try {
                root = storageArea.getProperties().getStringValue("RootDirectoryPath");
            }catch (EngineRuntimeException e){
                if(e.getExceptionCode() == ExceptionCode.API_PROPERTY_NOT_IN_CACHE){
                    root = "NONE";
                }
            }
            jsonObject.put("root", root);
            out.add(jsonObject);
        }
        return out;
    }

    public JSONObject getStorageAreaProperties(StorageArea storageArea) {
        Integer Id = 0;
        JSONObject out = new JSONObject();
        Properties properties = storageArea.getProperties();
        Iterator<Property> propertyIterator = properties.iterator();
            while (propertyIterator.hasNext()) {
                    Property property = propertyIterator.next();
                    out.put((++Id).toString() ,property.getPropertyName());
                    }
        return out;
    }

    public JSONObject getStorageAreasProperties(StorageAreaSet storageAreaSet){
        JSONObject out = new JSONObject();
        Iterator<StorageArea> it = storageAreaSet.iterator();
        while (it.hasNext()) {
            StorageArea storageArea = it.next();
            out.put(storageArea.get_DisplayName(),getStorageAreaProperties(storageArea));
        }
        return  out;
    }

    public JSONObject getStorageAreasProperties(Domain domain, String osName) {
        return  getStorageAreasProperties(getObjectStoreByName(domain, osName).get_StorageAreas());
   }
}




//    Properties properties = storageArea.getProperties();
//    Iterator<Property> propertyIterator = properties.iterator();
//            while (propertyIterator.hasNext()) {
//                    Property property = propertyIterator.next();
//                    out.put("Proprty -->>" + (++Id).toString() ,property.getPropertyName());
//                    }
