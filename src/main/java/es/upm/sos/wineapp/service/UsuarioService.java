package es.upm.sos.wineapp.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;


import es.upm.sos.wineapp.model.Usuario;
import es.upm.sos.wineapp.repository.UsuarioRepository;

@Service
public class UsuarioService {
    
    @Autowired UsuarioRepository repo;

    public Usuario registrar(Usuario u){
        if(Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears() < 18){
            throw new IllegalArgumentException("Debe ser mayor de edad.");
        }
        return repo.save(u);
    }

    public Page<Usuario> listar(String nombre, Pageable pageable){
        if(nombre != null && !nombre.isEmpty()){
            return repo.findByNombreContainingIgnoreCase(nombre, pageable);
        }
        return repo.findAll(pageable);
    }

    public Usuario buscarPorId(Long id){
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado."));
    }

    public Usuario actualizar(Long id, Usuario nuevosDatos){
        Usuario existing = buscarPorId(id);
        existing.setNombre(nuevosDatos.getNombre());
        existing.setEmail(nuevosDatos.getEmail());
        // La fecha de nacimiento no suele cambiarse
        return repo.save(existing);
    }

    public void eliminar(Long id){
        if(!repo.existsById(id)) throw new NoSuchElementException("ID inexistente.");
        repo.deleteById(id);
    }
    
}
