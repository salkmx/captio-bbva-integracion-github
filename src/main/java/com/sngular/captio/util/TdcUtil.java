package com.sngular.captio.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TdcUtil {

    /**
     * Valida el formato de la TDC (Tarjeta de Crédito) y su estatus.
     *
     * @param tdc       Valor de la tarjeta a validar (16 dígitos numéricos)
     * @param tdcStatus Estatus de la tarjeta ("ACT" = activa, "BAJ" = inactiva)
     * @param userId    ID del usuario (para logging)
     * @return true si la TDC es válida (16 dígitos numéricos y estatus ACT), false
     *         si es null/vacía, inválida, o no está activa
     */
    public static boolean validarTdc(String tdc, String tdcStatus, Integer userId) {
        // Si es null o vacío, simplemente ignoramos (usuario sin tarjeta)
        if (tdc == null || tdc.isBlank()) {
            log.debug("Usuario {} no tiene TDC asignada, se omite sincronización de método de pago", userId);
            return false;
        }

        String tdcLimpia = tdc.trim();

        // Si TDC tiene sufijo ACT/BAJ pegado, limpiarlo (tomar solo primeros 16
        // caracteres)
        if (tdcLimpia.length() > 16 && (tdcLimpia.endsWith("ACT") || tdcLimpia.endsWith("BAJ"))) {
            log.warn("Usuario {}: TDC viene con estatus pegado '{}', se limpia a 16 dígitos", userId, tdcLimpia);
            tdcLimpia = tdcLimpia.substring(0, 16);
        }

        // Validar que sean exactamente 16 caracteres numéricos
        if (!tdcLimpia.matches("^\\d{16}$")) {
            String errorMsg = String.format(
                    "TDC inválida para usuario %d: '%s' - Debe contener exactamente 16 dígitos numéricos (tiene %d caracteres)",
                    userId, tdcLimpia, tdcLimpia.length());
            log.error(errorMsg);
            // Registrar error para notificación
            try {
                String jsonError = String.format(
                        "[{\"result\":null,\"key\":\"UserId\",\"value\":\"%d\",\"validations\":[{\"message\":\"%s\",\"code\":\"TDC_INVALID_FORMAT\",\"key\":\"TDC\",\"value\":\"%s\"}],\"status\":1}]",
                        userId, errorMsg.replace("\"", "'"), tdcLimpia);
                //TODO
//                message(jsonError);
            } catch (Exception e) {
                log.warn("No se pudo enviar alerta de error para TDC inválida: {}", e.getMessage());
            }
            return false;
        }

        // Validar estatus: solo procesar tarjetas ACTIVAS
        if (tdcStatus == null || tdcStatus.isBlank()) {
            log.warn("Usuario {}: TDC '{}' no tiene estatus definido, se omite sincronización", userId, tdcLimpia);
            return false;
        }

        String estatusLimpio = tdcStatus.trim().toUpperCase();
        if (!"ACT".equals(estatusLimpio)) {
            if ("BAJ".equals(estatusLimpio)) {
                log.info("Usuario {}: TDC '{}' está INACTIVA (BAJ), se omite sincronización de método de pago", userId,
                        tdcLimpia);
            } else {
                log.warn("Usuario {}: TDC '{}' tiene estatus desconocido '{}', se omite sincronización", userId,
                        tdcLimpia, estatusLimpio);
            }
            return false;
        }

        log.debug("Usuario {}: TDC '{}' validada correctamente con estatus ACTIVO", userId, tdcLimpia);
        return true;
    }

    /**
     * Compara una TDC completa (16 dígitos) contra el valor almacenado en Captio,
     * que puede estar enmascarado con formato "XXXXXX|YYYY" (primeros 6 + últimos
     * 4).
     * @param tdcCompleta
     * @param value
     * @return true si representan la misma tarjeta.
     */
    public static boolean tdcCoincide(String tdcCompleta, String value) {
        if (tdcCompleta == null || value == null) return false;

        // Comparación directa primero
        if (tdcCompleta.equals(value)) return true;

        // Captio enmascara como "123123|4302" (primeros 6 + pipe + últimos 4)
        if (value.contains("|") && tdcCompleta.length() >= 10) {
            String[] partes = value.split("\\|", 2);
            if (partes.length == 2) {
                String first6 = partes[0];
                String last4 = partes[1];
                if (tdcCompleta.startsWith(first6) && tdcCompleta.endsWith(last4)) {
                    log.debug("TDC coincide por formato enmascarado: {}...{} = {}", first6, last4, value);
                    return true;
                }
            }
        }

        return false;
    }



}
