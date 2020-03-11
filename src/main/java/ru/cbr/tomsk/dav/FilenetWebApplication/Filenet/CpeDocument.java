package ru.cbr.tomsk.dav.FilenetWebApplication.Filenet;

import com.filenet.api.collection.ContentElementList;
import com.filenet.api.collection.DocumentSet;
import com.filenet.api.constants.*;
import com.filenet.api.core.*;
import com.filenet.api.exception.EngineRuntimeException;
import com.filenet.api.exception.ExceptionCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;
import ru.cbr.tomsk.dav.FilenetWebApplication.Constants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

@Log4j
@NoArgsConstructor
public class CpeDocument extends CpeIserContext{

    public Date getDate(int YYYY, int MM, int DD){
        Calendar calendar = Calendar.getInstance();
        calendar.set(YYYY,MM,DD);
        return calendar.getTime();
    }

      //    Return DoccumentSet in folder
    public DocumentSet getDocumentSetInFolder(ObjectStore objectStore, String folderPath){
        Folder currentFolder = Factory.Folder.fetchInstance(objectStore, folderPath, null);
        return currentFolder.get_ContainedDocuments();
    }

    public Document getDocumentById(ObjectStore objectStore, String docId){
        Document doc = null;
        try {
            doc = Factory.Document.fetchInstance(objectStore, docId, null);
        }catch (EngineRuntimeException ere){
            ExceptionCode code = ere.getExceptionCode();
            if (code == ExceptionCode.E_BAD_CLASSID) {
                System.out.println("Not valid ID :: " + docId);
            }else{
                throw ere;
            }
        }
        return doc;
    }

    public Document getDocumentByPath(ObjectStore objectStore, String docPath){
        Document doc = null;
        try {
            doc = Factory.Document.fetchInstance(objectStore, docPath, null);
        }catch (EngineRuntimeException ere){
            ExceptionCode code = ere.getExceptionCode();
            if (code == ExceptionCode.E_BAD_CLASSID) {
                System.out.println("Not valid PATH :: " + docPath);
            }else{
                throw ere;
            }
        }
        return doc;
    }

    private FileInputStream openLocalFileForRead(String fineName) {
        try {
            return new FileInputStream(fineName);
        } catch (FileNotFoundException fnfe) {
            throw new RuntimeException(fnfe);
        }
    }

    public String createAndFileDocument(Domain dom, ObjectStore os, Folder folder, String localFileName, String documentTitle){
        return createAndFileDocument(dom, os, folder, localFileName, documentTitle, null);
    }

    public String createAndFileDocument(Domain dom, ObjectStore os, Folder folder, String localFileName, String documentTitle, String containmentName)
    {
        if (containmentName == null){
            containmentName=documentTitle;
        }

        FileInputStream fis = openLocalFileForRead(localFileName);
        ContentTransfer ct = Factory.ContentTransfer.createInstance();
        ct.setCaptureSource(fis);
        // optional
        ct.set_RetrievalName(localFileName);
        // optional
        ct.set_ContentType(Constants.CONTENT_TYPE_OCTET_STREAM);

        ContentElementList cel = Factory.ContentElement.createList();
        cel.add(ct);

        Document doc = Factory.Document.createInstance(os, null);
        //not required
        doc.getProperties().putValue("DocumentTitle", documentTitle);
        doc.set_ContentElements(cel);
        doc.checkin(AutoClassify.DO_NOT_AUTO_CLASSIFY, CheckinType.MAJOR_VERSION);

        DynamicReferentialContainmentRelationship rcr =
                Factory.DynamicReferentialContainmentRelationship.createInstance(os,
                        null,
                        AutoUniqueName.AUTO_UNIQUE,
                        DefineSecurityParentage.DO_NOT_DEFINE_SECURITY_PARENTAGE);
        rcr.set_Tail(folder);
        rcr.set_Head(doc);
        rcr.set_ContainmentName(containmentName);

        UpdatingBatch ub = UpdatingBatch.createUpdatingBatchInstance(dom, RefreshMode.REFRESH);
        ub.add(doc, null);
        ub.add(rcr, null);
//        System.out.println("Doing updates via UpdatingBatch");
        ub.updateBatch();
        return rcr.get_ContainmentName();
    }

    public void removeDocument(Domain dom, ObjectStore os, Folder folder, String localFileName, String documentTitle, String containmentName){

    }

    public void removeDocumentById(ObjectStore os, String documentId){
        Document document = getDocumentById(os, documentId);
        ContentElementList contentElements = document.get_ContentElements();
        document.delete();
        document.save(RefreshMode.REFRESH);
    }
}
