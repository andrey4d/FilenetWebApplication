package ru.cbr.tomsk.dav.FilenetWebApplication.Filenet;

import com.filenet.api.util.UserContext;

import javax.security.auth.Subject;

public class CpeIserContext implements ICpeUserContext{
    private UserContext userContext = null;

    @Override
    public void createUserContext(Subject subject) {
       userContext = UserContext.get();
       userContext.pushSubject(subject);
    }

    @Override
    public void rmUserContext() {
        userContext.popSubject();
        userContext=null;
    }
}
