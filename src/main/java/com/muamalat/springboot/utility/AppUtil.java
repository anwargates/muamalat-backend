package com.muamalat.springboot.utility;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AppUtil {
    static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");

    @SneakyThrows

    public static Timestamp convertStringToTimestamp(String strDate) {
        if (!isObjectEmpty(strDate)) {

            // you can change format of date
            Date date = yyyyMMdd.parse(strDate);
            Date waktu = normalizeToDateOnly(date);
            Timestamp timeStampDate = new Timestamp(waktu.getTime());

            return timeStampDate;
        }
        return null;
    }

    public static Timestamp convertTimeToTimestamp(String strDate) throws ParseException {
        if (!AppUtil.isObjectEmpty(strDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
            Date date = sdf.parse(strDate);
            Timestamp jtbs = new Timestamp(date.getTime());
            return jtbs;
        }
        return null;

    }

    @SneakyThrows
    public static Date convertStringToDate(String strDate) {
        // you can change format of date
        Date waktu = yyyyMMdd.parse(strDate);
        return waktu;
    }

    public static Date convertStringToDateKusus(String strDate) throws ParseException {
        // you can change format of date
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
        Date date = sdf.parse(strDate);
        return date;
    }

    /**
     * Checks if is collection empty.
     *
     * @param collection the collection
     * @return true, if is collection empty
     */
    private static boolean isCollectionEmpty(Collection<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is object empty.
     *
     * @param object the object
     * @return true, if is object empty
     */
    public static boolean isObjectEmpty(Object object) {
        if (object == null) return true;
        else if (object instanceof String) {
            if (((String) object).trim().length() == 0 || ((String) object).equalsIgnoreCase("")) {
                return true;
            }
        } else if (object instanceof Collection) {
            return isCollectionEmpty((Collection<?>) object);
        }
        return false;
    }

    /**
     * Gets the bean to json string.
     *
     * @param beanClass the bean class
     * @return the bean to json string
     */
    public static String getBeanToJsonString(Object beanClass) {
        return new Gson().toJson(beanClass);
    }

    /**
     * Gets json object to string.
     *
     * @param data      the bean class
     * @param getObject is the object that will get the value
     * @return the string value of this object
     */

    public static String getJsonObject(Object data, String getObject) {
        String object = getBeanToJsonString(data);
        JSONObject jObject = new JSONObject(object);
        String result = (String) jObject.get(getObject);
        return result;
    }

    /**
     * Gets the bean to json string.
     *
     * @param beanClasses the bean classes
     * @return the bean to json string
     */
    public static String getBeanToJsonString(Object... beanClasses) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object beanClass : beanClasses) {
            stringBuilder.append(getBeanToJsonString(beanClass));
            stringBuilder.append(", ");
        }
        return stringBuilder.toString();
    }

    /**
     * Concatenate.
     *
     * @param listOfItems the list of items
     * @param separator   the separator
     * @return the string
     */
    public String concatenate(List<String> listOfItems, String separator) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> stit = listOfItems.iterator();

        while (stit.hasNext()) {
            sb.append(stit.next());
            if (stit.hasNext()) {
                sb.append(separator);
            }
        }

        return sb.toString();
    }

    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public static String camelCaseToUnderscore(String text) {
        // change from camelCase to camel_case --> for Hibernate-compliant field name
        Matcher m = Pattern.compile("(?<=[a-z])[A-Z]").matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "_" + m.group().toLowerCase());
        }
        m.appendTail(sb);
        String fieldName = sb.toString();

        return fieldName;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUtil.class);

    public static String handleFileUpload(String scanFile, String SAVE_DIR, String ext) {

        String outputFilePath = "file" + "_" + new Timestamp(System.currentTimeMillis()) + "." + ext;

        if (!scanFile.isEmpty()) {

            try {
                LOGGER.info("Decoding base64");

                LOGGER.info("Creating the directory to store");
                File fileSaveDir = new File(SAVE_DIR);
                if (!fileSaveDir.exists()) {
                    LOGGER.info("Creates the save directory if it does not exists");
                    fileSaveDir.mkdirs();
                }

                // decode the string and write to file
                byte[] decodedBytes = Base64.getDecoder().decode(scanFile);

                // create output file
                File outputFile = new File(fileSaveDir
                        //.getParentFile()
                        .getAbsolutePath()
                        + "/" + outputFilePath);

                FileUtils.writeByteArrayToFile(outputFile, decodedBytes);


                return outputFilePath;

            } catch (Exception e) {
                return "You failed to upload file => " + e.getMessage();
            }
        } else {
            return "You failed to upload file because the file is empty.";
        }

    }

    public static String handleFileRemove(String SAVE_DIR, String urlFile) {
        if (!urlFile.isEmpty()) {
            try {

                FileUtils.touch(new File(SAVE_DIR + "/" + urlFile));
                File fileToDelete = FileUtils.getFile(SAVE_DIR + "/" + urlFile);
                boolean success = FileUtils.deleteQuietly(fileToDelete);

                return "success";

            } catch (Exception e) {
                return "You failed to delete file => " + e.getMessage();
            }
        } else {
            return "You failed to delete file because the file is empty.";
        }
    }

    public static BigDecimal nvlBigDecimal(Object input) {
        if (isObjectEmpty(input) || input.equals("null") || input == null) {
            return new BigDecimal(0);
        } else {
            if (input.getClass() == Double.class) {
                return new BigDecimal(input.toString());
            } else if (input.getClass() == String.class) {
                return new BigDecimal(input.toString().replace(",", ""));
            } else {
                return new BigDecimal(input.toString().replace(",", "."));
            }

        }


    }

    public static Double nvlDouble(Object input) {
        if (isObjectEmpty(input) || input.equals("null") || input == null) {
            return 0D;
        } else {
            if (input.getClass() == BigDecimal.class) {
                double value = Double.parseDouble(input.toString().replace(",", "."));
                return value;
            } else if (input.getClass() == String.class) {
                return Double.parseDouble(input.toString());
            } else {
                return Double.parseDouble(input.toString());
            }

        }


    }

    public static Long nvlLong(Object input) {
        if (isObjectEmpty(input) || input.equals("null") || input == null) {
            return 0L;
        } else {
            if (input.getClass() == Double.class) {
                return ((Double) ((Double) input).doubleValue()).longValue();
            } else if (input.getClass() == String.class) {
                Long nilai = Long.parseLong(input.toString().replace(",", "."));
                return nilai.longValue();
            } else {
                return new Long(input.toString().replace(",", "."));
            }

        }


    }

    public static BigDecimal nvlBigDecimalPungutan(Object input) {
        System.out.println(isObjectEmpty(input));
        if (isObjectEmpty(input) || input.equals("null")) {
            return new BigDecimal(0);
        } else {
            return new BigDecimal(input.toString().replace(",", ""));
        }
    }

    public static Date nvlDate(Date input) {
        if (isObjectEmpty(input) || input.equals("null")) {
            return new Date();
        } else {
            return input;
        }
    }

    public static Object nvlObject(Object input) {
        if (isObjectEmpty(input) || input.equals("null")) {
            if (input instanceof BigDecimal) {
                input = new BigDecimal(0);
            } else if (input instanceof Integer) {
                input = 0;
            } else if (input instanceof String) {
                input = "-";
            } else if (input instanceof Date) {
                input = null;
            }
        }
        return input;
    }

    public static boolean isGreaterThan(Date tgl1, Date tgl2) {
        boolean cek = false;
        try {
            if (tgl1.after(tgl2)) {
                cek = true;
            }
        } catch (Exception e) {
            //SYSTEM.out.println(e);
        }
        return cek;
    }

    public static int getDiffDayBetween2Date(Date date1, Date date2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();

        c1.setTime(date1);
        c2.setTime(date2);
        int diffDay = 0;

        if (c1.before(c2)) {
            diffDay = countDiffDay(c1, c2);
        } else {
            diffDay = countDiffDay(c2, c1);
        }

        return diffDay;
    }

    public static int countDiffDay(Calendar c1, Calendar c2) {
        int returnInt = 0;
        while (!c1.after(c2)) {
            c1.add(Calendar.DAY_OF_MONTH, 1);
            returnInt++;
        }

        if (returnInt > 0) {
            returnInt = returnInt - 1;
        }

        return (returnInt);
    }

    public static boolean IN(String str, String strArrMerge) {
        StringTokenizer token = new StringTokenizer(strArrMerge, ",");
        List<String> list = new ArrayList();
        while (token.hasMoreTokens()) {
            list.add(token.nextToken());
        }
        boolean isIN = false;

        if (str != null && list != null) {
            for (String arr : list) {
                if (arr.equalsIgnoreCase(str)) {
                    isIN = true;
                    break;
                }
            }
        }
        return isIN;
    }

    public static boolean cekTanggal(Date tgl1, String komparasi, Date tgl2) {
        boolean cek = false;
        try {
            tgl1 = normalizeToDateOnly(tgl1);
            tgl2 = normalizeToDateOnly(tgl2);
            int a = tgl1.compareTo(tgl2);
            if (komparasi.equalsIgnoreCase("<")) {
                if (a < 0) {
                    cek = true;
                }
            }
            if (komparasi.equalsIgnoreCase("<=")) {
                if (a <= 0) {
                    cek = true;
                }
            }
            if (komparasi.equalsIgnoreCase(">")) {
                if (a > 0) {
                    cek = true;
                }
            }
            if (komparasi.equalsIgnoreCase(">=")) {
                if (a >= 0) {
                    cek = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cek;
    }

    public static Date normalizeToDateOnly(Date d) {
        Calendar nd = Calendar.getInstance();

        nd.setTime(d);
        nd.set(Calendar.HOUR_OF_DAY, 0);
        nd.set(Calendar.MINUTE, 0);
        nd.set(Calendar.SECOND, 0);
        nd.set(Calendar.MILLISECOND, 0);

        return nd.getTime();
    }

    public static boolean isContainerNumberValid(String pCid) {
        if (pCid == null || pCid.length() != 11) {
            return false;
        }
        String char2num = "0123456789A?BCDEFGHIJK?LMNOPQRSTU?VWXYZ";
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            int n = (char2num.indexOf(pCid.charAt(i)));
            n *= Math.pow(2, i);
            sum += n;
        }
        int rem = (sum % 11) % 10;
        return char2num.indexOf(pCid.charAt(10)) == rem;
    }

    public static boolean isInList(String value, List<String> list) {
        if (value == null) {
            return false;
        }
        for (String item : list) {
            if (value.equals(item)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNotInList(String value, List<String> list) {
        return !isInList(value, list);
    }

    public static String substrSafe(String input, int from, int to) {
        if (input == null) {
            return null;
        }
        if (from >= input.length() || from > to) {
            return null;
        }
        if (from < 0) {
            from = 0;
        }
        if (to > input.length()) {
            return input.substring(from, input.length());
        }
        return input.substring(from, to);
    }

    public static BigDecimal remainderPungutan(BigDecimal pungutan) { //pembulatan 1000
        if (pungutan != null) {
            if (pungutan.compareTo(new BigDecimal(0)) != 0) {
                BigDecimal remainder = pungutan.remainder(new BigDecimal(1000));
                if (remainder.compareTo(new BigDecimal(0)) != 0) {
                    BigDecimal result = pungutan.subtract(remainder).add(new BigDecimal(1000));
                    return result;
                }
            }
        }
        return pungutan;
    }

    public static BigDecimal ABS(BigDecimal val) {
        return val.compareTo(BigDecimal.ZERO) < 0 ? val.abs() : val;
    }

    public static BigInteger ABS(BigInteger val) {
        return val.compareTo(BigInteger.ZERO) < 0 ? val.abs() : val;
    }

    public static Integer ABS(Integer val) {
        return val < 0 ? val : Math.abs(val);
    }

    public static boolean cekDateBetween(Date tanggalCek, Date tanggalAwal, Date tanggalAkhir) {
        boolean cek = false;
        try {
            if (yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalCek))).after(yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalAwal)))) && yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalCek))).before(yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalAkhir))))) {
                cek = true;
            } else if (yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalCek))).compareTo(nvlDate(yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalAwal))))) == 0 || yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalCek))).compareTo(nvlDate(yyyyMMdd.parse(yyyyMMdd.format(nvlDate(tanggalAkhir))))) == 0) {
                cek = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cek;
    }

    public static String fi_zap(String pNoDok) {
        if (isObjectEmpty(pNoDok)) {
            return pNoDok;
        }
        String retVal = pNoDok;
        try {
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < pNoDok.length(); i++) {
                switch (pNoDok.charAt(i)) {
                    case '~':
                    case ' ':
                    case '`':
                    case '!':
                    case '@':
                    case '#':
                    case '$':
                    case '%':
                    case '^':
                    case '&':
                    case '*':
                    case '(':
                    case ')':
                    case '_':
                    case '-':
                    case '+':
                    case '=':
                    case '/':
                    case '\\':
                    case '.':
                    case ',':
                    case ';':
                    case ':':
                    case '\'':
                    case '"':
                    case '|':
                    case '?':
                    case '>':
                    case '<':
                    case '[':
                    case ']':
                    case '{':
                    case '}':
                        continue;
                    default:
                        temp.append(pNoDok.charAt(i));
                }
            }
            retVal = temp.toString();
        } catch (Exception e) {
        }
        return retVal;
    }

    public static String ZapEntitas(String str) {
//        String replace = getParParamDetail("104");
        return str.replaceAll("'[^0-9A-Z]|PT|PT.|P.T|P.T.|CV|CV.|C.V|C.V.|LTD.|LTD|L.T.D|L.T.D.|TBK|TBK.|T.B.K|T.B.K.|PERSERO|PERUSAHAAN ROKOK|PD|PD.|P.D.|P.D.'", "");
    }

    public static double similarity(String s1, String s2) {
        String longer = ZapEntitas(s1);
        String shorter = ZapEntitas(s2);
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2;
            shorter = s1;
        }
        if (shorter.length() < longer.length() && shorter.length() + 10 < longer.length()) {
            longer = longer.substring(0, shorter.length() + 9);
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
        }
        return ((longerLength - editDistance(longer, shorter)) / (double) longerLength) * 100;
    }

    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                } else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    public static String formatRupiah(Object obj) {
        String retVal = null;
        if (obj instanceof Number) {
            Currency currency = Currency.getInstance("IDR");
            NumberFormat num = NumberFormat.getInstance();
            num.setCurrency(currency);
            retVal = num.format(obj);
        }
        return retVal;
    }

    public static String splitCamelCase(String s) {
        return s.replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    public static BigDecimal nullBigDecimal(BigDecimal bd) {
        if (bd != null) {
            return bd;
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static Integer nullInteger(Integer i) {
        if (i != null) {
            return i;
        } else {
            return 0;
        }
    }

    public static String nullString(String s) {
        if (s != null) {
            return s;
        } else {
            return "";
        }
    }

    public static Date stringToDate(String s) {
        try {
            if (s != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s");
                Date date = formatter.parse(s + " 07:00:00.0");
                return date;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String timeStampToString(Timestamp t) {
        Date date = new Date();
        date.setTime(t.getTime());
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    public static Timestamp stringToTimestamp(String s) {
        try {
            if (s != null) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s");
                Date date = formatter.parse(s + " 07:00:00.0");
                Timestamp timestamp = new Timestamp(date.getTime());
                return timestamp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
