package br.gov.df.setrab.sorteio.service;

import br.gov.df.setrab.sorteio.Helper;
import br.gov.df.setrab.sorteio.model.Classificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataLoaderService {

    private final static Logger logger = LoggerFactory.getLogger(DataLoaderService.class);

    private static final String REPORT_FILE_NAME = "report.txt";

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

    private String reportDataFile() {
        return Helper.curdir() + File.separator + REPORT_FILE_NAME;
    }

}
