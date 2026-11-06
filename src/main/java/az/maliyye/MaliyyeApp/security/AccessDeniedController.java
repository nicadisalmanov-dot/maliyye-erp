package az.maliyye.MaliyyeApp.security;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccessDeniedController {

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "security/access-denied"; // templates qovluğundakı access-denied.html faylına yönləndirir
    }
}