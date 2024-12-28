package dev.kofe.kengine.service.impl;

import dev.kofe.kengine.dto.ResourceDTO;
import dev.kofe.kengine.dto.UiElementOneLanguageDTO;
import dev.kofe.kengine.model.*;
import dev.kofe.kengine.repository.*;
import dev.kofe.kengine.service.LanguageExcelService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static dev.kofe.kengine.constant.KEConstant.*;

@Service
@Transactional
public class LanguageExcelServiceImpl implements LanguageExcelService {

    private final LanguageRepository languageRepository;
    private final BigValueRepository bigValueRepository;
    private final DescriptorRepository descriptorRepository;
    private final UiShortElementRepository uiShortElementRepository;
    private final UiBigElementRepository uiBigElementRepository;
    private final DescriptorSetRepository descriptorSetRepository;

    public LanguageExcelServiceImpl (LanguageRepository languageRepository,
                                    BigValueRepository bigValueRepository,
                                    DescriptorRepository descriptorRepository,
                                    UiShortElementRepository uiShortElementRepository,
                                    UiBigElementRepository uiBigElementRepository,
                                    DescriptorSetRepository descriptorSetRepository

    ) {
        this.languageRepository = languageRepository;
        this.bigValueRepository = bigValueRepository;
        this.descriptorRepository = descriptorRepository;
        this.uiShortElementRepository = uiShortElementRepository;
        this.uiBigElementRepository = uiBigElementRepository;
        this.descriptorSetRepository = descriptorSetRepository;
    }

    private List<UiElementOneLanguageDTO> getAllUiShortElementsByLanguage(Long languageId) {
        List<Object[]> result = uiShortElementRepository.findUiElementShortValuesForLanguage(languageId);
        return result.stream()
                .map(row -> {
                    UiElementOneLanguageDTO uiElementOneLanguageDTO = new UiElementOneLanguageDTO();
                    uiElementOneLanguageDTO.setKey((Integer) row[0]);
                    uiElementOneLanguageDTO.setValue((String) row[1]);
                    return uiElementOneLanguageDTO;
                })
                .collect(Collectors.toList());
    }

    private List<UiElementOneLanguageDTO> getUiBigsForLanguage (Long languageId) {
        Language language = languageRepository.findById(languageId).orElse(null);
        if (language == null) return null;
        List<UiBigElement> uiBigElementList = uiBigElementRepository.findAll();
        List<UiElementOneLanguageDTO> uiElementOneLanguageDTOList = new ArrayList<>();
        for (UiBigElement uiBigElement : uiBigElementList) {
            List<Descriptor> descriptorList = uiBigElement.getValueSet().getDescriptors();
            for (Descriptor descriptor : descriptorList) {
                if (descriptor.getLanguage().getLanguageId().equals(languageId)) {
                    if (!descriptor.getIsBig()) return null; //@ToDo log, errors.... @ToDo exception!!!
                    UiElementOneLanguageDTO elementDTO = new UiElementOneLanguageDTO();
                    elementDTO.setIsBig(true);
                    elementDTO.setKey(uiBigElement.getKey());
                    //@ToDo can be NULL!!!!!
                    elementDTO.setValue(descriptor.getBigValue().getValue().replace("\n", "\r\n"));
                    uiElementOneLanguageDTOList.add(elementDTO);
                }
            }
        }

        return uiElementOneLanguageDTOList;
    }

    @Override
    public ResourceDTO exportToExcelByLanguage (Long languageId) {

        Language language = languageRepository.findById(languageId).orElse(null);
        String languageCode = "UNKNOWN_LANGUAGE_CODE";
        if (language != null) { languageCode = language.getLanguageCode(); }

        // SHORTS
        List<UiElementOneLanguageDTO> uiElementList_SHORTS = getAllUiShortElementsByLanguage (languageId);

        // BIGS
        List<UiElementOneLanguageDTO> uiElementList_BIGS = getUiBigsForLanguage(languageId);
        if (uiElementList_BIGS == null) uiElementList_BIGS = new ArrayList<>(); // @ToDo exception!!!

        Resource resource = prepareExcelTwoUiElementsSheets(uiElementList_SHORTS, uiElementList_BIGS, languageCode);

        return ResourceDTO.builder().resource(resource).
                mediaType(MediaType.parseMediaType
                        ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).build();

    }

