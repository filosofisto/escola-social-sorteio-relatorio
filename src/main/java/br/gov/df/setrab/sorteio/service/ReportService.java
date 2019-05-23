package br.gov.df.setrab.sorteio.service;

import br.gov.df.setrab.sorteio.Helper;
import br.gov.df.setrab.sorteio.jasperreport.JasperReportEngine;
import br.gov.df.setrab.sorteio.model.Classificacao;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class ReportService {

    private final JasperReportEngine jasperReportEngine;

    @Autowired
    public ReportService(JasperReportEngine jasperReportEngine) {
        this.jasperReportEngine = jasperReportEngine;
    }

    public byte[] generatePDF(List<Classificacao> classificacaoList) throws JRException {
        return jasperReportEngine
                .init("SorteioResultado")
                .execute(classificacaoList)
                .pdf()
                .bytes();
    }

    public XSSFWorkbook generateExcel(List<Classificacao> classificacaoList) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Resultado do Sorteio");
        int rowNum = 0;

        Row rowHeader = sheet.createRow(rowNum);

        rowHeader.createCell(0).setCellValue("Capacitação");
        rowHeader.createCell(1).setCellValue("Categoria");
        rowHeader.createCell(2).setCellValue("Classificação");
        rowHeader.createCell(3).setCellValue("Nome");
        rowHeader.createCell(4).setCellValue("CPF");

        for (Classificacao classificacao: classificacaoList) {
            Row row = sheet.createRow(++rowNum);

            row.createCell(0).setCellValue(classificacao.getCapacitacao());
            row.createCell(1).setCellValue(classificacao.getCategoria());
            row.createCell(2).setCellValue(classificacao.getClassificacao());
            row.createCell(3).setCellValue(classificacao.getNome());
            row.createCell(4).setCellValue(classificacao.getCpf());
        }

        return workbook;
    }

    public String savePDF(byte[] bytes) throws IOException {
        String pdfFilename = Helper.curdir()+File.separator+"report.pdf";
        return saveBytes(pdfFilename, bytes);
    }

    public String savePDF(String filename, byte[] bytes) throws IOException {
        String pdfFilename = Helper.curdir()+File.separator+filename;
        return saveBytes(pdfFilename, bytes);
    }

    public String saveExcel(XSSFWorkbook workbook) throws IOException {
        String filename = Helper.curdir()+File.separator+"report.xlsx";

        FileOutputStream outputStream = new FileOutputStream(filename);
        workbook.write(outputStream);
        workbook.close();

        return filename;
    }

    /*public String saveExcel(byte[] bytes) throws IOException {
        String pdfFilename = Helper.curdir()+File.separator+"report.xls";
        return saveBytes(pdfFilename, bytes);
    }*/

    private String saveBytes(String filename, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(filename));
        out.write(bytes);
        out.close();

        return filename;
    }

    /*public void openPDF(String pdfFilename) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(pdfFilename);
        builder.redirectErrorStream(false);
        Process process = builder.start();
    }*/
}
