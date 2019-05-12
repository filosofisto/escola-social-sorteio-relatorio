package br.gov.df.setrab.sorteio.jasperreport;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JasperReportEngine {

    public static final String CONTENT_TYPE_PDF = "application/pdf";

    private InputStream stream;
    private OutputStream outputStream;
    private JasperReport jasperReport;
    private JasperPrint jasperPrint;
    private Map<String, Object> parameters;
    private String contentType;

    public JasperReportEngine init(String reportName) throws JRException {
        stream = getClass().getResourceAsStream("/jasperreports/" + reportName + ".jrxml");
        jasperReport = JasperCompileManager.compileReport(stream);
        parameters = new HashMap<>();

        return this;
    }

    public JasperReportEngine addParameter(String name, Object value) throws JRException {
        if (parameters == null) {
            throw new JRException("parameters null");
        }

        parameters.put(name, value);

        return this;
    }

    public JasperReportEngine execute(List<?> data) throws JRException {
        if (stream == null) {
            throw new JRException("Stream null");
        }
        if (jasperReport == null) {
            throw new JRException("JasperReport nao inicializado");
        }
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JRBeanCollectionDataSource(data));

        return this;
    }

    public JasperReportEngine pdf() throws JRException {
        outputStream = new ByteArrayOutputStream();
        JRPdfExporter exporter = createExporter(outputStream);
        SimplePdfReportConfiguration reportConfiguration = createPdfReportConfiguration();
        SimplePdfExporterConfiguration exporterConfiguration = createExportConfiguration();
        setConfigurations(exporter, reportConfiguration, exporterConfiguration);
        exporter.exportReport();
        contentType = CONTENT_TYPE_PDF;

        return this;
    }

    public byte[] bytes() throws JRException {
        if (outputStream == null) {
            throw new JRException("outputStream null");
        }

        return ((ByteArrayOutputStream) outputStream).toByteArray();
    }

    public String base64Encoded() throws JRException {
        if (outputStream == null) {
            throw new JRException("outputStream null");
        }

        return Base64.getEncoder().encodeToString(bytes());
    }

    public String base64Encoded(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    private void setConfigurations(
            JRPdfExporter exporter,
            SimplePdfReportConfiguration reportConfiguration,
            SimplePdfExporterConfiguration exporterConfiguration) {
        exporter.setConfiguration(reportConfiguration);
        exporter.setConfiguration(exporterConfiguration);
    }

    public String getContentType() {
        return contentType;
    }

    private SimplePdfExporterConfiguration createExportConfiguration() {
        SimplePdfExporterConfiguration exporterConfiguration = new SimplePdfExporterConfiguration();
        exporterConfiguration.setMetadataAuthor("SGC");
        exporterConfiguration.setEncrypted(false);
        exporterConfiguration.setAllowedPermissionsHint("PRINTING");

        return exporterConfiguration;
    }

    private SimplePdfReportConfiguration createPdfReportConfiguration() {
        SimplePdfReportConfiguration reportConfiguration = new SimplePdfReportConfiguration();
        reportConfiguration.setSizePageToContent(true);
        reportConfiguration.setForceLineBreakPolicy(false);

        return reportConfiguration;
    }

    private JRPdfExporter createExporter(OutputStream outputStream) {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        return exporter;
    }
}
