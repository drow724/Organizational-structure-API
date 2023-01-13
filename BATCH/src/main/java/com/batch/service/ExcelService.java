package com.batch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExcelService {

	public List<Map<String, Object>> read(MultipartFile file) {

		Workbook workBook = getWorkBook(file);

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

	private Workbook getWorkBook(MultipartFile file) {

		Workbook workBook = null;

		try {
			workBook = new XSSFWorkbook(file.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workBook;

	}
}
