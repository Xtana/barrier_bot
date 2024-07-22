package ru.pobeda18.bot.funcs.num_processing;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CarNumberProcessing {

    private static final Map<String, String> LETTER_CONVERTER = new HashMap<>() {{
        put("А", "A");
        put("В", "B");
        put("Е", "E");
        put("К", "K");
        put("М", "M");
        put("Н", "H");
        put("О", "O");
        put("Р", "P");
        put("С", "C");
        put("Т", "T");
        put("У", "Y");
        put("Х", "X");
    }};

    public String getProcessedCarNumber(String textMessage) {
        String carNum = getCarNumFromMessage(textMessage);
        if (isCarNum(carNum)) {
            return getEnglishUpperCaseNumber(carNum);
        }
        return null;
    }

    private String getCarNumFromMessage(String textMessage) {
        return textMessage.replaceAll("[^0-9a-zA-ZА-я]", "");
    }

    private boolean isCarNum(String carNumber) {
        return carNumber.matches("([АВЕКМНОРСТУХавекмнорстухABEKMHOPCTYXabekmhopctyx]\\d{3}[АВЕКМНОРСТУХавекмнорстухABEKMHOPCTYXabekmhopctyx]{2}\\d{2,3})");
    }

    private String getEnglishUpperCaseNumber(String carNum) {
        carNum = carNum.toUpperCase();

        for (int i = 0; i < carNum.length(); i++) {
            char letter = carNum.charAt(i);
            if (LETTER_CONVERTER.containsKey(String.valueOf(letter))) {
                carNum = carNum.replace(letter, LETTER_CONVERTER.get(String.valueOf(letter)).charAt(0));
            }
        }
        return carNum;
    }
}
