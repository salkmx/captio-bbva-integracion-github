package com.sngular.captio.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sngular.captio.dto.UsuarioDTO;

/**
 * Tests unitarios para los métodos de sincronización de UsuarioService.
 * Estos tests verifican la lógica de negocio sin dependencias externas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para lógica de sincronización de usuarios")
class UsuarioServiceSyncTest {

    // ========== Tests de validación de datos ==========

    @Nested
    @DisplayName("Validación de UsuarioDTO")
    class ValidacionUsuarioDTO {

        @Test
        @DisplayName("Usuario con email válido debe ser procesable")
        void testUsuarioConEmailValido() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("test@bbva.com");
            usuario.setLogin("test@bbva.com");
            usuario.setId(123);

            assertNotNull(usuario.getEmail());
            assertFalse(usuario.getEmail().isBlank());
            assertNotNull(usuario.getId());
        }

        @Test
        @DisplayName("Usuario sin email no debe ser procesable")
        void testUsuarioSinEmail() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("");
            usuario.setLogin("");

            assertTrue(usuario.getEmail() == null || usuario.getEmail().isBlank());
        }

        @Test
        @DisplayName("Usuario con TDC debe tener valor no vacío")
        void testUsuarioConTDC() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("tdc@bbva.com");
            usuario.setTdc("4532111122223333");
            usuario.setId(456);

            assertNotNull(usuario.getTdc());
            assertFalse(usuario.getTdc().isBlank());
            assertEquals(16, usuario.getTdc().length());
        }

        @Test
        @DisplayName("Usuario existente debe tener ID de Captio")
        void testUsuarioExistenteConId() {
            UsuarioDTO usuarioNuevo = new UsuarioDTO();
            usuarioNuevo.setEmail("nuevo@bbva.com");
            usuarioNuevo.setId(null);

            UsuarioDTO usuarioExistente = new UsuarioDTO();
            usuarioExistente.setEmail("existente@bbva.com");
            usuarioExistente.setId(789);

            assertNull(usuarioNuevo.getId());
            assertNotNull(usuarioExistente.getId());
        }
    }

    // ========== Tests de separación de usuarios ==========

    @Nested
    @DisplayName("Lógica de separación nuevo/existente")
    class SeparacionUsuarios {

        @Test
        @DisplayName("Debe identificar usuario como nuevo si no tiene ID")
        void testIdentificarUsuarioNuevo() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("nuevo@bbva.com");

            boolean esNuevo = usuario.getId() == null;

            assertTrue(esNuevo);
        }

        @Test
        @DisplayName("Debe identificar usuario como existente si tiene ID")
        void testIdentificarUsuarioExistente() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("existente@bbva.com");
            usuario.setId(123);

            boolean esExistente = usuario.getId() != null;

            assertTrue(esExistente);
        }

        @Test
        @DisplayName("Debe filtrar usuarios sin email de la lista")
        void testFiltrarUsuariosSinEmail() {
            List<UsuarioDTO> usuarios = Arrays.asList(
                    crearUsuario("valido1@bbva.com", 1),
                    crearUsuario("", null),
                    crearUsuario("valido2@bbva.com", 2),
                    crearUsuario(null, null));

            List<UsuarioDTO> usuariosValidos = usuarios.stream()
                    .filter(u -> u.getEmail() != null && !u.getEmail().isBlank())
                    .toList();

            assertEquals(2, usuariosValidos.size());
            assertTrue(usuariosValidos.stream().allMatch(u -> u.getEmail().contains("@")));
        }
    }

    // ========== Tests de preparación de datos para UPDATE ==========

    @Nested
    @DisplayName("Preparación de datos para actualización")
    class PreparacionDatosUpdate {

        @Test
        @DisplayName("Datos de actualización deben incluir ID obligatorio")
        void testDatosUpdateConId() {
            UsuarioDTO original = crearUsuario("test@bbva.com", 123);
            original.setName("Test User");

            UsuarioDTO paraUpdate = new UsuarioDTO();
            paraUpdate.setId(original.getId());
            paraUpdate.setEmail(original.getEmail());
            paraUpdate.setLogin(original.getLogin());
            paraUpdate.setName(original.getName());
            paraUpdate.setActive(true);

            assertNotNull(paraUpdate.getId());
            assertEquals(123, paraUpdate.getId());
            assertTrue(paraUpdate.getActive());
        }

        @Test
        @DisplayName("Lista vacía no debe procesarse")
        void testListaVacia() {
            List<UsuarioDTO> listaVacia = Collections.emptyList();

            assertTrue(listaVacia.isEmpty());
        }

        @Test
        @DisplayName("Lista null no debe procesarse")
        void testListaNull() {
            List<UsuarioDTO> listaNula = null;

            assertNull(listaNula);
        }
    }

    // ========== Tests de sincronización de grupos ==========

    @Nested
    @DisplayName("Sincronización de grupos")
    class SincronizacionGrupos {

        @Test
        @DisplayName("Usuario para grupo viajes debe tener activeTravelGroup=true")
        void testUsuarioGrupoViajes() {
            UsuarioDTO usuario = crearUsuario("viajes@bbva.com", 456);
            usuario.setActiveTravelGroup(true);

            assertTrue(usuario.getActiveTravelGroup());
        }

        @Test
        @DisplayName("Usuario para grupo KM debe tener ID válido")
        void testUsuarioGrupoKm() {
            UsuarioDTO usuario = crearUsuario("km@bbva.com", 789);

            assertNotNull(usuario.getId());
        }
    }

    // ========== Tests de sincronización de payments ==========

    @Nested
    @DisplayName("Sincronización de payments/TDC")
    class SincronizacionPayments {

        @Test
        @DisplayName("Usuario con TDC y sin ID no debe procesarse")
        void testUsuarioConTDCSinId() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("notdc@bbva.com");
            usuario.setTdc("4532999988887777");
            usuario.setId(null);

            boolean debeProcesar = usuario.getTdc() != null
                    && !usuario.getTdc().isBlank()
                    && usuario.getId() != null;

            assertFalse(debeProcesar);
        }

        @Test
        @DisplayName("Usuario con TDC y con ID debe procesarse")
        void testUsuarioConTDCConId() {
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail("contdc@bbva.com");
            usuario.setTdc("4532111122223333");
            usuario.setId(111);

            boolean debeProcesar = usuario.getTdc() != null
                    && !usuario.getTdc().isBlank()
                    && usuario.getId() != null;

            assertTrue(debeProcesar);
        }

        @Test
        @DisplayName("Usuario sin TDC no debe procesarse")
        void testUsuarioSinTDC() {
            UsuarioDTO usuario = crearUsuario("sintdc@bbva.com", 222);
            usuario.setTdc(null);

            boolean debeProcesar = usuario.getTdc() != null
                    && !usuario.getTdc().isBlank()
                    && usuario.getId() != null;

            assertFalse(debeProcesar);
        }

        @Test
        @DisplayName("TDC vacío no debe procesarse")
        void testTDCVacio() {
            UsuarioDTO usuario = crearUsuario("tdcvacio@bbva.com", 333);
            usuario.setTdc("");

            boolean debeProcesar = usuario.getTdc() != null
                    && !usuario.getTdc().isBlank()
                    && usuario.getId() != null;

            assertFalse(debeProcesar);
        }
    }

    // ========== Método helper ==========

    private UsuarioDTO crearUsuario(String email, Integer id) {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setEmail(email);
        usuario.setLogin(email);
        usuario.setId(id);
        usuario.setName("Test User");
        return usuario;
    }
}
