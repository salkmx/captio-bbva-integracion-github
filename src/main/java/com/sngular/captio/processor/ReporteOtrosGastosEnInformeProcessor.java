package com.sngular.captio.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.GastoDTO;
import com.sngular.captio.dto.InformeDTO;
import com.sngular.captio.dto.UsuarioDTO;
import com.sngular.captio.enums.NombreInformeEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.GastoService;
import com.sngular.captio.services.InformeService;
import com.sngular.captio.services.UsuarioService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ReporteOtrosGastosEnInformeProcessor implements ItemProcessor<InformeDTO, InformeDTO> {

	private final GastoService gastoService;

	private final Properties properties;

	private final UsuarioService usuarioService;

	private final InformeService informeService;

	@Override
	public InformeDTO process(InformeDTO item) throws Exception {
		if (item != null && Arrays.stream(NombreInformeEnum.values())
				.anyMatch(t -> item.getName().startsWith(t.getNombreInforme()))) {
			List<GastoDTO> gastos = gastoService.obtenerGastosPorFiltro("{\"Report_Id\":" + item.getId() + "}");

			//List<UsuarioDTO> usuarios = usuarioService.obtenerGrupos("{\"Id\":" + item.getUser().getId() + "}");
            List<UsuarioDTO> usuarios = usuarioService.obtenerUsuarioByFiltro("{\"Id\":" + item.getUser().getId() + "}");
            
            List<GastoDTO> gastosConUsuario = new ArrayList<GastoDTO>();
            
            if (usuarios != null && !usuarios.isEmpty()) {
                UsuarioDTO usuario = usuarios.get(0);
                item.setUser(usuario);

                for(GastoDTO gasto : gastos){
                    gasto.setUsuarioCorporativo(usuario.getUserOptions().getEmployeeCode());
					gasto.setNombreInforme(item.getName());
                    gastosConUsuario.add(gasto);
                }
            }

			item.setGastos(gastosConUsuario);
			informeService.actualizarInforme(item);

		}
		return item;
	}

}