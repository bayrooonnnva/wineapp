package es.upm.sos.wineapp.controller;

// 1. IMPORTS ESTÁTICOS (Según tu lista oficial)
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// NOTA: En la 4.0.5 el paquete de WebMvcTest ha cambiado de sitio como indicas
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest; 
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import es.upm.sos.wineapp.assembler.UsuarioModelAssembler;
import es.upm.sos.wineapp.model.Usuario;
import es.upm.sos.wineapp.service.UsuarioService;

@WebMvcTest(UsuarioController.class) // Usando el nuevo paquete de la v4
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // En la v4, @MockBean se ha sustituido por @MockitoBean
    // y se ha movido al paquete 'bean.override'
    @MockitoBean
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioModelAssembler assembler;

    @Test
    public void testCrearUsuario_Exito() throws Exception {
        // GIVEN
        Usuario u = new Usuario();
        u.setId(1L);
        u.setNombre("Alex");
        u.setEmail("alex@upm.es");
        u.setFechaNacimiento(LocalDate.of(2000, 1, 1));

        when(usuarioService.registrar(any(Usuario.class))).thenReturn(u);
        
        // Creamos un modelo con el link self para que el test no falle
        EntityModel<Usuario> model = EntityModel.of(u, Link.of("/api/v1/usuarios/1").withSelfRel());
        when(assembler.toModel(any(Usuario.class))).thenReturn(model);

        // WHEN & THEN
        mockMvc.perform(post("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Alex\",\"email\":\"alex@upm.es\",\"fechaNacimiento\":\"2000-01-01\"}"))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(header().exists("Location")) // Verifica que el Location header está ahí
                .andExpect(jsonPath("$.nombre").value("Alex"));
    }

    @Test
    public void testActualizarUsuario_Error() throws Exception {
        // GIVEN: El ID 99 no existe
        when(usuarioService.actualizar(eq(99L), any(Usuario.class)))
                .thenThrow(new NoSuchElementException("No encontrado"));

        // WHEN & THEN
        mockMvc.perform(put("/api/v1/usuarios/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nombre\":\"Update\",\"email\":\"update@upm.es\",\"fechaNacimiento\":\"1990-01-01\"}"))
                .andExpect(status().isNotFound()); // 404 Not Found
    }
}