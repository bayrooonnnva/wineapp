package es.upm.sos.wineapp.repository;

import es.upm.sos.wineapp.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
}
