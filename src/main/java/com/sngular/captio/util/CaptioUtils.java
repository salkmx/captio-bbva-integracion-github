package com.sngular.captio.util;

import java.text.Normalizer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.sngular.captio.dto.CustomFieldDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.dto.ViajeDTO;
import com.sngular.captio.enums.CustomFieldsEnum;
import com.sngular.captio.model.UsuarioSonar;

public class CaptioUtils {

	private CaptioUtils() {

	}

	public static String obtenerNombreWorkFlow(UsuarioSonar usuarioSonar, String tipo) {
		return usuarioSonar.getName() + tipo;
	}

	public static String obtenerNombreWorkFlow(UsuarioDTO usuarioSonar, String tipo) {
		return usuarioSonar.getName() + tipo;
	}

	public static String obtenerNombreWorkFlowNacional(UsuarioDTO usuarioSonar, String tipo) {
		return "Nacional " + usuarioSonar.getName() + tipo;
	}

	public static String obtenerNombreWorkFlowExtranjero(UsuarioDTO usuarioSonar, String tipo) {
		return "Extranjero " + usuarioSonar.getName() + tipo;
	}

	public static boolean contieneFormacion(String s) {
		if (s == null)
			return false;
		String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
		return Constantes.FORMACION.matcher(t).find();
	}

	public static boolean contieneCapacitacion(String s) {
		if (s == null)
			return false;
		String t = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
		return Constantes.CAPACITACION.matcher(t).find();
	}

	public static boolean contieneNacionalPalabra(ViajeDTO item) {
		List<CustomFieldDTO> cfs = Optional.ofNullable(item.getCustomFields()).orElseGet(Collections::emptyList);

		return cfs.stream().map(CustomFieldDTO::getValue).filter(Objects::nonNull)
				.map(v -> Normalizer.normalize(v, Normalizer.Form.NFD).replaceAll("\\p{M}+", ""))
				.anyMatch(v -> Constantes.P_NACIONAL.matcher(v).find());
	}

	public static CustomFieldDTO obtieneTipoViaje(ViajeDTO item) {
		return item.getCustomFields().stream()
				.filter(cf -> CustomFieldsEnum.DESTINO_VIAJE.getIdCustomField().equals(cf.getId())).findFirst()
				.orElse(null);
	}

}
