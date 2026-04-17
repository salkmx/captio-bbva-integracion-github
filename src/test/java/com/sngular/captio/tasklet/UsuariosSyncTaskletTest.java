package com.sngular.captio.tasklet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;

import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.model.UsuarioSonar;
import com.sngular.captio.repository.UsuarioAmexNewRepository;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.UsuarioService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para UsuariosSyncTasklet")
class UsuariosSyncTaskletTest {

    @Mock
    private UsuarioSonarRepository usuarioSonarRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioAmexNewRepository usuarioAmexNewRepository;

    @Mock
    private StepContribution stepContribution;

    @Mock
    private ChunkContext chunkContext;

    @InjectMocks
    private UsuariosSyncTasklet tasklet;

    @Captor
    private ArgumentCaptor<List<UsuarioDTO>> usuariosCaptor;

    private UsuarioSonar crearUsuarioSonar(String email, String nombre) {
        UsuarioSonar usuario = new UsuarioSonar();
        usuario.setCorreo(email);
        usuario.setEmail(email);
        usuario.setNombreEmpleado(nombre);
        usuario.setLogin(email);
        usuario.setActive(true);
        return usuario;
    }

    private UsuarioDTO crearUsuarioCaptio(Integer id, String email) {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setId(id);
        usuario.setEmail(email);
        usuario.setLogin(email);
        return usuario;
    }

    @Test
    @DisplayName("Debe separar correctamente usuarios nuevos de existentes")
    void testSepararUsuariosNuevosYExistentes() throws Exception {
        // Arrange
        UsuarioSonar usuarioNuevo = crearUsuarioSonar("nuevo@bbva.com", "Usuario Nuevo");
        UsuarioSonar usuarioExistente = crearUsuarioSonar("existente@bbva.com", "Usuario Existente");

        List<UsuarioSonar> usuariosSonar = Arrays.asList(usuarioNuevo, usuarioExistente);

        // Usuario existente en Captio
        UsuarioDTO usuarioCaptio = crearUsuarioCaptio(123, "existente@bbva.com");

        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(usuariosSonar);
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Collections.emptyList());

        // Simular búsqueda en Captio - nuevo no existe, existente sí
        when(usuarioService.obtenerUsuarioByFiltro(contains("nuevo@bbva.com")))
                .thenReturn(Collections.emptyList());
        when(usuarioService.obtenerUsuarioByFiltro(contains("existente@bbva.com")))
                .thenReturn(Arrays.asList(usuarioCaptio));

        when(usuarioAmexNewRepository.obtenerTarjeta(anyString())).thenReturn(null);

