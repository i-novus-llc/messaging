package ru.inovus.messaging.impl.utils;

import com.sun.istack.ByteArrayDataSource;
import jakarta.activation.DataHandler;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.pg.EnableTestcontainersPg;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.impl.util.DocumentUtils;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "novus.messaging.attachment.enabled: false")
@TestPropertySource("classpath:application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableTestcontainersPg
public class DocumentUtilsTest {
    private static final String VALID_FILE_NAME = "test_file_name.pdf";
    private static final String INVALID_FILE_NAME = "test_file_name.xml";

    @Value("${novus.messaging.attachment.file-size}")
    private Integer maxFileSize;
    @Autowired
    private DocumentUtils documentUtils;

    @Test
    void getFileNameWithDateTimeTest() {
        String fileName = documentUtils.getFileNameWithDateTimePrefix("filename");
        String date = fileName.substring(0, documentUtils.getDateTimePrefixLength() - 1);
        assertDoesNotThrow(() -> LocalDateTime.from(documentUtils.getFormatter().parse(date)));
        fileName = documentUtils.getFileNameWithDateTimePrefix(null);
        assertThat(fileName, nullValue());
    }

    @Test
    void getFileNameTest() {
        String fileName = documentUtils.getFileName(createAttachment());
        assertThat(fileName, is(VALID_FILE_NAME));
        fileName = documentUtils.getFileName(null);
        assertThat(fileName, nullValue());
    }

    @Test
    void checkFileExtensionTest() {
        assertDoesNotThrow(() -> documentUtils.checkFileExtension(VALID_FILE_NAME));
        assertThrows(UserException.class, () -> documentUtils.checkFileExtension(INVALID_FILE_NAME));
        assertDoesNotThrow(() -> documentUtils.checkFileExtension(null));
    }

    @Test
    void checkFileSizeTest() {
        assertDoesNotThrow(() -> documentUtils.checkFileSize(new ByteArrayInputStream(new byte[10])));
        assertThrows(UserException.class, () -> documentUtils.checkFileSize(new ByteArrayInputStream(new byte[maxFileSize * 1024 * 1024 * 2])));
        assertDoesNotThrow(() -> documentUtils.checkFileSize(null));
    }

    @Test
    void replaceInvalidCharactersTest() {
        assertEquals("C_Users_A_Evseeva_Шайдакова_М.А._выписка_из_истории_болезни.pdf",
                documentUtils.replaceInvalidCharacters("C:\\Users\\A_Evseeva\\Шайдакова_М.А._выписка из/истории:болезни.pdf"));
        assertEquals("C_Users_A_Evseeva_Шайдакова_М.А._выписка_из_истории_болезни.pdf",
                documentUtils.replaceInvalidCharacters("C:/Users/A_Evseeva/Шайдакова_М.А._выписка из/истории:болезни.pdf"));
    }

    private Attachment createAttachment() {
        MetadataMap<String, String> map = new MetadataMap();
        map.add("Content-Disposition", String.format("filename=%s", VALID_FILE_NAME));
        return new Attachment("id",
                new DataHandler(new ByteArrayDataSource(new byte[]{1}, "application/octet-stream")),
                map);
    }
}
