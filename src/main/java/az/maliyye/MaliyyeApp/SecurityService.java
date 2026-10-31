package az.maliyye.MaliyyeApp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    @Autowired
    private IstifadeciRepository repo;

    private Istifadeci getIstifadeci(String username) {
        return repo.findByUsername(username);
    }

    public boolean hasDashboard(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeDashboard());
    }

    public boolean hasKassa(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeKassa());
    }

    public boolean hasBank(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeBank());
    }

    public boolean hasKontragent(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeKontragent());
    }

    public boolean hasAnbar(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeAnbar());
    }

    public boolean hasSatis(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeSatis());
    }

    public boolean hasSatinalma(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeSatinalma());
    }

    public boolean hasHesabat(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeHesabat());
    }

    public boolean hasHr(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && ("ROLE_ADMIN".equals(u.getRol()) || u.isIcazeHr());
    }
}