package com.sngular.captio.layout;

import java.util.List;

import org.springframework.batch.item.file.transform.Range;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "bbva.archivo.usuarios")
@Data
public class LayoutArchivoProperties {

	private List<String> columnas;
	private List<String> longitudes;

	public Range[] getRangos() {
		return longitudes.stream().map(rango -> {
			String[] partes = rango.split("-");
			return new Range(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
		}).toArray(Range[]::new);
	}
}