        // Act
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);

        // Assert
        assertEquals(RepeatStatus.FINISHED, result);

        // Verificar que se llamó altaUsuario para el nuevo
        verify(usuarioService).altaUsuario(usuariosCaptor.capture());
        List<UsuarioDTO> usuariosCreados = usuariosCaptor.getValue();
        assertEquals(1, usuariosCreados.size());
        assertEquals("nuevo@bbva.com", usuariosCreados.get(0).getEmail());

        // Verificar que se llamó updateUsuarios para el existente
        verify(usuarioService).updateUsuarios(usuariosCaptor.capture());
        List<UsuarioDTO> usuariosActualizados = usuariosCaptor.getValue();
        assertEquals(1, usuariosActualizados.size());
        assertEquals(123, usuariosActualizados.get(0).getId());
    }

    @Test
    @DisplayName("Debe ignorar usuarios sin email válido")
    void testIgnorarUsuariosSinEmail() throws Exception {
        // Arrange
        UsuarioSonar usuarioSinEmail = crearUsuarioSonar("", "Sin Email");
        UsuarioSonar usuarioConEmail = crearUsuarioSonar("valido@bbva.com", "Con Email");

        List<UsuarioSonar> usuariosSonar = Arrays.asList(usuarioSinEmail, usuarioConEmail);

        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(usuariosSonar);
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Collections.emptyList());
        when(usuarioService.obtenerUsuarioByFiltro(contains("valido@bbva.com")))
                .thenReturn(Collections.emptyList());
        when(usuarioAmexNewRepository.obtenerTarjeta(anyString())).thenReturn(null);

        // Act
        tasklet.execute(stepContribution, chunkContext);

        // Assert - solo debe procesar el usuario con email válido
        verify(usuarioService).altaUsuario(usuariosCaptor.capture());
        List<UsuarioDTO> usuariosCreados = usuariosCaptor.getValue();
        assertEquals(1, usuariosCreados.size());
        assertEquals("valido@bbva.com", usuariosCreados.get(0).getEmail());
    }

    @Test
    @DisplayName("Debe asignar TDC del repositorio AMEX a usuarios")
    void testAsignarTDC() throws Exception {
        // Arrange
        UsuarioSonar usuario = crearUsuarioSonar("usuario@bbva.com", "Usuario Test");
        String tdcEsperada = "4532123456789012";

        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(Arrays.asList(usuario));
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Collections.emptyList());
        when(usuarioService.obtenerUsuarioByFiltro(anyString())).thenReturn(Collections.emptyList());
        when(usuarioAmexNewRepository.obtenerTarjeta("usuario@bbva.com")).thenReturn(tdcEsperada);

        // Act
        tasklet.execute(stepContribution, chunkContext);

        // Assert
        verify(usuarioService).altaUsuario(usuariosCaptor.capture());
        List<UsuarioDTO> usuarios = usuariosCaptor.getValue();
        assertEquals(tdcEsperada, usuarios.get(0).getTdc());
    }

    @Test
    @DisplayName("Debe sincronizar grupos para usuarios existentes")
    void testSincronizarGruposUsuariosExistentes() throws Exception {
        // Arrange
        UsuarioSonar usuarioExistente = crearUsuarioSonar("existente@bbva.com", "Existente");
        UsuarioDTO usuarioCaptio = crearUsuarioCaptio(456, "existente@bbva.com");

        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(Arrays.asList(usuarioExistente));
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Collections.emptyList());
        when(usuarioService.obtenerUsuarioByFiltro(contains("existente@bbva.com")))
                .thenReturn(Arrays.asList(usuarioCaptio));
        when(usuarioAmexNewRepository.obtenerTarjeta(anyString())).thenReturn(null);

        // Act
        tasklet.execute(stepContribution, chunkContext);

        // Assert - verificar sincronización de grupos
        verify(usuarioService).sincronizarGrupoViajes(anyList());
        verify(usuarioService).sincronizarGrupoKm(anyList());
        verify(usuarioService).sincronizarPayments(anyList());
    }

    @Test
    @DisplayName("No debe llamar a altaUsuario si no hay usuarios nuevos")
    void testSinUsuariosNuevos() throws Exception {
        // Arrange
        UsuarioSonar usuarioExistente = crearUsuarioSonar("existente@bbva.com", "Existente");
        UsuarioDTO usuarioCaptio = crearUsuarioCaptio(789, "existente@bbva.com");

        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(Arrays.asList(usuarioExistente));
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Collections.emptyList());
        when(usuarioService.obtenerUsuarioByFiltro(anyString())).thenReturn(Arrays.asList(usuarioCaptio));
        when(usuarioAmexNewRepository.obtenerTarjeta(anyString())).thenReturn(null);

        // Act
        tasklet.execute(stepContribution, chunkContext);

        // Assert - no debe llamar a altaUsuario
        verify(usuarioService, never()).altaUsuario(anyList());
        // Pero sí debe llamar a updateUsuarios
        verify(usuarioService).updateUsuarios(anyList());
    }

    @Test
    @DisplayName("Debe procesar bajas de usuarios")
    void testBajaUsuarios() throws Exception {
        // Arrange
        UsuarioSonar usuarioBaja = crearUsuarioSonar("baja@bbva.com", "Usuario Baja");

        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(Collections.emptyList());
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Arrays.asList(usuarioBaja));

        // Act
        tasklet.execute(stepContribution, chunkContext);

        // Assert
        verify(usuarioService).bajaUsuario(usuariosCaptor.capture());
        assertEquals(1, usuariosCaptor.getValue().size());
    }

    @Test
    @DisplayName("Debe retornar FINISHED cuando no hay usuarios")
    void testSinUsuarios() throws Exception {
        // Arrange
        when(usuarioSonarRepository.obtenerUsuariosActivos()).thenReturn(Collections.emptyList());
        when(usuarioSonarRepository.obtenerUsuariosBaja()).thenReturn(Collections.emptyList());

        // Act
        RepeatStatus result = tasklet.execute(stepContribution, chunkContext);

        // Assert
        assertEquals(RepeatStatus.FINISHED, result);
        verify(usuarioService, never()).altaUsuario(anyList());
        verify(usuarioService, never()).updateUsuarios(anyList());
    }
}
