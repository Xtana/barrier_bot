package ru.pobeda18.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSheetData {
    private final String number;
    private final String apartmentNumber;
    private final String phoneNumber;
}
