package es.upm.sos.wineapp.controller;

import es.upm.sos.wineapp.assembler.UsuarioModelAssembler;
import es.upm.sos.wineapp.model.Usuario;
import es.upm.sos.wineapp.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// IMPORTS ESTÁTICOS: Fundamentales para HATEOAS
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioModelAssembler assembler;

    /**
     * POST /usuarios — Registra un nuevo usuario.
     * El @Valid activa las restricciones de la entidad (NotBlank, Email, etc.)
     * Devuelve 201 Created y el recurso con sus links.
     */
    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> crear(@Valid @RequestBody Usuario u) {
        Usuario nuevo = service.registrar(u);
        
        return ResponseEntity
                .created(linkTo(methodOn(UsuarioController.class).obtenerPorId(nuevo.getId())).toUri())
                .body(assembler.toModel(nuevo));
    }

    /**
     * GET /usuarios — Obtiene lista paginada y filtrada.
     * HATEOAS: Incluye enlaces de navegación (first, self, next, last).
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<Usuario>>> listarUsuarios(
            @RequestParam(required = false) String nombre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<Usuario> pagedAssembler) {
        
        Page<Usuario> usuariosPage = service.listar(nombre, PageRequest.of(page, size));
        
        // El pagedAssembler usa nuestro 'assembler' para añadir links a cada usuario individualmente
        return ResponseEntity.ok(pagedAssembler.toModel(usuariosPage, assembler));
    }

    /**
     * GET /usuarios/{id} — Devuelve los datos de un perfil concreto.
     */
    @GetMapping("/{id}")
    public EntityModel<Usuario> obtenerPorId(@PathVariable Long id) {
        Usuario usuario = service.buscarPorId(id);
        return assembler.toModel(usuario);
    }

    /**
     * PUT /usuarios/{id} — Actualiza los datos del perfil.
     * No hay try-catch: si el ID no existe, GlobalExceptionHandler devuelve el 404.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario u) {
        Usuario actualizado = service.actualizar(id, u);
        return ResponseEntity.ok(assembler.toModel(actualizado));
    }

    /**
     * DELETE /usuarios/{id} — Elimina el perfil.
     * Devuelve 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}