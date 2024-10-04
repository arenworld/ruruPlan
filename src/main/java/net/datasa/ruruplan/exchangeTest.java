package net.datasa.ruruplan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class exchangeTest {
    public static String exchangeValue() throws IOException {
        String firstPythonScriptPath = "src/test/python/exchange.py";
        String pythonInterpreter = "python";

        ProcessBuilder processBuilder = new ProcessBuilder(pythonInterpreter, firstPythonScriptPath);
        Process process = processBuilder.start();
        // 파이썬에서 출력된 값 읽어오는 객체 생성
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        line = reader.readLine();
        if(line == null){
            line = "0";
        }

        return line;
    }
}
