package ru.cbr.tomsk.dav.FilenetWebApplication.Service.Filenet;

import com.filenet.api.collection.ObjectStoreSet;
import com.filenet.api.core.Connection;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.util.UserContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Log4j
@Service
@NoArgsConstructor
public class CPEConnection {

//    private static final Logger logger = LogManager.getLogger(CpeInfo.class);
    @Setter
    private String userName = null;
    @Setter
    private String password = null;
    @Setter
    private String stanza   = null;
    @Setter
    private String uri      = null;

    @Getter
    private boolean isConnected = false;
    @Getter
    private Domain domain;
    @Getter
    private ObjectStoreSet objectStoreSet;
    @Getter
    private Connection connection;
    @Getter
    private Subject subject;

    private UserContext userContext;

    @PostConstruct
    public void init(){
        this.userContext = UserContext.get();
    }

    public void connect(String userName, String password, String uri, String stanza ){
        this.setUserName(userName);
        this.setPassword(password);
        this.setStanza(stanza);
        this.setUri(uri);
        connect();
    }

    public void connect(){
        this.connection = Factory.Connection.getConnection(uri);
        this.subject = UserContext.createSubject(connection, userName, password, stanza);
        this.userContext.pushSubject(subject);
        try {


            this.domain = Factory.Domain.fetchInstance(this.connection, null, null);
            this.objectStoreSet = domain.get_ObjectStores();
            this.isConnected = true;
        } catch (Exception e) {
            String lineSeparator = System.getProperty("line.separator");
            log.error(
                    "FileNet connection error." + lineSeparator +
                            "   UserID: " + userName + lineSeparator +
                            "   URI: " + uri + lineSeparator +
                            "   Stanza: " + stanza + lineSeparator +
                            "Exception message: " + lineSeparator + e.getMessage(),
                    e);
            this.isConnected = false;
        }
        finally {
            userContext.popSubject();
        }
    }


    public boolean disconnect(){
        this.userContext.popSubject();
        isConnected = false;
        objectStoreSet = null;
        domain = null;
        return false;
    }


    public ObjectStore getObjectStoreByName(String objectstoreName) {
        Iterator<ObjectStore> it = objectStoreSet.iterator();
        while (it.hasNext()) {
            ObjectStore os = it.next();
            if(os.get_Name().equals(objectstoreName)) return os;
        }
        return null;
    }


    public List<String> getObjectStoreName(){
        if(!isConnected) return null;
        List<String> out = new ArrayList<>();
        Iterator<ObjectStore>  osIterator = objectStoreSet.iterator();
        while (osIterator.hasNext()) {
            out.add(osIterator.next().get_Name());
        }
        return out;
    }


    public String getDomainName() {
        if(!isConnected) return null;
        return domain.get_Name() ;
    }
}