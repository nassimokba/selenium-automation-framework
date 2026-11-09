package report.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Getter
public class ReportConfig {

    private String reportType;
    private String reportName;
    @Setter
    private Path reportPath;
    private Path reportArtifactsPath;

    public ReportConfig(@Value("${report.type}") String reportType,
                        @Value("${report.name}") String reportName,
                        @Value("${report.output.path}") String reportOutputPath,
                        @Value("${report.artifacts.path}") String reportArtifactsPath) {
        this.reportType = reportType;
        this.reportName = reportName;
        this.reportPath = Path.of(reportOutputPath);
        this.reportArtifactsPath = Path.of(reportArtifactsPath);
    }
}
