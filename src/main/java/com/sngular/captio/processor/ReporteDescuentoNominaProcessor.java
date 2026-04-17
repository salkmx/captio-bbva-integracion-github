package com.sngular.captio.processor;

import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.DescuentoNominaDTO;
import com.sngular.captio.dto.ViajeDTO;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Component
public class ReporteDescuentoNominaProcessor implements ItemProcessor<List<ViajeDTO>, DescuentoNominaDTO> {

    @Override
    public DescuentoNominaDTO process(List<ViajeDTO> viajes) throws Exception {

        // Lógica de procesamiento para generar DescuentoNominaDTO a partir de ViajeDTO

        DescuentoNominaDTO descuentoNominaDTO = new DescuentoNominaDTO();
        Integer usuarioId = null;

        log.info("***********************ReporteDescuentoNominaProcessor***************************");
        log.info("process ReporteDescuentoNominaProcessor : Cantidad Viajes [" + viajes.size() + "]");

        for (ViajeDTO viaje : viajes) {
            //DescuentoNominaDTO dto = mapearCamposPersonalizados(viaje, descuentoNominaDTO);
            // Aquí podrías agregar lógica adicional para combinar los DTOs si es necesario

            usuarioId = viaje.getUser().getId();
            log.info("UserId: [" + usuarioId + "]");

        }


/*
        if (viaje.getCustomFields().isEmpty()) {
            return null;
        }

        List<CustomFieldDTO> customerFields = viaje.getCustomFields();

        customerFields.forEach(customer -> {
            if (Objects.equals(customer.getId(), CustomFieldsEnum.NOMBRE_INFORME.getIdCustomField())) {
                log.info("UserId: [" + usuarioId + "]");
                descuentoNominaDTO.setTravelStartDate(viaje.getStartDate());
                descuentoNominaDTO.setTravelEndDate(viaje.getEndDate());
                //filterReports += "'" + customer.getValue() + "','";
            }
        });
*/
        // Aquí se mapearían los campos necesarios desde item a descuentoNominaDTO
        return descuentoNominaDTO;
    }
}
