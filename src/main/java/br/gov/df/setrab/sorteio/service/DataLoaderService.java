package br.gov.df.setrab.sorteio.service;

import br.gov.df.setrab.sorteio.Helper;
import br.gov.df.setrab.sorteio.model.Classificacao;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class DataLoaderService {

    private final static Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    private enum ClassificationCols {
        CAPACITACAO,
        CATEGORIA,
        CLASSIFICACAO,
        NOME,
        CPF
    }

    private static final String REPORT_FILE_NAME = "report.txt";
    private static final int CLASSIFIEDS_SHEET = 0;
    private static final int RESERVE_REGISTER_SHEET = 1;

    public List<Classificacao> importData() throws IOException {
        List<Classificacao> list = new ArrayList<>();

        FileInputStream input = new FileInputStream(
                new File(Helper.curdir() + File.separator + "report.txt")
        );
        CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
        decoder.onMalformedInput(CodingErrorAction.IGNORE);
        InputStreamReader reader = new InputStreamReader(input, decoder);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            list.add(new Classificacao(line));
        }

        bufferedReader.close();

        return list;
    }

    public Pair<List<Classificacao>, List<Classificacao>> importDataClassifiedsAndReserveRegisters(String filename)
            throws IOException {
        List<Classificacao> classifiedList = new ArrayList<>();
        List<Classificacao> reserveRegisterList = new ArrayList<>();
        Pair<List<Classificacao>, List<Classificacao>> ret = new Pair<>(classifiedList, reserveRegisterList);

        FileInputStream inputStream = new FileInputStream(new File(filename));

        Workbook workbook = new XSSFWorkbook(inputStream);

        Sheet classifiedSheet = workbook.getSheetAt(CLASSIFIEDS_SHEET);
        createClassificationList(classifiedList, classifiedSheet);

        Sheet reserveRegisterSheet = workbook.getSheetAt(RESERVE_REGISTER_SHEET);
        createClassificationList(reserveRegisterList, reserveRegisterSheet);

        workbook.close();
        inputStream.close();

        return ret;
    }

    private void createClassificationList(List<Classificacao> classifiedList, Sheet sheet) {
        Iterator<Row> iterator = sheet.iterator();
        if (iterator.hasNext()) {
            iterator.next(); // jump first row -> header
        }

        Classificacao classificacao;
        int col;

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            classificacao = new Classificacao();
            classificacao.setSanitizeName(false);

            col = -1;

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                setClassificationByCol(classificacao, cell, ++col);
            }

            classifiedList.add(classificacao);
        }
    }

    private void setClassificationByCol(Classificacao classificacao, Cell cell, int col) {
        if (col >= ClassificationCols.values().length) {
            return; //ignore
        }

        ClassificationCols classificationCol = ClassificationCols.values()[col];

        switch (classificationCol) {
            case CAPACITACAO:
                classificacao.setCapacitacao(cell.getStringCellValue());
                break;
            case CATEGORIA:
                classificacao.setCategoria(cell.getStringCellValue());
                break;
            case CLASSIFICACAO:
                classificacao.setClassificacao(new Double(cell.getNumericCellValue()).intValue());
                break;
            case NOME:
                classificacao.setNome(cell.getStringCellValue());
                break;
            case CPF:
                classificacao.setCpf(cell.getStringCellValue());
                break;
        }
    }

    private String reportDataFile() {
        return Helper.curdir() + File.separator + REPORT_FILE_NAME;
    }

}
