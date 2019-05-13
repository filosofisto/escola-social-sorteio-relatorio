package br.gov.df.setrab.sorteio;

import br.gov.df.setrab.sorteio.model.Classificacao;
import br.gov.df.setrab.sorteio.service.DataLoaderService;
import br.gov.df.setrab.sorteio.service.ReportService;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final DataLoaderService dataLoaderService;

    private final ReportService reportService;

    @Autowired
    public Main(DataLoaderService dataLoaderService, ReportService reportService) {
        this.dataLoaderService = dataLoaderService;
        this.reportService = reportService;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @Override
    public void run(String... args) {
        try {
            List<Classificacao> classificacaoList = dataLoaderService.importData();

            byte[] bytesPDF = reportService.generatePDF(classificacaoList);
            reportService.savePDF(bytesPDF);

            XSSFWorkbook xssfWorkbook = reportService.generateExcel(classificacaoList);
            reportService.saveExcel(xssfWorkbook);
        } catch (IOException | JRException e) {
            e.printStackTrace();
        }
    }

    private void printResultDataImport(List<Classificacao> classificacaoList) {
        classificacaoList.forEach(Classificacao::print);
    }
}
