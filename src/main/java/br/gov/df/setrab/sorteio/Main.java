package br.gov.df.setrab.sorteio;

import br.gov.df.setrab.sorteio.model.Classificacao;
import br.gov.df.setrab.sorteio.service.DataLoaderService;
import br.gov.df.setrab.sorteio.service.ReportService;
import javafx.util.Pair;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.List;

@SpringBootApplication
public class Main implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

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
        if (args.length == 0) {
            normalMode();
        } else {
            if (args.length == 2 && "-cr".equals(args[0])) {
                modeCadastroReserva(args[1]);
            }
        }
    }

    private void modeCadastroReserva(String filename) {
        if (!Helper.fileExists(filename)) {
            logger.error("Planilha {} nao foi encontrada, verifique se o caminha esta correto", filename);
            System.exit(-1);
        }

        try {
            Pair<List<Classificacao>, List<Classificacao>> pair =
                    dataLoaderService.importDataClassifiedsAndReserveRegisters(filename);

            byte[] bytesPDFClassificados = reportService.generatePDF(pair.getKey());
            reportService.savePDF("classificados.pdf", bytesPDFClassificados);

            byte[] bytesPDFCadastroReserva = reportService.generatePDF(pair.getValue());
            reportService.savePDF("cadastroReserva.pdf", bytesPDFCadastroReserva);
        } catch (IOException | JRException e) {
            e.printStackTrace();
        }
    }

    private void normalMode() {
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
