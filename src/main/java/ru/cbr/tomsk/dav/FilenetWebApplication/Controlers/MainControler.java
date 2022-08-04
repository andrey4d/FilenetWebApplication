package ru.cbr.tomsk.dav.FilenetWebApplication.Controlers;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@Controller
@RequestMapping("/")
public class MainControler {


    @Value("${filenet.cpe.username}")
    private String username;

    @Value("${filenet.cpe.password}")
    private String password;

    @Value("${filenet.cpe.host}")
    private String host;

    @Value("${filenet.cpe.stanza}")
    private String stanza;

    @GetMapping
    public String main(Model model, @AuthenticationPrincipal User user){
        HashMap<Object, Object> data = new HashMap<>();
        data.put("profile", user);
        data.put("cpe_username",this.username);
        data.put("cpe_password",this.password);
        data.put("cpe_host",this.host);
        data.put("cpe_stanza",this.stanza);

        model.addAttribute("fromtendData", data);
        return "index";
    }

    @ResponseStatus(HttpStatus.OK)
    public String connection(){
        return "connect";
    }

}

