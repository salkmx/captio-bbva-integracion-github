package com.sngular.captio.tasklet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.common.base.Stopwatch;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.LenguajeDTO;
import com.sngular.captio.dto.PermisoDTO;
import com.sngular.captio.dto.StepDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.WorkFlowDTO;
import com.sngular.captio.enums.TipoWorkFlowEnum;
import com.sngular.captio.model.UsuarioSonar;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.repository.UsuarioSonarRepository;
import com.sngular.captio.services.EmailService;
import com.sngular.captio.services.FlujoAprobacionService;
import com.sngular.captio.services.UsuarioService;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Tasklet que sincroniza los workflows de aprobación entre el archivo de
 * empleados y Captio.
 *
 * Lógica de sincronización:
 * 1. Construye un mapa de workflows DESEADOS desde el archivo (por usuario)
 * 2. Construye un mapa de workflows ACTUALES desde Captio (por usuario)
 * 3. Compara ambos mapas para cada usuario:
 * - AÑADIR: workflows en DESEADO pero no en ACTUAL
 * - QUITAR: workflows en ACTUAL pero no en DESEADO (excepto el workflow fijo
 * 232)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FlujoAprobacionTasklet implements Tasklet {

	private static final Integer WORKFLOW_FIJO_ID = 232;
	private static final Integer WORKFLOW_FIJO_ID_294 = 294;

	// Patrón para validar formato de código de autorizador (alfanumérico,
	// típicamente M, MB, MI seguido de números)
	private static final Pattern PATRON_CODIGO_AUTORIZADOR = Pattern.compile("^[A-Z]{1,2}\\d{4,7}$");

	// Nombres de las etapas del workflow por posición (índice 0 = Etapa 1, etc.)
	private static final List<String> NOMBRES_ETAPAS = List.of("Líder", "Controller");

	private final UsuarioSonarRepository usuarioSonarRepository;
	private final UsuarioService usuarioService;
	private final FlujoAprobacionService flujoAprobacionService;
	private final Properties properties;
	private final EmailService emailService;

	// Cache de workflows por clave de autorizadores:
	// "tipo_autorizador1Id_autorizador2Id" -> WorkFlowDTO
	private final Map<String, WorkFlowDTO> workflowCachePorClave = new HashMap<>();

	// Cache de workflows existentes por nombre (cargados al inicio desde Captio)
	private final Map<String, WorkFlowDTO> workflowsExistentesPorNombre = new HashMap<>();

	// Cache de usuarios de Captio por email
	private final Map<String, UsuarioDTO> usuarioCaptioCache = new HashMap<>();

	// Cache de tipo Captio por ID de workflow (para controlar flag Default)
	private final Map<Integer, Integer> workflowTipoPorId = new HashMap<>();

	// Lista de errores de autorizadores para reporte
	private final List<Map<String, Object>> erroresAutorizadores = new ArrayList<>();

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		log.info("Iniciando sincronización de flujos de aprobación");
        var sp = Stopwatch.createStarted();
		// Limpiar caches y errores al inicio
		workflowCachePorClave.clear();
		workflowsExistentesPorNombre.clear();
		usuarioCaptioCache.clear();
		erroresAutorizadores.clear();
		workflowTipoPorId.clear();

		List<UsuarioSonar> usuariosActivosSonar = usuarioSonarRepository.obtenerUsuariosActivos();

		if (usuariosActivosSonar == null || usuariosActivosSonar.isEmpty()) {
			log.warn("No hay usuarios activos en el archivo para procesar");
			return RepeatStatus.FINISHED;
		}

		// Obtener workflows fijos (232 y 294)
		WorkFlowDTO workflowFijo232 = obtenerWorkflowFijo();
		WorkFlowDTO workflowFijo294 = obtenerWorkflowFijo294();

		// Registrar tipos de workflows fijos
		if (workflowFijo232 != null && workflowFijo232.getId() != null && workflowFijo232.getType() != null) {
			workflowTipoPorId.put(workflowFijo232.getId(), workflowFijo232.getType());
		}
		if (workflowFijo294 != null && workflowFijo294.getId() != null && workflowFijo294.getType() != null) {
			workflowTipoPorId.put(workflowFijo294.getId(), workflowFijo294.getType());
		}

		// PASO 0: Cargar TODOS los workflows existentes de Captio para evitar
		// duplicados
		log.info("PASO 0: Cargando todos los workflows existentes de Captio...");
		cargarWorkflowsExistentes();
		log.info("{} workflows existentes cargados en cache por nombre", workflowsExistentesPorNombre.size());

		// PASO 0.5: Resolver autorizador fijo para Etapa 1 de Informes viaje
		log.info("PASO 0.5: Resolviendo autorizador fijo para Informes viaje...");
		UsuarioDTO autorizadorFijo = resolverAutorizadorFijo();
		if (autorizadorFijo == null) {
			log.warn("No se pudo resolver el autorizador fijo ({}). Los workflows de Informes viaje NO se crearán.",
					properties.getEmailAutorizadorFijo());
		} else {
			log.info("Autorizador fijo resuelto: {} (ID: {})", autorizadorFijo.getName(), autorizadorFijo.getId());
		}

		// PASO 1: Obtener TODOS los usuarios con workflows de Captio (una sola llamada)
		log.info("PASO 1: Obteniendo todos los usuarios con workflows de la plataforma Captio...");
		Map<Integer, Set<Integer>> workflowsActualesCaptio = construirMapaWorkflowsActualesDeCapito();
		log.info("Workflows actuales en Captio: {} usuarios tienen workflows asignados",
				workflowsActualesCaptio.size());

		// PASO 2: Construir mapa de workflows DESEADOS desde el archivo
		log.info("PASO 2: Construyendo mapa de workflows deseados desde el archivo...");
		Map<Integer, Set<Integer>> workflowsDeseados = construirMapaWorkflowsDeseados(usuariosActivosSonar,
				workflowFijo232, workflowFijo294, autorizadorFijo);
		log.info("Workflows deseados construidos para {} usuarios del archivo", workflowsDeseados.size());

		// PASO 3: Comparar y sincronizar
		log.info("PASO 3: Comparando y sincronizando workflows...");
		sincronizarWorkflows(workflowsDeseados, workflowsActualesCaptio);

		// PASO 4: Reportar errores de autorizadores si los hay
		if (!erroresAutorizadores.isEmpty()) {
			escribirErroresAutorizadores();
			log.warn("{} errores de autorizadores detectados - registrados en archivo de errores",
					erroresAutorizadores.size());
		}

		log.info("Sincronización de flujos completada. Workflows únicos en cache: {}", workflowCachePorClave.size());
        log.info("[{}][execute] took {}sg", this.getClass().getSimpleName(), sp.elapsed(TimeUnit.SECONDS));
		return RepeatStatus.FINISHED;
	}

	/**
	 * Carga todos los workflows existentes de Captio al inicio para evitar
	 * duplicados.
	 * Construye un mapa por nombre para búsqueda rápida.
	 */
	private void cargarWorkflowsExistentes() {
		try {
			List<WorkFlowDTO> workflows = flujoAprobacionService.obtenerTodosWorkflows();
			for (WorkFlowDTO wf : workflows) {
				if (wf.getName() != null && !wf.getName().isBlank()) {
					workflowsExistentesPorNombre.put(wf.getName(), wf);
				}
			}
		} catch (Exception e) {
			log.error("Error al cargar workflows existentes: {}", e.getMessage());
		}
	}

	/**
	 * Construye el mapa de workflows ACTUALES obteniendo TODOS los usuarios de
	 * Captio
	 * con sus workflows en una sola operación (paginada).
	 *
	 * @return Mapa ID de usuario -> Set de IDs de workflows asignados
	 */
	private Map<Integer, Set<Integer>> construirMapaWorkflowsActualesDeCapito() {
		Map<Integer, Set<Integer>> mapa = new HashMap<>();
		int usuariosConWorkflows = 0;
		int usuariosSinWorkflows = 0;

		try {
			List<UsuarioDTO> todosUsuarios = usuarioService.obtenerTodosUsuariosConWorkflows();
			log.info("Usuarios obtenidos de Captio (Users/Workflows): {}", todosUsuarios.size());

			for (UsuarioDTO usuario : todosUsuarios) {
				if (usuario.getId() != null) {
					// Extraer IDs de workflows
					Set<Integer> workflowIds = new HashSet<>();
					if (usuario.getWorkflows() != null && !usuario.getWorkflows().isEmpty()) {
						for (WorkFlowDTO wf : usuario.getWorkflows()) {
							if (wf.getId() != null) {
								workflowIds.add(wf.getId());
							}
						}
						usuariosConWorkflows++;
						log.debug("Usuario ID={} tiene workflows asignados: {}", usuario.getId(), workflowIds);
					} else {
						usuariosSinWorkflows++;
					}
					mapa.put(usuario.getId(), workflowIds);
				}
			}

			log.info(
					"📊 Mapa de workflows actuales construido: {} usuarios procesados ({} con workflows, {} sin workflows)",
					mapa.size(), usuariosConWorkflows, usuariosSinWorkflows);

		} catch (Exception e) {
			log.error("Error al obtener todos los usuarios con workflows: {}", e.getMessage(), e);
		}

		return mapa;
	}

	/**
	 * Construye el mapa de workflows DESEADOS basándose en el archivo de empleados.
	 * Para cada usuario, determina qué workflows debería tener según sus niveles
	 * jerárquicos (N2, N3, N4). También valida los códigos y registra errores.
	 *
	 * Workflows dinámicos por usuario:
	 * - WF Viajes Nacional (tipo 3): 1 etapa → N3
	 * - WF Viajes Extranjero (tipo 3): 1 etapa → N2
	 * - WF Informes viaje Nacional (tipo 1): 3 etapas → Fijo, N3, N4
	 * - WF Informes viaje Extranjero (tipo 1): 3 etapas → Fijo, N3, N2
	 * - WF Informe gastos locales (tipo 1): 1 etapa → N4 (jefe inmediato)
	 *
	 * @return Mapa ID de usuario Captio -> Set de IDs de workflows deseados
	 */
	private Map<Integer, Set<Integer>> construirMapaWorkflowsDeseados(List<UsuarioSonar> usuariosArchivo,
			WorkFlowDTO workflowFijo232, WorkFlowDTO workflowFijo294, UsuarioDTO autorizadorFijo) {
		Map<Integer, Set<Integer>> mapa = new HashMap<>();

		for (UsuarioSonar usuarioSonar : usuariosArchivo) {
			try {
				String email = usuarioSonar.getEmail();
				if (email == null || email.isBlank()) {
					continue;
				}

				// Obtener usuario de Captio
				UsuarioDTO usuarioCaptio = obtenerUsuarioCaptio(email);
				if (usuarioCaptio == null || usuarioCaptio.getId() == null) {
					log.warn("Usuario no encontrado en Captio: {}", email);
					continue;
				}

				Set<Integer> workflowIds = new HashSet<>();

				// PRIMERO: Siempre añadir workflows fijos (232 Dotación y 294 Gastos médicos)
				if (workflowFijo232 != null && workflowFijo232.getId() != null) {
					workflowIds.add(workflowFijo232.getId());
				}
				if (workflowFijo294 != null && workflowFijo294.getId() != null) {
					workflowIds.add(workflowFijo294.getId());
				}

				// SEGUNDO: Leer niveles jerárquicos del archivo
				String codigoN2 = usuarioSonar.getLoginN2();
				String codigoN3 = usuarioSonar.getLoginN3();
				String codigoN4 = usuarioSonar.getLoginN4();

				// Validar formato de cada código individualmente
				boolean n2FormatoOk = validarCodigoAutorizador(codigoN2, "N2", email, usuarioSonar);
				boolean n3FormatoOk = validarCodigoAutorizador(codigoN3, "N3", email, usuarioSonar);
				boolean n4FormatoOk = validarCodigoAutorizador(codigoN4, "N4", email, usuarioSonar);

				// Resolver autorizadores (solo si el formato es válido)
				UsuarioDTO autN2 = n2FormatoOk
						? obtenerAutorizadorConValidacion(codigoN2, "N2", email, usuarioSonar)
						: null;
				UsuarioDTO autN3 = n3FormatoOk
						? obtenerAutorizadorConValidacion(codigoN3, "N3", email, usuarioSonar)
						: null;
				UsuarioDTO autN4 = n4FormatoOk
						? obtenerAutorizadorConValidacion(codigoN4, "N4", email, usuarioSonar)
						: null;

				boolean n2Valido = esAutorizadorValido(autN2);
				boolean n3Valido = esAutorizadorValido(autN3);
				boolean n4Valido = esAutorizadorValido(autN4);

				// --- WF VIAJES NACIONAL (1 etapa: N3 como Responsable) ---
				if (n3Valido) {
					WorkFlowDTO wfVN = obtenerOcrearWorkflow(
							TipoWorkFlowEnum.VIAJES_NACIONAL,
							List.of(autN3),
							autN3.getName(), null);
					if (wfVN != null && wfVN.getId() != null) {
						workflowIds.add(wfVN.getId());
					}
				} else {
					log.warn("Usuario {}: No se crea WF Viajes Nacional (N3 inválido)", email);
				}

				// --- WF VIAJES EXTRANJERO (1 etapa: N2 como Responsable) ---
				if (n2Valido) {
					WorkFlowDTO wfVE = obtenerOcrearWorkflow(
							TipoWorkFlowEnum.VIAJES_EXTRANJERO,
							List.of(autN2),
							autN2.getName(), null);
					if (wfVE != null && wfVE.getId() != null) {
						workflowIds.add(wfVE.getId());
					}
				} else {
					log.warn("Usuario {}: No se crea WF Viajes Extranjero (N2 inválido)", email);
				}

				// --- WF INFORMES VIAJE NACIONAL (3 etapas: Fijo, N3, N4) ---
				if (autorizadorFijo != null && n3Valido && n4Valido) {
					if (!autN3.getId().equals(autN4.getId())) {
						WorkFlowDTO wfIN = obtenerOcrearWorkflow(
								TipoWorkFlowEnum.INFORMES_VIAJE_NACIONAL,
								List.of(autorizadorFijo, autN3, autN4),
								autN3.getName(), autN4.getName());
						if (wfIN != null && wfIN.getId() != null) {
							workflowIds.add(wfIN.getId());
						}
					} else {
						registrarErrorAutorizador(email, codigoN3 + "/" + codigoN4, "N3_N4_DUPLICATED",
								"N3 y N4 son el mismo usuario (ID: " + autN3.getId()
										+ ") para Informes viaje Nacional. Deben ser diferentes.",
								usuarioSonar);
						log.warn("Usuario {}: N3 y N4 son el mismo usuario. No se crea WF Informes viaje Nacional.",
								email);
					}
				} else {
					log.warn("Usuario {}: No se crea WF Informes viaje Nacional (fijo={}, N3={}, N4={})",
							email, autorizadorFijo != null, n3Valido, n4Valido);
				}

				// --- WF INFORMES VIAJE EXTRANJERO (3 etapas: Fijo, N3, N2 como Supervisor) ---
				if (autorizadorFijo != null && n3Valido && n2Valido) {
					if (!autN3.getId().equals(autN2.getId())) {
						WorkFlowDTO wfIE = obtenerOcrearWorkflow(
								TipoWorkFlowEnum.INFORMES_VIAJE_EXTRANJERO,
								List.of(autorizadorFijo, autN3, autN2),
								autN3.getName(), autN2.getName());
						if (wfIE != null && wfIE.getId() != null) {
							workflowIds.add(wfIE.getId());
						}
					} else {
						registrarErrorAutorizador(email, codigoN3 + "/" + codigoN2, "N3_N2_DUPLICATED",
								"N3 y N2 son el mismo usuario (ID: " + autN3.getId()
										+ ") para Informes viaje Extranjero. Deben ser diferentes.",
								usuarioSonar);
						log.warn(
								"Usuario {}: N3 y N2 son el mismo usuario. No se crea WF Informes viaje Extranjero.",
								email);
					}
				} else {
					log.warn("Usuario {}: No se crea WF Informes viaje Extranjero (fijo={}, N3={}, N2={})",
							email, autorizadorFijo != null, n3Valido, n2Valido);
				}

				// --- WF INFORME GASTOS LOCALES (1 etapa: N4 como Responsable / jefe inmediato)
				// ---
				if (n4Valido) {
					WorkFlowDTO wfGL = obtenerOcrearWorkflow(
							TipoWorkFlowEnum.GASTOS_LOCALES,
							List.of(autN4),
							autN4.getName(), null);
					if (wfGL != null && wfGL.getId() != null) {
						workflowIds.add(wfGL.getId());
					}
				} else {
					log.warn("Usuario {}: No se crea WF Informe gastos locales (N4 inválido)", email);
				}

				// Agregar al mapa si tiene al menos un workflow (mínimo los fijos)
				if (!workflowIds.isEmpty()) {
					mapa.put(usuarioCaptio.getId(), workflowIds);
					log.debug("Usuario {} (ID:{}): workflows deseados = {}", email, usuarioCaptio.getId(),
							workflowIds);
				}

			} catch (Exception e) {
				log.error("Error procesando usuario {}: {}", usuarioSonar.getEmail(), e.getMessage());
			}
		}

		return mapa;
	}

	/**
	 * Valida el formato de un código de autorizador individual.
	 * El formato esperado es: 1-2 letras mayúsculas seguidas de 4-7 dígitos
	 * (ej: M911938, MB10282, MI05590)
	 *
	 * @return true si el código es válido, false si hay errores
	 */
	private boolean validarCodigoAutorizador(String codigo, String nivelJerarquico, String emailUsuario,
			UsuarioSonar usuarioSonar) {
		if (codigo == null || codigo.isBlank()) {
			String mensajeError = nivelJerarquico + " no tiene valor o está vacío";
			registrarErrorAutorizador(emailUsuario, "(vacío)", "EMPTY_" + nivelJerarquico, mensajeError, usuarioSonar);
			log.warn("Usuario {}: {}", emailUsuario, mensajeError);
			return false;
		}
		if (!PATRON_CODIGO_AUTORIZADOR.matcher(codigo.trim()).matches()) {
			String mensajeError = nivelJerarquico + " no tiene formato correcto: '" + codigo +
					"' (esperado: 1-2 letras + 4-7 dígitos, ej: M911938, MB10282)";
			registrarErrorAutorizador(emailUsuario, codigo, "INVALID_FORMAT_" + nivelJerarquico, mensajeError,
					usuarioSonar);
			log.warn("Usuario {}: {}", emailUsuario, mensajeError);
			return false;
		}
		return true;
	}

	/**
	 * Verifica si un autorizador es válido (no nulo, tiene ID y nombre).
	 */
	private boolean esAutorizadorValido(UsuarioDTO autorizador) {
		return autorizador != null
				&& autorizador.getId() != null
				&& autorizador.getName() != null
				&& !autorizador.getName().isBlank();
	}

	/**
	 * Resuelve el autorizador fijo por email (configurado en
	 * application.properties).
	 * Se usa como Etapa 1 en los workflows de Informes viaje.
	 */
	private UsuarioDTO resolverAutorizadorFijo() {
		String emailFijo = properties.getEmailAutorizadorFijo();
		if (emailFijo == null || emailFijo.isBlank()) {
			log.error("No se ha configurado el email del autorizador fijo (captio.workflow.autorizador.fijo.email)");
			return null;
		}
		try {
			List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarioByFiltro("{\"Email\":\"" + emailFijo + "\"}");
			if (usuarios != null && !usuarios.isEmpty()) {
				UsuarioDTO fijo = usuarios.get(0);
				if (fijo.getId() != null && fijo.getName() != null && !fijo.getName().isBlank()) {
					return fijo;
				}
				log.error("Autorizador fijo encontrado pero sin ID o nombre: email={}", emailFijo);
			} else {
				log.error("Autorizador fijo no encontrado en Captio: email={}", emailFijo);
			}
		} catch (Exception e) {
			log.error("Error al resolver autorizador fijo {}: {}", emailFijo, e.getMessage());
		}
		return null;
	}

	/**
	 * Obtiene un autorizador por código y registra error si no se encuentra.
	 */
	private UsuarioDTO obtenerAutorizadorConValidacion(String codigoEmpleado, String tipoAutorizador,
			String emailUsuario, UsuarioSonar usuarioSonar) {
		if (codigoEmpleado == null || codigoEmpleado.isBlank()) {
			return null;
		}

		UsuarioDTO autorizador = obtenerAutorizadorPorCodigo(codigoEmpleado.trim());

		if (autorizador == null) {
			String mensajeError = tipoAutorizador + " con código '" + codigoEmpleado + "' no encontrado en Captio";
			registrarErrorAutorizador(emailUsuario, codigoEmpleado, "AUTH_NOT_FOUND_IN_CAPTIO", mensajeError,
					usuarioSonar);
			log.warn("Usuario {}: {}", emailUsuario, mensajeError);
		}

		return autorizador;
	}

	/**
	 * Registra un error de autorizador en formato JSON.
	 */
	private void registrarErrorAutorizador(String emailUsuario, String valorAutorizador, String codigoError,
			String mensaje, UsuarioSonar usuarioSonar) {
		Map<String, Object> errorEntry = new HashMap<>();
		errorEntry.put("Key", "Autorizador");
		errorEntry.put("Value", valorAutorizador);

		Map<String, Object> validation = new HashMap<>();
		validation.put("Message", mensaje + " | Usuario: " + emailUsuario +
				" | Empleado: " + (usuarioSonar.getNombreEmpleado() != null ? usuarioSonar.getNombreEmpleado() : "N/A")
				+
				" | Codigo: " + (usuarioSonar.getCodigoEmpleado() != null ? usuarioSonar.getCodigoEmpleado() : "N/A"));
		validation.put("Code", codigoError);
		validation.put("Key", "stepFlujoAprobacion");
		validation.put("Value", emailUsuario);

		errorEntry.put("Validations", List.of(validation));
		errorEntry.put("Status", 1);

		erroresAutorizadores.add(errorEntry);
	}

	/**
	 * Escribe los errores de autorizadores en el archivo CSV y envía email de
	 * notificación.
	 */
	private void escribirErroresAutorizadores() {
		String ruta = properties.getRutaArchivoErrorWorkFlow() + DateUtils.obtenerFechaActual() + ".csv";

		try {
			File archivo = new File(ruta);
			archivo.getParentFile().mkdirs();

			// Convertir a JSON
			ObjectMapper mapper = new ObjectMapper();
			String jsonError = mapper.writeValueAsString(erroresAutorizadores);

			// Construir mensaje para email
			StringBuilder sb = new StringBuilder();
			sb.append("[stepFlujoAprobacion] Errores de autorizadores detectados:").append(System.lineSeparator());
			sb.append(System.lineSeparator());
			for (Map<String, Object> error : erroresAutorizadores) {
				sb.append("- Código: ").append(error.get("Value"));
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> validations = (List<Map<String, Object>>) error.get("Validations");
				if (validations != null && !validations.isEmpty()) {
					sb.append(" → ").append(validations.get(0).get("Message"));
				}
				sb.append(System.lineSeparator());
			}

			// Enviar email
			for (String errorMail : properties.getErrorMails()) {
				emailService.enviarCorreo(
						emailService.crearMailDTO(errorMail, "Error Flujo Aprobación - Autorizadores", sb.toString(),
								"email/error_workflow"));
			}

			// Escribir en archivo CSV con UTF-8
			try (BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(archivo, true), StandardCharsets.UTF_8))) {
				writer.write(jsonError);
				writer.newLine();
			}
			log.info("Errores de autorizadores escritos en: {}", ruta);
		} catch (IOException e) {
			log.error("Error al escribir archivo de errores de workflow: {}", e.getMessage(), e);
		}
	}

	/**
	 * Sincroniza los workflows comparando estado deseado vs actual.
	 * Los mapas usan ID de usuario como clave.
	 */
	private void sincronizarWorkflows(Map<Integer, Set<Integer>> deseados, Map<Integer, Set<Integer>> actuales)
			throws Exception {

		List<UsuarioDTO> usuariosParaAgregar = new ArrayList<>();
		List<UsuarioDTO> usuariosParaQuitar = new ArrayList<>();
		int usuariosYaSincronizados = 0;

		log.info("Analizando {} usuarios del archivo vs {} usuarios en Captio", deseados.size(), actuales.size());

		for (Map.Entry<Integer, Set<Integer>> entry : deseados.entrySet()) {
			Integer userId = entry.getKey();
			Set<Integer> wfDeseados = entry.getValue();
			Set<Integer> wfActuales = actuales.getOrDefault(userId, new HashSet<>());

			log.debug("Usuario ID={}: DESEADOS={}, ACTUALES={}", userId, wfDeseados, wfActuales);

			// Calcular diferencias
			Set<Integer> paraAgregar = new HashSet<>(wfDeseados);
			paraAgregar.removeAll(wfActuales); // DESEADO - ACTUAL = A AGREGAR

			Set<Integer> paraQuitar = new HashSet<>(wfActuales);
			paraQuitar.removeAll(wfDeseados); // ACTUAL - DESEADO = A QUITAR

			// Preparar usuario para agregar workflows (solo si hay algo que agregar)
			if (!paraAgregar.isEmpty()) {
				UsuarioDTO usuarioJoin = new UsuarioDTO();
				usuarioJoin.setId(userId);

				// Controlar que solo 1 workflow por tipo Captio sea Default=true
				Set<Integer> tiposConDefault = new HashSet<>();
				List<WorkFlowDTO> workflowsJoin = paraAgregar.stream().map(wfId -> {
					WorkFlowDTO wf = new WorkFlowDTO();
					wf.setId(wfId);
					Integer tipoCaptio = workflowTipoPorId.get(wfId);
					if (tipoCaptio != null && !tiposConDefault.add(tipoCaptio)) {
						// Ya hay un default para este tipo → false
						wf.setDefaulte(false);
						log.debug("WF ID={} tipo={} → Default=false (ya existe otro default de este tipo)", wfId,
								tipoCaptio);
					} else {
						wf.setDefaulte(true);
					}
					return wf;
				}).toList();
				usuarioJoin.setWorkflows(new ArrayList<>(workflowsJoin));
				usuariosParaAgregar.add(usuarioJoin);
				log.info("Usuario ID={}: AGREGAR workflows {}", userId, paraAgregar);
			} else if (wfDeseados.equals(wfActuales)) {
				usuariosYaSincronizados++;
				log.debug("Usuario ID={} ya tiene todos los workflows correctos", userId);
			}

			// Preparar usuario para quitar workflows
			if (!paraQuitar.isEmpty()) {
				UsuarioDTO usuarioUnjoin = new UsuarioDTO();
				usuarioUnjoin.setId(userId);
				List<WorkFlowDTO> workflowsUnjoin = paraQuitar.stream().map(wfId -> {
					WorkFlowDTO wf = new WorkFlowDTO();
					wf.setId(wfId);
					return wf;
				}).toList();
				usuarioUnjoin.setWorkflows(new ArrayList<>(workflowsUnjoin));
				usuariosParaQuitar.add(usuarioUnjoin);
				log.info("Usuario ID={}: QUITAR workflows {}", userId, paraQuitar);
			}
		}

		// Ejecutar operaciones
		if (!usuariosParaQuitar.isEmpty()) {
			log.info("Desasignando workflows de {} usuarios", usuariosParaQuitar.size());
			usuarioService.unjoinWorkFlow(usuariosParaQuitar);
		}

		if (!usuariosParaAgregar.isEmpty()) {
			log.info("Asignando workflows a {} usuarios", usuariosParaAgregar.size());
			usuarioService.joinWorkFlow(usuariosParaAgregar);
		}

		log.info(
				"Resumen sincronización: {} usuarios para agregar workflows, {} usuarios para quitar workflows, {} usuarios ya sincronizados",
				usuariosParaAgregar.size(), usuariosParaQuitar.size(), usuariosYaSincronizados);
	}

	/**
	 * Obtiene el workflow fijo (ID 232) de Captio.
	 */
	private WorkFlowDTO obtenerWorkflowFijo() {
		try {
			return flujoAprobacionService.obtenerFlujoByFiltro("{\"Id\":" + WORKFLOW_FIJO_ID + "}");
		} catch (Exception e) {
			log.warn("No se pudo obtener workflow fijo con ID {}: {}", WORKFLOW_FIJO_ID, e.getMessage());
			return null;
		}
	}

	/**
	 * Obtiene el workflow fijo (ID 294) de Captio.
	 */
	private WorkFlowDTO obtenerWorkflowFijo294() {
		try {
			return flujoAprobacionService.obtenerFlujoByFiltro("{\"Id\":" + WORKFLOW_FIJO_ID_294 + "}");
		} catch (Exception e) {
			log.warn("No se pudo obtener workflow fijo con ID {}: {}", WORKFLOW_FIJO_ID_294, e.getMessage());
			return null;
		}
	}

	/**
	 * Obtiene usuario de Captio por email, usando cache para evitar llamadas
	 * repetidas.
	 */
	private UsuarioDTO obtenerUsuarioCaptio(String email) {
		if (email == null || email.isBlank()) {
			return null;
		}

		String emailLower = email.toLowerCase();

		// Verificar cache
		if (usuarioCaptioCache.containsKey(emailLower)) {
			return usuarioCaptioCache.get(emailLower);
		}

		try {
			List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarioByFiltro("{\"Email\":\"" + email + "\"}");
			UsuarioDTO usuario = (usuarios != null && !usuarios.isEmpty()) ? usuarios.get(0) : null;
			usuarioCaptioCache.put(emailLower, usuario);
			return usuario;
		} catch (Exception e) {
			log.error("Error al obtener usuario por email {}: {}", email, e.getMessage());
			return null;
		}
	}

	/**
	 * Obtiene un workflow existente o lo crea si no existe.
	 * Usa una clave basada en los IDs de autorizadores y tipo para evitar
	 * duplicados.
	 *
	 * ESTRATEGIA PARA EVITAR DUPLICADOS Y NOMENCLATURA:
	 * 1. Busca en cache por clave exacta (tipo + todos los autorizadores)
	 * 2. Busca por nombre SIMPLE (solo autorizador principal)
	 * - Si existe Y los autorizadores coinciden → Reutilizar
	 * - Si existe Y NO coinciden → Crear con nombre COMPUESTO
	 * 3. Si no existe con nombre simple → Crear con nombre simple
	 *
	 * @param tipoWorkflow     Tipo de workflow a crear
	 * @param autorizadores    Lista ordenada de autorizadores (1 para viajes, 3
	 *                         para informes)
	 * @param nombrePrincipal  Nombre del autorizador principal (para nomenclatura
	 *                         simple)
	 * @param nombreSecundario Nombre del autorizador secundario (para nomenclatura
	 *                         compuesta, puede ser null)
	 */
	private WorkFlowDTO obtenerOcrearWorkflow(TipoWorkFlowEnum tipoWorkflow, List<UsuarioDTO> autorizadores,
			String nombrePrincipal, String nombreSecundario) {

		// Generar clave única basada en tipo + todos los IDs de autorizadores
		String cacheKey = generarClaveWorkflow(tipoWorkflow, autorizadores);

		// 1. Verificar si ya existe en cache de sesión actual
		if (workflowCachePorClave.containsKey(cacheKey)) {
			log.debug("Workflow encontrado en cache de sesión: {}", cacheKey);
			WorkFlowDTO cached = workflowCachePorClave.get(cacheKey);
			registrarTipoWorkflow(cached, tipoWorkflow);
			return cached;
		}

		// 2. Generar nombre SIMPLE
		String wfNameSimple = generarNombreWorkflowSimple(tipoWorkflow, nombrePrincipal);

		// Buscar workflow existente con nombre simple
		if (workflowsExistentesPorNombre.containsKey(wfNameSimple)) {
			WorkFlowDTO existente = workflowsExistentesPorNombre.get(wfNameSimple);

			// Verificar si los autorizadores del workflow existente coinciden
			if (verificarAutorizadoresCoinciden(existente, autorizadores)) {
				log.info("Reutilizando workflow existente (autorizadores coinciden): {} (ID: {})", wfNameSimple,
						existente.getId());
				workflowCachePorClave.put(cacheKey, existente);
				registrarTipoWorkflow(existente, tipoWorkflow);
				return existente;
			} else {
				// Autorizadores NO coinciden
				if (nombreSecundario != null && !nombreSecundario.isBlank()) {
					// Crear con nombre COMPUESTO para diferenciar
					log.info("Workflow {} existe pero autorizadores no coinciden. Creando con nombre compuesto.",
							wfNameSimple);

					String wfNameCompuesto = generarNombreWorkflowCompuesto(tipoWorkflow, nombrePrincipal,
							nombreSecundario);

					if (workflowsExistentesPorNombre.containsKey(wfNameCompuesto)) {
						WorkFlowDTO existenteCompuesto = workflowsExistentesPorNombre.get(wfNameCompuesto);
						log.info("Reutilizando workflow compuesto existente: {} (ID: {})", wfNameCompuesto,
								existenteCompuesto.getId());
						workflowCachePorClave.put(cacheKey, existenteCompuesto);
						registrarTipoWorkflow(existenteCompuesto, tipoWorkflow);
						return existenteCompuesto;
					}

					return crearNuevoWorkflow(wfNameCompuesto, tipoWorkflow, autorizadores, cacheKey);
				} else {
					// Sin nombre secundario (ej: Viajes con 1 etapa) → reutilizar el existente
					log.info("Workflow {} existe con autorizadores distintos pero sin nombre secundario disponible. "
							+ "Reutilizando existente (ID: {}).", wfNameSimple, existente.getId());
					workflowCachePorClave.put(cacheKey, existente);
					registrarTipoWorkflow(existente, tipoWorkflow);
					return existente;
				}
			}
		}

		// 3. No existe → Crear con nombre simple
		return crearNuevoWorkflow(wfNameSimple, tipoWorkflow, autorizadores, cacheKey);
	}

	/**
	 * Registra el tipo Captio de un workflow en el cache workflowTipoPorId.
	 * Se usa para controlar que solo 1 workflow por tipo sea Default al asignar.
	 */
	private void registrarTipoWorkflow(WorkFlowDTO wf, TipoWorkFlowEnum tipoWorkflow) {
		if (wf != null && wf.getId() != null) {
			workflowTipoPorId.put(wf.getId(), tipoWorkflow.getTipoFlujo());
		}
	}

	/**
	 * Verifica si los autorizadores de un workflow existente coinciden con los
	 * esperados.
	 */
	private boolean verificarAutorizadoresCoinciden(WorkFlowDTO workflow, List<UsuarioDTO> autorizadores) {
		if (workflow.getSteps() == null || workflow.getSteps().size() != autorizadores.size()) {
			return false;
		}
		for (int i = 0; i < autorizadores.size(); i++) {
			Integer expectedId = autorizadores.get(i).getId();
			Integer actualId = workflow.getSteps().get(i).getSupervisorId();
			if (!expectedId.equals(actualId)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Crea un nuevo workflow con el nombre especificado y lo guarda en caches.
	 *
	 * @param wfName        Nombre del workflow
	 * @param tipoWorkflow  Tipo de workflow
	 * @param autorizadores Lista ordenada de autorizadores (cada uno será un step)
	 * @param cacheKey      Clave para el cache
	 */
	private WorkFlowDTO crearNuevoWorkflow(String wfName, TipoWorkFlowEnum tipoWorkflow,
			List<UsuarioDTO> autorizadores, String cacheKey) {
		try {
			log.info("Creando nuevo workflow: {} (tipo={}, etapas={})", wfName, tipoWorkflow.name(),
					autorizadores.size());

			WorkFlowDTO nuevoWf = new WorkFlowDTO();
			nuevoWf.setName(wfName);
			nuevoWf.setType(tipoWorkflow.getTipoFlujo());
			nuevoWf.setRequesterDigitalSignatureRequired(false);

			List<WorkFlowDTO> flujos = new ArrayList<>();
			flujos.add(nuevoWf);

			Integer id = flujoAprobacionService.crearFlujoAprobacion(flujos);

			if (id == null) {
				log.error("No se pudo crear workflow: {}", wfName);
				return null;
			}

			nuevoWf.setId(id);

			// Agregar steps (un step por cada autorizador)
			nuevoWf.setSteps(buildSteps(autorizadores, wfName));
			flujoAprobacionService.agregarPaso(flujos);

			// Guardar en ambos caches para evitar duplicados en la misma sesión
			workflowCachePorClave.put(cacheKey, nuevoWf);
			workflowsExistentesPorNombre.put(wfName, nuevoWf);
			registrarTipoWorkflow(nuevoWf, tipoWorkflow);

			log.info("Workflow creado exitosamente: {} (ID: {}, etapas: {})", wfName, id, autorizadores.size());

			return nuevoWf;

		} catch (Exception e) {
			log.error("Error al crear workflow {}: {}", wfName, e.getMessage());
			return null;
		}
	}

	/**
	 * Genera una clave única para el cache basada en los IDs de autorizadores y
	 * tipo.
	 */
	private String generarClaveWorkflow(TipoWorkFlowEnum tipoWorkflow, List<UsuarioDTO> autorizadores) {
		String ids = autorizadores.stream()
				.map(a -> String.valueOf(a.getId()))
				.collect(Collectors.joining("_"));
		return tipoWorkflow.name() + "_" + ids;
	}

	/**
	 * Genera el nombre SIMPLE del workflow (solo autorizador principal):
	 * - Viajes Nacional: "Nacional {Nombre} Viajes"
	 * - Viajes Extranjero: "Extranjero {Nombre} Viajes"
	 * - Informes viaje Nacional: "{Nombre} Informes viaje Nacional"
	 * - Informes viaje Extranjero: "{Nombre} Informes viaje Extranjero"
	 * - Informe gastos locales: "{Nombre} Informe gastos locales"
	 *
	 * Este es el formato preferido. Solo se usa el formato compuesto cuando
	 * ya existe un workflow con el mismo nombre pero diferentes autorizadores.
	 */
	private String generarNombreWorkflowSimple(TipoWorkFlowEnum tipoWorkflow, String nombreAutorizadorPrincipal) {
		switch (tipoWorkflow) {
			case VIAJES_NACIONAL:
				return "Nacional " + nombreAutorizadorPrincipal + " Viajes";
			case VIAJES_EXTRANJERO:
				return "Extranjero " + nombreAutorizadorPrincipal + " Viajes";
			case INFORMES_VIAJE_NACIONAL:
				return nombreAutorizadorPrincipal + " Informes viaje Nacional";
			case INFORMES_VIAJE_EXTRANJERO:
				return nombreAutorizadorPrincipal + " Informes viaje Extranjero";
			case GASTOS_LOCALES:
				return nombreAutorizadorPrincipal + " Informe gastos locales";
			case INFORMES:
				return nombreAutorizadorPrincipal + " Informes";
			default:
				log.warn("Tipo de workflow no reconocido: {}", tipoWorkflow);
				return "WF_" + tipoWorkflow.name() + "_" + nombreAutorizadorPrincipal;
		}
	}

	/**
	 * Genera el nombre COMPUESTO del workflow (incluye autorizador secundario):
	 * Se usa SOLO cuando ya existe un workflow simple con el mismo nombre
	 * pero con diferentes autorizadores, para evitar conflictos de steps.
	 */
	private String generarNombreWorkflowCompuesto(TipoWorkFlowEnum tipoWorkflow, String nombrePrincipal,
			String nombreSecundario) {
		switch (tipoWorkflow) {
			case VIAJES_NACIONAL:
				return "Nacional " + nombrePrincipal + " - " + nombreSecundario + " Viajes";
			case VIAJES_EXTRANJERO:
				return "Extranjero " + nombrePrincipal + " - " + nombreSecundario + " Viajes";
			case INFORMES_VIAJE_NACIONAL:
				return nombrePrincipal + " - " + nombreSecundario + " Informes viaje Nacional";
			case INFORMES_VIAJE_EXTRANJERO:
				return nombrePrincipal + " - " + nombreSecundario + " Informes viaje Extranjero";
			case GASTOS_LOCALES:
				return nombrePrincipal + " - " + nombreSecundario + " Informe gastos locales";
			case INFORMES:
				return nombrePrincipal + " - " + nombreSecundario + " Informes";
			default:
				log.warn("Tipo de workflow no reconocido: {}", tipoWorkflow);
				return "WF_" + tipoWorkflow.name() + "_" + nombrePrincipal + "_" + nombreSecundario;
		}
	}

	/**
	 * Obtiene autorizador por código de empleado.
	 */
	private UsuarioDTO obtenerAutorizadorPorCodigo(String codigoEmpleado) {
		if (codigoEmpleado == null || codigoEmpleado.isBlank()) {
			return null;
		}
		try {
			List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarioByFiltro(
					"{\"UserOptions_EmployeeCode\":\"" + codigoEmpleado + "\"}");

			if (usuarios != null && !usuarios.isEmpty()) {
				UsuarioDTO autorizador = usuarios.get(0);
				// Validar que el autorizador tenga nombre
				if (autorizador.getName() == null || autorizador.getName().isBlank()) {
					log.warn(
							"⚠️ Autorizador encontrado con código '{}' (ID: {}, Email: {}) pero NO tiene campo 'Name' configurado en Captio. "
									+
									"Esto puede ocurrir si el usuario fue actualizado sin enviar el campo 'Name' (problema corregido en UsuariosSyncTasklet). "
									+
									"El nombre se restaurará automáticamente en la próxima sincronización si el usuario está en el archivo de empleados.",
							codigoEmpleado, autorizador.getId(), autorizador.getEmail());
				}
				return autorizador;
			}
			return null;
		} catch (Exception e) {
			log.error("Error al obtener autorizador por código {}: {}", codigoEmpleado, e.getMessage());
			return null;
		}
	}

	private static PermisoDTO defaultPermisos() {
		return new PermisoDTO(true, true, true, true, true, true);
	}

	private static LenguajeDTO lenguajeEs(String texto) {
		LenguajeDTO l = new LenguajeDTO();
		l.setCode("es");
		l.setText(texto);
		l.setDescription(texto);
		return l;
	}

	private static StepDTO buildStep(UsuarioDTO autorizador, String nombre) {
		StepDTO paso = new StepDTO();
		paso.setPermissions(defaultPermisos());
		paso.setSupervisorId(autorizador.getId());
		paso.setMaxValue(1);
		paso.setActiveAllAlerts(true);
		paso.setLanguages(List.of(lenguajeEs(nombre)));
		return paso;
	}

	/**
	 * Construye los steps de un workflow a partir de una lista ordenada de
	 * autorizadores.
	 * Cada autorizador se convierte en un step (etapa).
	 * El nombre de cada etapa se obtiene de NOMBRES_ETAPAS por posición;
	 * si hay más etapas que nombres definidos, se usa el nombre del workflow.
	 */
	private static List<StepDTO> buildSteps(List<UsuarioDTO> autorizadores, String nombre) {
		List<StepDTO> pasos = new ArrayList<>();
		for (int i = 0; i < autorizadores.size(); i++) {
			UsuarioDTO autorizador = autorizadores.get(i);
			if (autorizador != null) {
				String nombreEtapa = i < NOMBRES_ETAPAS.size() ? NOMBRES_ETAPAS.get(i) : nombre;
				pasos.add(buildStep(autorizador, nombreEtapa));
			}
		}
		return pasos;
	}

}
