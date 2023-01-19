package com.batch.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelService {

	public List<Map<String, Object>> read(InputStream stream) {

		Workbook workBook = getWorkBook(stream);

		final List<Map<String, Object>> list = new ArrayList<>();

		if (workBook.iterator().hasNext()) {
			IntStream.range(0, workBook.getNumberOfSheets()).parallel().filter(i -> workBook.getSheetAt(i) != null)
					.forEach(i -> {
						IntStream.range(1, workBook.getSheetAt(i).getPhysicalNumberOfRows()).parallel()
								.filter(j -> workBook.getSheetAt(i).getRow(j) != null).forEach(j -> {
									Row headerRow = workBook.getSheetAt(i).getRow(0);
									try {
										Map<String, Object> map = new HashMap<>();
										IntStream.range(0, workBook.getSheetAt(i).getPhysicalNumberOfRows()).parallel()
												.filter(k -> workBook.getSheetAt(i).getRow(j).getCell(k) != null)
												.forEach(k -> {
													
													Object value = null;
													switch (workBook.getSheetAt(i).getRow(j).getCell(k).getCellType()) {
													case BLANK:
														value = "";
														break;
													case BOOLEAN:
														value = workBook.getSheetAt(i).getRow(j).getCell(k)
																.getBooleanCellValue();
														break;
													case FORMULA:
														value = workBook.getSheetAt(i).getRow(j).getCell(k)
																.getCellFormula();
														break;
													case NUMERIC:
														value = workBook.getSheetAt(i).getRow(j).getCell(k)
																.getNumericCellValue();
														break;
													case STRING:
														value = workBook.getSheetAt(i).getRow(j).getCell(k)
																.getStringCellValue();
														break;
													case ERROR:
														break;
													case _NONE:
														break;
													default:
														break;
													}
													map.put(headerRow.getCell(k).getStringCellValue(), value);
												});
										list.add(map);
									} catch (IllegalArgumentException | SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								});
					});
		}
		return list;

	}

	private Workbook getWorkBook(InputStream stream) {
		Workbook workBook = null;
		try {
			workBook = new XSSFWorkbook(stream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workBook;

	}
}
