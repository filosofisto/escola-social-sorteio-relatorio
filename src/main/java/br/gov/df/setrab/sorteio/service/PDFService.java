package br.gov.df.setrab.sorteio.service;

import br.gov.df.setrab.sorteio.Helper;
import br.gov.df.setrab.sorteio.jasperreport.JasperReportEngine;
import br.gov.df.setrab.sorteio.model.Classificacao;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class PDFService {

    private final JasperReportEngine jasperReportEngine;

    @Autowired
    public PDFService(JasperReportEngine jasperReportEngine) {
        this.jasperReportEngine = jasperReportEngine;
    }

    public byte[] generatePDF(List<Classificacao> classificacaoList) throws JRException {
        return jasperReportEngine
                .init("SorteioResultado")
                .execute(classificacaoList)
                .pdf()
                .bytes();
    }

    public String savePDF(byte[] bytes) throws IOException {
        String pdfFilename = Helper.curdir()+File.separator+"report.pdf";
        FileOutputStream out = new FileOutputStream(new File(pdfFilename));
        out.write(bytes);
        out.close();

        return pdfFilename;
    }

    public void openPDF(String pdfFilename) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(pdfFilename);
        builder.redirectErrorStream(false);
        Process process = builder.start();
    }
}
