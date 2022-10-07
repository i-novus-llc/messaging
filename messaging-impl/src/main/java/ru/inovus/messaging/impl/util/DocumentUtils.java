package ru.inovus.messaging.impl.util;

import net.n2oapp.platform.i18n.Message;
import net.n2oapp.platform.i18n.UserException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;


public class DocumentUtils {

    private static final String DATE_TIME_FORMAT = "dd.MM.yyyy_HH:mm:ss";
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final int DATE_TIME_PREFIX_LENGTH = DATE_TIME_FORMAT.length() + 1;

    private List<String> fileExtensionList;
    private Integer maxFileSize;

    public DocumentUtils(List<String> fileExtensionList, Integer maxFileSize) {
        this.fileExtensionList = fileExtensionList;
        this.maxFileSize = maxFileSize;
    }

    public String getFileNameWithDateTime(String fileName) {
        if (!hasText(fileName))
            return null;
        return String.format("%s_%s", formatter.format(LocalDateTime.now()), fileName);
    }

    public String getFileName(Attachment attachment) {
        if (nonNull(attachment) && nonNull(attachment.getContentDisposition()) && hasText(attachment.getContentDisposition().getFilename())) {
            String result = new String(attachment.getContentDisposition()
                    .getFilename()
                    .getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            return replaceInvalidCharacters(result);
        }
        return null;
    }

    public void checkFileExtension(String fileName) {
        if (!hasText(fileName)) return;
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if (!fileExtensionList.contains(suffix.toLowerCase()))
            throw new UserException(new Message("messaging.exception.file.notValidFormat", fileExtensionList));
    }

    public Integer checkFileSize(InputStream is) throws IOException {
        if (isNull(is)) return null;
        int size = is.available();
        if ((size / (1024 * 1024)) > maxFileSize)
            throw new UserException(new Message("messaging.exception.file.size", maxFileSize));
        return size;
    }

    public String replaceInvalidCharacters(String source) {
        return source.trim().replaceAll("((:\\/)|(:\\\\)|[ :\\/\\\\])", "_");
    }
}