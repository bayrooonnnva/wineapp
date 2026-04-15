package es.upm.sos.wineapp.repository;

import es.upm.sos.wineapp.model.Usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    /**
     * Busca usuarios cuyo nombre contenga el texto indicado, ignorando mayúsculas y minúsculas.
     *
     * @param nombre texto a buscar dentro del nombre
     * @param Pageable información de paginación
     * @return página con los usuarios que cumplen el criterio
     */
    Page<Usuario> findByNombreContainingIgnoreCase(String nombre, Pageable Pageable);
}
