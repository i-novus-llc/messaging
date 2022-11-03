package ru.inovus.messaging.impl.util;

import lombok.Getter;
import net.n2oapp.platform.i18n.Message;
import net.n2oapp.platform.i18n.UserException;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;


@Controller
@Getter
public class DocumentUtils {

    @Value("${novus.messaging.attachment.file-type:}")
    private List<String> fileExtensionList;
    @Value("${novus.messaging.attachment.file-size:10}")
    private Integer maxFileSize;
    @Value("${novus.messaging.attachment.file-prefix-format:dd.MM.yyyy_HH:mm:ss}")
    private String dateTimeFormat;
    private DateTimeFormatter formatter;
    private Integer dateTimePrefixLength;

    @PostConstruct
    void initialize() {
        this.formatter = DateTimeFormatter.ofPattern(this.dateTimeFormat);
        this.dateTimePrefixLength = this.dateTimeFormat.length() + 1;
    }

    public String getFileNameWithDateTimePrefix(String fileName) {
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