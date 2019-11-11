package google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.IOException;

public class GoogleSheetsService {
    private Sheets sheetsService;

    public GoogleSheetsService(Sheets sheetsService) {
        this.sheetsService = sheetsService;
    }

    public ValueRange getRange(String sheetId, String range) throws IOException {
        Spreadsheet sheet = sheetsService.spreadsheets().get(sheetId).execute();
        ValueRange readResult = sheetsService.spreadsheets().values()
                .get(sheetId, range).execute();
        readResult.getValues().forEach( v-> System.out.println(v.get(0)));
        return readResult;
    }

}