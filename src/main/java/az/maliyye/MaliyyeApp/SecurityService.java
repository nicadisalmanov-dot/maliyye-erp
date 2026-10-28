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
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeDashboard());
    }

    public boolean hasKassa(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeKassa());
    }

    public boolean hasBank(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeBank());
    }

    public boolean hasKontragent(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeKontragent());
    }

    public boolean hasAnbar(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeAnbar());
    }

    public boolean hasSatis(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeSatis());
    }

    public boolean hasSatinalma(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeSatinalma());
    }

    public boolean hasHesabat(String username) {
        Istifadeci u = getIstifadeci(username);
        return u != null && (u.getRol().equals("ROLE_ADMIN") || u.isIcazeHesabat());
    }
}