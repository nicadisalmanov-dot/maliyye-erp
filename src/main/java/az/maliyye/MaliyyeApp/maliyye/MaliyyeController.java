package az.maliyye.MaliyyeApp;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MaliyyeController {

    @GetMapping("/maliyye")
    public String maliyyeMerkezi() {
        return "maliyye/maliyye";
    }
}