package ru.cbr.tomsk.dav.FilenetWebApplication.Controlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.cbr.tomsk.dav.FilenetWebApplication.Service.DocumentService;
import ru.cbr.tomsk.dav.FilenetWebApplication.Filenet.CPEConnection;
import ru.cbr.tomsk.dav.FilenetWebApplication.Service.FolderService;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilenetConnect {

    @Autowired
    CPEConnection cpeConnection;
    @Autowired
    DocumentService documentService;
    @Autowired
    FolderService folderService;

    @PreDestroy
    public void preDestroy(){
        cpeConnection.disconnect();
        System.out.println("--DESTROY--");
    }

    @GetMapping(value = "/connect")
    public boolean getConnection(@RequestParam(value = "username", defaultValue = "bootstrap") String username,
                                 @RequestParam(value = "password", defaultValue = "password") String password,
                                 @RequestParam(value = "host", defaultValue = "http://filenet06.dev.bench2.ppod.cbr.ru:9080") String host,
                                 @RequestParam(value= "stanza", defaultValue = "FileNetP8") String stanza){
        String uri = host+"/wsi/FNCEWS40MTOM";
        cpeConnection.connect(username,password, uri, stanza);
        return cpeConnection.isConnected();
    }

    @GetMapping(value = "/disconnect")
    public boolean getConnection(){
        cpeConnection.disconnect();
        return cpeConnection.isConnected();
    }

    @GetMapping(value = "/domain/name")
    public String getObjectStore(){
        String out = cpeConnection.getDomainName();
        if ( out == null ) return "Not connected to CPE!";
        return out;
    }


    @GetMapping(value = "/domain/ObjectStores")
    public List<String> getObjectStrores(){
        List<String> list = cpeConnection.getObjectStoreName();
        if( list == null ) list.add("Not connected to CPE!");
        return list;
    }

    @GetMapping(value = "/domain/folders", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getFoldes(  @RequestParam(value = "root", defaultValue = "/") String rootPath,
                                    @RequestParam(value = "objectstore", defaultValue = "DOSTEST") String osName){
        List<String> list = new ArrayList<>();
        if(cpeConnection.isConnected()){
            folderService.setSubject(cpeConnection.getSubject());
            list = folderService.getAllSubFoldersInRoot(cpeConnection.getObjectStoreByName(osName),rootPath);
        }
        else{
            list.add("Not connected to CPE!");
        }
        return list;
    }

    @GetMapping(value = "/domain/folders/map", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,String> getFoldesIdAndPath(   @RequestParam(value = "root", defaultValue = "/") String rootPath,
                                                    @RequestParam(value = "objectstore", defaultValue = "DOSTEST") String osName){
        Map<String,String> out = new HashMap<>();
        if(cpeConnection.isConnected()){
            folderService.setSubject(cpeConnection.getSubject());
            out = folderService.getAllSubFoldersWithIdAndName(cpeConnection.getObjectStoreByName(osName),rootPath);
        }
        else{
            out.put("ERROR","Not connected to CPE!");
        }
        return out;
    }


    @GetMapping(value = "/domain/folders/documents", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,String> getDocumentsInFolderService(@RequestParam(value = "root", defaultValue = "/") String rootPath,
                                                          @RequestParam(value = "objectstore", defaultValue = "DOSTEST") String osName){
        Map<String,String> out = new HashMap<>();
        if(cpeConnection.isConnected()){
            documentService.setSubject(cpeConnection.getSubject());
            out = documentService.getDocumentsIdAndName(cpeConnection.getObjectStoreByName(osName),rootPath);
        }
        else{
            out.put("ERROR","Not connected to CPE!");
        }
        return out;
    }
}
