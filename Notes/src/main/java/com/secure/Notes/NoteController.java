package com.secure.Notes;

import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoteController {

    @GetMapping("/hello")
  public String getHello()
  {
      return "Hello";
  }

    @GetMapping("/contact")
    public String getContact()
    {
        return "Contact";
    }


}