    private Resource prepareExcelTwoUiElementsSheets ( List<UiElementOneLanguageDTO> uiElementList_SHORTS,
                                                       List<UiElementOneLanguageDTO> uiElementList_BIGS,
                                                       String languageCode) {

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet_SHORTS_uiElements  =  workbook.createSheet(EXCEL_SHEET_NAME_FOR_SHORTS_UI);
        Sheet sheet_BIGS_uiElements    =  workbook.createSheet(EXCEL_SHEET_NAME_FOR_BIGS_UI);

        prepareHeaders  (workbook,  sheet_SHORTS_uiElements, "LANGUAGE", "KEY-SHORTS", "VALUE");
        populateUserData(workbook,  sheet_SHORTS_uiElements, uiElementList_SHORTS, languageCode);

        prepareHeaders  (workbook,  sheet_BIGS_uiElements, "LANGUAGE", "KEY-BIGS", "VALUE");
        populateUserData(workbook,  sheet_BIGS_uiElements, uiElementList_BIGS, languageCode);

        // generate
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            return new ByteArrayResource(byteArrayOutputStream.toByteArray());

        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException ("Error - Excel file generation"); // @ToDo
        }

    }

    private void populateUserData(Workbook workbook, Sheet sheet,
                                  List<UiElementOneLanguageDTO> uiElementList,
                                  String languageCode) {

        int rowNo = 1;
        Font font=workbook.createFont();
        font.setFontName("Arial");

        CellStyle cellStyle=workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setWrapText(true); ///

        for (UiElementOneLanguageDTO element : uiElementList) {
            int columnNo=0;
            Row row=sheet.createRow(rowNo);

            // LANGUAGE
            populateCell(sheet, row, columnNo++, languageCode, cellStyle);

            // KEY
            populateCell(sheet, row, columnNo++, String.valueOf(element.getKey()), cellStyle);

            // VALUE
            populateCell(sheet, row, columnNo++, element.getValue(), cellStyle);

            rowNo++;
        }
    }

    private void populateCell(Sheet sheet, Row row, int columnNo, String value, CellStyle cellStyle ) {
        Cell cell=row.createCell(columnNo);
        cell.setCellStyle(cellStyle);
        cell.setCellValue(value);
        sheet.autoSizeColumn(columnNo);
    }

    private void prepareHeaders(Workbook workbook,
                                Sheet sheet, String... headers) {

        Row headerRow=sheet.createRow(0);
        Font font=workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

        int columnNo = 0;
        for(String header:headers){
            Cell headerCell = headerRow.createCell(columnNo++);
            headerCell.setCellValue(header);
            headerCell.setCellStyle(cellStyle);
        }
    }


    // **** IMPORT LANGUAGE ENGINE ****
    public void importLanguageViaExcel(MultipartFile inputFile)  {
        // load all languages
        List<Language> languageList = languageRepository.findAll();

        try {

            XSSFWorkbook workbook = new XSSFWorkbook(inputFile.getInputStream());

            //    FileInputStream is = new FileInputStream(new File("D:\\Users\\user2777005\\Desktop\\bob.xlsx"));
            //    XSSFWorkbook wb = new XSSFWorkbook(is);

            int numberOfSheet = workbook.getNumberOfSheets();
            //System.out.println("NUMBER OF SHEETS: " + numberOfSheet);

            for (int i = 0; i < numberOfSheet; i++) {
                // Getting the Sheet at index i
                Sheet sheet = workbook.getSheetAt(i);

                // Create a DataFormatter to format and get each cell's value as String
                DataFormatter dataFormatter = new DataFormatter();

                Iterator<Row> rowIterator = sheet.rowIterator();
                for (int rowCounter = 1; rowIterator.hasNext() ; rowCounter++) {
                    Row row = rowIterator.next();
                    String languageCode = "";
                    int key = -1;
                    String value = "";

                    // first row (1) is a header
                    if (rowCounter > 1) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        for (int cellCount = 1; cellIterator.hasNext() ; cellCount++) {
                            Cell cell = cellIterator.next();
                            String cellValue = dataFormatter.formatCellValue(cell);
                            if (cellCount == 1) {
                                //System.out.println("LANGUAGE: " + cellValue + "\t");
                                languageCode = cellValue;
                            }
                            if (cellCount == 2) {
                                //System.out.println("KEY: " + cellValue + "\t");
                                if (cellValue != null) {
                                    try {
                                        key = Integer.parseInt(cellValue);
                                    } catch (Exception e) { break; } //@ToDo log
                                }
                            }
                            if (cellCount == 3) {
                                //System.out.println("VALUE: " + cellValue + "\t");
                                value = cellValue;
                            }
                        }
                        // so, we have language code, key and value
                        // **** PROCESSING ****
                        // SHORT UIs CASE
                        if (sheet.getSheetName().equals(EXCEL_SHEET_NAME_FOR_SHORTS_UI)) {
                            UiShortElement uiShortElement = uiShortElementRepository.findByKey(key);
                            if (uiShortElement != null) {
                                // element has been found
                                List<Descriptor> descriptorList = uiShortElement.getValueSet().getDescriptors();
                                for (Descriptor descriptor : descriptorList) {
                                    if (descriptor.getLanguage().getLanguageCode().equals(languageCode)) {
                                        descriptor.setValue(value);
                                        descriptorRepository.save(descriptor);
                                        break;
                                    }
                                }
                            } else {
                                // important: need to create ui element firstly by hand in the admin panel
                            }
                        }
                        // BIG UIs CASE
                        if (sheet.getSheetName().equals(EXCEL_SHEET_NAME_FOR_BIGS_UI)) {
                            UiBigElement uiBigElement = uiBigElementRepository.findByKey(key);
                            if (uiBigElement != null) {
                                // element has been found
                                List<Descriptor> descriptorList = uiBigElement.getValueSet().getDescriptors();
                                for (Descriptor descriptor : descriptorList) {
                                    if (descriptor.getLanguage().getLanguageCode().equals(languageCode)) {
                                        BigValue bigValue = descriptor.getBigValue();
                                        bigValue.setValue(value);
                                        bigValueRepository.save(bigValue);
                                        //// first symbols to short value (to usability)
                                        descriptor.setValue(value.substring(0, Math.min(LENGTH_FIRST_SYMBOLS_FROM_BIG_VALUE_TO_USE_IN_SHORT_VALUE, value.length())));
                                        descriptorRepository.save(descriptor);
                                        break;
                                    }
                                }
                            } else {
                                // important: need to create ui element firstly by hand in the admin panel
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            /// LOG @ToDo
        }

    }

    // **** IMPORT INITIAL LANGUAGE ENGINE ****
    public void importLanguageViaExcel1(FileInputStream is)  {
        // load all languages
        // List<Language> languageList = languageRepository.findAll();
        Language initialLanguage = languageRepository.findByIsInitialIsTrue();
        if (initialLanguage == null) {
          //@ToDo LOG!
          return;
        }

        Map<Integer, String> shortElements = new HashMap<>();
        Map<Integer, String> bigElements = new HashMap<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            int numberOfSheet = workbook.getNumberOfSheets();
            for (int i = 0; i < numberOfSheet; i++) {
                // Getting the Sheet at index i
                Sheet sheet = workbook.getSheetAt(i);

                // Create a DataFormatter to format and get each cell's value as String
                DataFormatter dataFormatter = new DataFormatter();

                Iterator<Row> rowIterator = sheet.rowIterator();
                for (int rowCounter = 1; rowIterator.hasNext() ; rowCounter++) {
                    Row row = rowIterator.next();
                    String languageCode = "";
                    int key = -1;
                    String value = "";

                    // first row (1) is a header
                    if (rowCounter > 1) {
                        Iterator<Cell> cellIterator = row.cellIterator();
                        for (int cellCount = 1; cellIterator.hasNext() ; cellCount++) {
                            Cell cell = cellIterator.next();
                            String cellValue = dataFormatter.formatCellValue(cell);
                            if (cellCount == 1) {
                                //System.out.println("LANGUAGE: " + cellValue + "\t");
                                languageCode = cellValue;
                            }
                            if (cellCount == 2) {
                                //System.out.println("KEY: " + cellValue + "\t");
                                if (cellValue != null) {
                                    try {
                                        key = Integer.parseInt(cellValue);
                                    } catch (Exception e) { break; } //@ToDo log
                                }
                            }
                            if (cellCount == 3) {
                                //System.out.println("VALUE: " + cellValue + "\t");
                                value = cellValue;
                            }
                        }
                        // so, we have language code, key and value
                        // **** PROCESSING ****
                        // SHORT UIs CASE
                        if (sheet.getSheetName().equals(EXCEL_SHEET_NAME_FOR_SHORTS_UI)) {
                               shortElements.put(key, value);

                        }
                        // BIG UIs CASE
                        if (sheet.getSheetName().equals(EXCEL_SHEET_NAME_FOR_BIGS_UI)) {
                            bigElements.put(key, value);
                        }
                    }
                }
            }
        } catch (IOException e) {
            /// LOG @ToDo
        }

        //
        // ========== REGISTER ELEMENTS FROM BOTH MAPs (SHORTS AND BIGS)
        //

        // SHORTS UI ELEMENTS
        Map<Integer, String> sortedMapShorts = new TreeMap<>(shortElements);
        for (Map.Entry<Integer, String> entry : sortedMapShorts.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();

            UiShortElement uiShortElement = new UiShortElement();
            uiShortElementRepository.save(uiShortElement);

            DescriptorSet descriptorSet = new DescriptorSet();
            descriptorSetRepository.save(descriptorSet);

            uiShortElement.setValueSet(descriptorSet);
            uiShortElement.setKey(key);
            uiShortElementRepository.save(uiShortElement);

            Descriptor descriptor = new Descriptor();
            descriptor.setValue(value);
            descriptorRepository.save(descriptor);

            descriptor.setLanguage(initialLanguage);
            descriptor.setIsBig(false);
            descriptorSet.addDescriptor(descriptor);
            descriptorSetRepository.save(descriptorSet);
            descriptorRepository.save(descriptor);
        }

        // BIG UI ELEMENTS
        Map<Integer, String> sortedMapBigs = new TreeMap<>(bigElements);
        for (Map.Entry<Integer, String> entry : sortedMapBigs.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();

            UiBigElement uiBigElement = new UiBigElement();
            uiBigElementRepository.save(uiBigElement);

            DescriptorSet descriptorSet = new DescriptorSet();
            descriptorSetRepository.save(descriptorSet);

            uiBigElement.setValueSet(descriptorSet);
            uiBigElement.setKey(key);
            uiBigElementRepository.save(uiBigElement);

            Descriptor descriptor = new Descriptor();
            descriptor.setValue(value.substring(0, Math.min(LENGTH_FIRST_SYMBOLS_FROM_BIG_VALUE_TO_USE_IN_SHORT_VALUE, value.length())));
            descriptorRepository.save(descriptor);

            descriptor.setLanguage(initialLanguage);
            descriptor.setIsBig(true);
            descriptorSet.addDescriptor(descriptor);
            descriptorSetRepository.save(descriptorSet);
            descriptorRepository.save(descriptor);

            /// big value
            BigValue bigValue = new BigValue();
            bigValue.setValue(value);
            bigValueRepository.save(bigValue);

            descriptor.setBigValue(bigValue);
            descriptorRepository.save(descriptor);
        }


    }


}
