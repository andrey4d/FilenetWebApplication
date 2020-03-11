package ru.cbr.tomsk.dav.FilenetWebApplication.Filenet;

import javax.security.auth.Subject;

public interface ICpeUserContext {
public void createUserContext(Subject subject);
public void rmUserContext();
}

