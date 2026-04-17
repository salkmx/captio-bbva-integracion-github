package com.sngular.captio.writer;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.sngular.captio.dto.InformeLogDTO;
import com.sngular.captio.dto.LogDetailDTO;
import com.sngular.captio.enums.EventIdEnum;
import com.sngular.captio.enums.SourceIdEnum;
import com.sngular.captio.properties.Properties;
import com.sngular.captio.services.InformeCaptioService;
import com.sngular.captio.services.UsuarioCaptioService;
import com.sngular.captio.services.WorkflowStepService;
import com.sngular.captio.util.DateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InformeLogWriter implements ItemWriter<InformeLogDTO> {

    private final Properties properties;
    private final InformeCaptioService informeCaptioService;
    private final UsuarioCaptioService usuarioCaptioService;
    private final WorkflowStepService workflowStepService;

    @Override
    public void write(Chunk<? extends InformeLogDTO> chunk) throws Exception {
        Path path = Paths.get(properties.getRutaArchivoLogs() + DateUtils.obtenerFechaActual() + "_InformeLogs" + ".txt");

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Report.Id|Report.ExternalId|Report.Name|Report.Status|Report.StatusDate|Log.Id|Log.UserId|Log.EmployeeCode|Log.StatusId|Log.StatusDate|Log.Comments|Log.StepId|Log.ExpenseId|Log.DelegantUserId|Log.EventId|Log.SourceId");
        sb.append(System.lineSeparator());
        for (InformeLogDTO item : chunk) {
            if (item.getLogs() == null) {
                continue;
            }

            for (LogDetailDTO logDetail : item.getLogs()) {
                sb.append(String.format("%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                        item.getId(),
                        item.getExternalId(),
                        replaceReportName(item.getId()), // ReportName
                        EventIdEnum.getDescriptionById(item.getStatus()),
                        item.getStatusDate(),
                        logDetail.getId(),
                        logDetail.getUserId(),
                        replaceLogUser(logDetail.getUserId()), // EmployeeCode
                        SourceIdEnum.getDescriptionById(logDetail.getStatusId()),
                        logDetail.getStatusDate(),
                        logDetail.getComments() != null ?
                                logDetail.getComments().replace("\n", " ").replace("\r", " ") : null,
                        replaceWorkflowStep(logDetail.getStepId()), //StepId
                        logDetail.getExpenseId(),
                        logDetail.getDelegantUserId(),
                        EventIdEnum.getDescriptionById(logDetail.getEventId()),
                        SourceIdEnum.getDescriptionById(logDetail.getSourceId())));
                sb.append(System.lineSeparator());
            }

        }

        Files.writeString(path, sb.toString(), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private String replaceReportName(Integer itemId){
        if(itemId == null) return "";
        var report = informeCaptioService.findOneById(itemId.longValue());
        if (report.isEmpty()) {
            log.warn("[{}] Reporte no encontrado con ID: {}", this.getClass().getSimpleName(), itemId);
        }
        return report.isPresent() ? report.get().getName() : String.valueOf(itemId);
    }

    private String replaceLogUser(Integer userId) {
        if(userId == null) return "";
        var user = usuarioCaptioService.findOneById(userId.longValue());
        if (user.isEmpty()) {
            log.warn("[{}] Usuario no encontrado con ID: {}", this.getClass().getSimpleName(), userId);
        }
        return user.isPresent() ? user.get().getEmployeeCode() : String.valueOf(userId);
    }

    private String replaceWorkflowStep(Integer stepId) {
        if(stepId == null) return "";
        var step = workflowStepService.findOneByStepId(stepId.longValue());
        if (step.isEmpty()) {
            log.warn("[{}] Workflow Step no encontrado con ID: {}", this.getClass().getSimpleName(), stepId);
        }

        return step.isPresent() ? step.get().getStepName() : String.valueOf(stepId);
    }



}