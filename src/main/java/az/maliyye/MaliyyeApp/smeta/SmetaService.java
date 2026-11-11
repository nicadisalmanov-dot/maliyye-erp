package az.maliyye.MaliyyeApp.smeta;

import az.maliyye.MaliyyeApp.layiheler.Layihe;
import az.maliyye.MaliyyeApp.layiheler.LayiheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmetaService {

    @Autowired private SmetaRepository smetaRepository;
    @Autowired private SmetaBendiRepository smetaBendiRepository;
    @Autowired private SmetaBasliqRepository smetaBasliqRepository;
    @Autowired private SmetaAltBasliqRepository smetaAltBasliqRepository;
    @Autowired private BudceRepository budceRepository;
    @Autowired private LayiheRepository layiheRepository;
    @Autowired private FaktikiXercRepository faktikiXercRepository;

    public List<Smeta> butunSmetalar() {
        return smetaRepository.findAll();
    }

    // Köhnə metod (tarixsiz) - bütün xərcləri götürür
    public Smeta smetaTap(Long id) {
        return smetaTap(id, null);
    }

    // YENİ metod (tarixli) - həmin tarixə qədər olan xərcləri götürür
    public Smeta smetaTap(Long id, LocalDateTime tarix) {
        Smeta smeta = smetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Smeta tapılmadı: ID " + id));

        if (smeta.getBəndlər() != null) {
            for (SmetaBendi b : smeta.getBəndlər()) {
                Double faktiki;
                if (tarix != null) {
                    faktiki = faktikiXercRepository.getCemiFaktikiXercByBendiIdVeTarix(b.getId(), tarix);
                } else {
                    faktiki = faktikiXercRepository.getCemiFaktikiXercByBendiId(b.getId());
                }
                b.setFaktikiXerc(faktiki != null ? faktiki : 0.0);
            }
        }
        return smeta;
    }

    // Köməkçi metod: Təsdiqlənmiş büdcəni yeniləyir
    private void budceYenile(Smeta smeta) {
        if (smeta != null && "TESDIQLENMIS".equals(smeta.getStatus()) && smeta.getBudce() != null) {
            Budce budce = smeta.getBudce();
            budce.hesablaVeYenile();
            budceRepository.save(budce);
        }
    }

    @Transactional
    public void smetaYarat(Long layiheId, String ad, String versiya, String valyuta, String istifadeci) {
        Layihe layihe = layiheRepository.findById(layiheId).orElseThrow(() -> new RuntimeException("Layihə tapılmadı!"));
        Smeta smeta = new Smeta();
        smeta.setLayihe(layihe);
        smeta.setAd(ad);
        smeta.setVersiya(versiya != null && !versiya.isEmpty() ? versiya : "v1.0");
        smeta.setValyuta(valyuta != null && !valyuta.isEmpty() ? valyuta : "AZN");
        smeta.setStatus("QARALAMA");
        smeta.setYaradanIstifadeci(istifadeci);
        smeta.setYaradilmaTarixi(LocalDateTime.now());
        smetaRepository.save(smeta);
    }

    // --- BAŞLIQ (Bölmə) ---
    @Transactional
    public SmetaBasliq basliqElaveEt(Long smetaId, String kod, String ad) {
        Smeta smeta = smetaTap(smetaId);
        SmetaBasliq basliq = new SmetaBasliq();
        basliq.setKod(kod);
        basliq.setAd(ad);
        basliq.setSmeta(smeta);

        smeta.setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(smeta);

        return smetaBasliqRepository.save(basliq);
    }

    @Transactional
    public void basliqRedakteEt(Long basliqId, String kod, String ad) {
        SmetaBasliq basliq = smetaBasliqRepository.findById(basliqId).orElseThrow();
        basliq.setKod(kod);
        basliq.setAd(ad);

        basliq.getSmeta().setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(basliq.getSmeta());

        smetaBasliqRepository.save(basliq);
    }

    @Transactional
    public void basliqSil(Long basliqId) {
        SmetaBasliq basliq = smetaBasliqRepository.findById(basliqId).orElse(null);
        if (basliq != null) {
            Smeta smeta = basliq.getSmeta();
            smetaBasliqRepository.deleteById(basliqId);

            smeta.setDuzelisTarixi(LocalDateTime.now());
            smetaRepository.save(smeta);
        }
    }

    // --- ALT BAŞLIQ (İş qrupu) ---
    @Transactional
    public SmetaAltBasliq altBasliqElaveEt(Long basliqId, String kod, String ad) {
        SmetaBasliq basliq = smetaBasliqRepository.findById(basliqId).orElseThrow();
        SmetaAltBasliq alt = new SmetaAltBasliq();
        alt.setKod(kod);
        alt.setAd(ad);
        alt.setBasliq(basliq);

        basliq.getSmeta().setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(basliq.getSmeta());

        return smetaAltBasliqRepository.save(alt);
    }

    @Transactional
    public void altBasliqRedakteEt(Long altBasliqId, String kod, String ad) {
        SmetaAltBasliq alt = smetaAltBasliqRepository.findById(altBasliqId).orElseThrow();
        alt.setKod(kod);
        alt.setAd(ad);

        alt.getBasliq().getSmeta().setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(alt.getBasliq().getSmeta());

        smetaAltBasliqRepository.save(alt);
    }

    @Transactional
    public void altBasliqSil(Long altBasliqId) {
        SmetaAltBasliq alt = smetaAltBasliqRepository.findById(altBasliqId).orElse(null);
        if (alt != null) {
            Smeta smeta = alt.getBasliq().getSmeta();
            smetaAltBasliqRepository.deleteById(altBasliqId);

            smeta.setDuzelisTarixi(LocalDateTime.now());
            smetaRepository.save(smeta);
        }
    }

    // --- BƏND (İş sətri) ---
    @Transactional
    public void bendElaveEt(Long smetaId, Long altBasliqId, String kod, String isAdi, String vahid, Double miqdar, Double vahidQiymeti, String kateqoriya) {
        Smeta smeta = smetaTap(smetaId);
        SmetaBendi bendi = new SmetaBendi();
        bendi.setSmeta(smeta);
        bendi.setKod(kod);
        if (altBasliqId != null) {
            SmetaAltBasliq alt = smetaAltBasliqRepository.findById(altBasliqId).orElse(null);
            bendi.setAltBasliq(alt);
        }
        bendi.setIsAdi(isAdi);
        bendi.setVahid(vahid);
        bendi.setMiqdar(miqdar != null ? miqdar : 0.0);
        bendi.setVahidQiymeti(vahidQiymeti != null ? vahidQiymeti : 0.0);
        bendi.setKateqoriya(kateqoriya != null ? kateqoriya : "MATERIAL");
        smetaBendiRepository.save(bendi);

        smeta.setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(smeta);

        budceYenile(smeta);
    }

    @Transactional
    public void bendSil(Long bendiId) {
        if (faktikiXercRepository.existsBySmetaBendiId(bendiId)) {
            throw new RuntimeException("Bu bəndə aid faktiki xərc mövcuddur! Silmək üçün əvvəlcə xərcləri ləğv edin.");
        }

        SmetaBendi bendi = smetaBendiRepository.findById(bendiId).orElse(null);
        if (bendi != null) {
            Smeta smeta = bendi.getSmeta();
            if (smeta != null && smeta.getBəndlər() != null) {
                smeta.getBəndlər().remove(bendi);
                smeta.setDuzelisTarixi(LocalDateTime.now());
                smetaRepository.save(smeta);
            }
            smetaBendiRepository.delete(bendi);
            if (smeta != null) {
                budceYenile(smeta);
            }
        }
    }

    public SmetaBendi bendTap(Long bendiId) {
        return smetaBendiRepository.findById(bendiId)
                .orElseThrow(() -> new RuntimeException("Smeta bəndi tapılmadı! ID: " + bendiId));
    }

    @Transactional
    public void bendRedakteEt(Long bendiId, String kod, String isAdi, String vahid, Double miqdar, Double vahidQiymeti, String kateqoriya) {
        SmetaBendi bendi = bendTap(bendiId);
        bendi.setKod(kod);
        bendi.setIsAdi(isAdi);
        bendi.setVahid(vahid);
        bendi.setMiqdar(miqdar != null ? miqdar : 0.0);
        bendi.setVahidQiymeti(vahidQiymeti != null ? vahidQiymeti : 0.0);
        bendi.setKateqoriya(kateqoriya != null ? kateqoriya : "MATERIAL");
        smetaBendiRepository.save(bendi);

        Smeta smeta = bendi.getSmeta();
        smeta.setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(smeta);

        budceYenile(smeta);
    }

    @Transactional
    public void smetaSil(Long smetaId) {
        Smeta smeta = smetaTap(smetaId);

        if (smeta.getBəndlər() != null) {
            smetaBendiRepository.deleteAll(smeta.getBəndlər());
        }

        if (smeta.getBasliqlar() != null) {
            for (SmetaBasliq basliq : smeta.getBasliqlar()) {
                if (basliq.getAltBasliqlar() != null) {
                    for (SmetaAltBasliq alt : basliq.getAltBasliqlar()) {
                        if (alt.getBəndlər() != null) {
                            smetaBendiRepository.deleteAll(alt.getBəndlər());
                        }
                        smetaAltBasliqRepository.delete(alt);
                    }
                }
                smetaBasliqRepository.delete(basliq);
            }
        }
        smetaRepository.delete(smeta);
    }

    // --- TƏSDİQLƏMƏ ---
    @Transactional
    public void budceyeCevirVeTesdiqle(Long smetaId, Double gozlenilmezFaiz, Double dolayiFaiz, String istifadeci) {
        Smeta smeta = smetaTap(smetaId);
        Budce budce = smeta.getBudce();
        if (budce == null) {
            budce = new Budce();
            budce.setSmeta(smeta);
        }
        budce.setGozlenilmezFaiz(gozlenilmezFaiz != null ? gozlenilmezFaiz : 0.0);
        budce.setDolayiXerclerFaizi(dolayiFaiz != null ? dolayiFaiz : 0.0);
        budce.hesablaVeYenile();
        budce.setTesdiqleyenIstifadeci(istifadeci);
        budce.setTesdiqTarixi(LocalDateTime.now());
        budceRepository.save(budce);

        smeta.setStatus("TESDIQLENMIS");
        smeta.setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(smeta);
    }

    @Transactional
    public void smetaRedakteEt(Long smetaId, Long layiheId, String ad, String versiya, String valyuta) {
        Smeta smeta = smetaTap(smetaId);
        Layihe layihe = layiheRepository.findById(layiheId).orElseThrow();
        smeta.setLayihe(layihe);
        smeta.setAd(ad);
        smeta.setVersiya(versiya);
        smeta.setValyuta(valyuta);
        smeta.setDuzelisTarixi(LocalDateTime.now());
        smetaRepository.save(smeta);
    }

    // --- EXCEL-Ə İXRAC ---
    public void excelIxracEt(Long smetaId, java.io.OutputStream outputStream) throws java.io.IOException {
        Smeta smeta = smetaTap(smetaId);

        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Smeta");

            // --- ÜSLUBLAR ---
            org.apache.poi.ss.usermodel.CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(org.apache.poi.ss.usermodel.HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.CellStyle bolmeStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font bolmeFont = workbook.createFont();
            bolmeFont.setBold(true);
            bolmeFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            bolmeStyle.setFont(bolmeFont);
            bolmeStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            bolmeStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            bolmeStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.CellStyle altBasliqStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font altFont = workbook.createFont();
            altFont.setBold(true);
            altFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_GREEN.getIndex());
            altBasliqStyle.setFont(altFont);
            altBasliqStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_GREEN.getIndex());
            altBasliqStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            altBasliqStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            org.apache.poi.ss.usermodel.CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            // --- YUXARI HİSSƏ ---
            org.apache.poi.ss.usermodel.Row titleRow = sheet.createRow(0);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Layihə: " + (smeta.getLayihe() != null ? smeta.getLayihe().getAd() : "-") + " | Smeta: " + smeta.getAd());
            titleCell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 8));

            org.apache.poi.ss.usermodel.Row dateRow = sheet.createRow(1);
            dateRow.createCell(0).setCellValue("Tarix: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            dateRow.createCell(5).setCellValue("Valyuta: " + smeta.getValyuta());
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 4));

            // --- CƏDVƏL BAŞLIQLARI ---
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(3);
            String[] headers = {"Kod", "İşin Adı", "Vahid", "Miqdar", "Vahid Qiymət", "Plan (Cəmi)", "Faktiki Xərc", "Qalıq", "İcra (%)"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 4;
            int sütunSayi = headers.length;

            for (SmetaBasliq basliq : smeta.getBasliqlar()) {
                org.apache.poi.ss.usermodel.Row bolmeRow = sheet.createRow(rowNum++);
                org.apache.poi.ss.usermodel.Cell bolmeKodCell = bolmeRow.createCell(0);
                bolmeKodCell.setCellValue(basliq.getKod());
                bolmeKodCell.setCellStyle(bolmeStyle);

                org.apache.poi.ss.usermodel.Cell bolmeAdCell = bolmeRow.createCell(1);
                bolmeAdCell.setCellValue(basliq.getAd());
                bolmeAdCell.setCellStyle(bolmeStyle);

                org.apache.poi.ss.usermodel.Cell bolmeYekunCell = bolmeRow.createCell(5);
                bolmeYekunCell.setCellValue(basliq.getYekunMebleg());
                bolmeYekunCell.setCellStyle(bolmeStyle);
                bolmeYekunCell.getCellStyle().setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

                for (int i = 2; i < sütunSayi; i++) {
                    if (i != 5) {
                        bolmeRow.createCell(i).setCellStyle(bolmeStyle);
                    }
                }

                for (SmetaAltBasliq alt : basliq.getAltBasliqlar()) {
                    org.apache.poi.ss.usermodel.Row altRow = sheet.createRow(rowNum++);
                    org.apache.poi.ss.usermodel.Cell altKodCell = altRow.createCell(0);
                    altKodCell.setCellValue(alt.getKod());
                    altKodCell.setCellStyle(altBasliqStyle);

                    org.apache.poi.ss.usermodel.Cell altAdCell = altRow.createCell(1);
                    altAdCell.setCellValue(alt.getAd());
                    altAdCell.setCellStyle(altBasliqStyle);

                    org.apache.poi.ss.usermodel.Cell altYekunCell = altRow.createCell(5);
                    altYekunCell.setCellValue(alt.getYekunMebleg());
                    altYekunCell.setCellStyle(altBasliqStyle);
                    altYekunCell.getCellStyle().setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

                    for (int i = 2; i < sütunSayi; i++) {
                        if (i != 5) {
                            altRow.createCell(i).setCellStyle(altBasliqStyle);
                        }
                    }

                    for (SmetaBendi bendi : alt.getBəndlər()) {
                        org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(bendi.getKod());
                        row.createCell(1).setCellValue(bendi.getIsAdi());
                        row.createCell(2).setCellValue(bendi.getVahid() != null ? bendi.getVahid() : "");
                        row.createCell(3).setCellValue(bendi.getMiqdar() != null ? bendi.getMiqdar() : 0.0);
                        row.createCell(4).setCellValue(bendi.getVahidQiymeti() != null ? bendi.getVahidQiymeti() : 0.0);
                        row.createCell(5).setCellValue(bendi.getCemiMebleg() != null ? bendi.getCemiMebleg() : 0.0);
                        row.createCell(6).setCellValue(bendi.getFaktikiXerc() != null ? bendi.getFaktikiXerc() : 0.0);
                        row.createCell(7).setCellValue(bendi.getQaliqMebleg() != null ? bendi.getQaliqMebleg() : 0.0);
                        row.createCell(8).setCellValue(bendi.getIcraFaizi() != null ? bendi.getIcraFaizi() : 0.0);

                        row.getCell(4).setCellStyle(moneyStyle);
                        row.getCell(5).setCellStyle(moneyStyle);
                        row.getCell(6).setCellStyle(moneyStyle);
                        row.getCell(7).setCellStyle(moneyStyle);
                    }
                }
            }

            for (int i = 0; i < sütunSayi; i++) {
                sheet.autoSizeColumn(i);
            }

            rowNum++;
            org.apache.poi.ss.usermodel.Row totalRow = sheet.createRow(rowNum);
            org.apache.poi.ss.usermodel.CellStyle totalStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalFont.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());
            totalStyle.setFont(totalFont);
            totalStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_BLUE.getIndex());
            totalStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);

            org.apache.poi.ss.usermodel.Cell totalLabel = totalRow.createCell(0);
            totalLabel.setCellValue("YEKUN:");
            totalLabel.setCellStyle(totalStyle);
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(rowNum, rowNum, 0, 4));

            org.apache.poi.ss.usermodel.Cell totalValue = totalRow.createCell(5);
            totalValue.setCellValue(smeta.getUmumiMebleg());
            totalValue.setCellStyle(totalStyle);
            totalValue.getCellStyle().setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));

            workbook.write(outputStream);
        }
    }
}