package ru.inovus.messaging.impl.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.sun.istack.ByteArrayDataSource;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.inovus.messaging.TestApp;
import ru.inovus.messaging.api.criteria.RecipientCriteria;
import ru.inovus.messaging.api.model.AttachmentResponse;
import ru.inovus.messaging.impl.util.DocumentUtils;

import javax.activation.DataHandler;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "novus.messaging.attachment.enabled: true")
@TestPropertySource("classpath:application.properties")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@EnableEmbeddedPg
public class AttachmentServiceTest {

    private static final String FIRST_FILE_NAME = "test_file_name_1.pdf";
    private static final String SECOND_FILE_NAME = "test_file_name_2.pdf";
    private static final String THIRD_FILE_NAME = "test_file_name_3.pdf";
    private static final UUID FIRST_MESSAGE_ID = UUID.fromString("a2bd666b-1684-4005-a10f-f14224f66d0a");
    private static final UUID SECOND_MESSAGE_ID = UUID.fromString("d1450cd1-5b93-47fe-9e44-a3800476342e");
    private static final UUID THIRD_MESSAGE_ID = UUID.fromString("6aa72600-1290-41ae-8429-e36ce46f60af");
    @Autowired
    private AttachmentService service;
    @MockBean
    private AmazonS3 s3client;
    @Autowired
    private DocumentUtils documentUtils;

    @BeforeAll
    void initialize() {
        service.create(List.of(getNewAttachment(FIRST_FILE_NAME), getNewAttachment(SECOND_FILE_NAME)), FIRST_MESSAGE_ID);
        service.create(List.of(getNewAttachment(THIRD_FILE_NAME)), SECOND_MESSAGE_ID);
    }

    @Test
    void createTest() {
        RecipientCriteria criteria = new RecipientCriteria();
        Page<AttachmentResponse> responses = service.findAll(criteria);
        assertThat(responses.getContent().size(), is(3));

        criteria.setMessageId(FIRST_MESSAGE_ID);
        responses = service.findAll(criteria);
        assertThat(responses.getContent().size(), is(2));
        assertThat(responses.getContent().get(0).getShortFileName(), is(FIRST_FILE_NAME));
        assertThat(responses.getContent().get(1).getShortFileName(), is(SECOND_FILE_NAME));

        List<AttachmentResponse> responsesList = service.findAll(SECOND_MESSAGE_ID);
        assertThat(responsesList.size(), is(1));
        assertThat(responsesList.get(0).getShortFileName(), is(THIRD_FILE_NAME));
    }

    @Test
    void fileCountTest() {
        assertThrows(UserException.class, () -> service.create(List.of(getNewAttachment(FIRST_FILE_NAME),
                        getNewAttachment(SECOND_FILE_NAME),
                        getNewAttachment(THIRD_FILE_NAME)),
                THIRD_MESSAGE_ID));
    }

    @Test
    void fileDuplicateTest() {
        AttachmentResponse firstAttachment = getNewAttachment(FIRST_FILE_NAME);
        firstAttachment.setFileSize(100);
        assertThrows(UserException.class, () -> service.create(List.of(firstAttachment, firstAttachment),
                THIRD_MESSAGE_ID));
    }

    @Test
    void uploadTest() {
        when(s3client.doesBucketExistV2(any())).thenReturn(true);
        when(s3client.putObject(anyString(), anyString(), any(), any())).thenReturn(null);
        AttachmentResponse response = service.upload(createAttachment());
        assertThat(response.getShortFileName(), is(FIRST_FILE_NAME));
    }

    @Test
    void downloadTest() {
        when(s3client.getObject(anyString(), anyString())).thenReturn(new S3Object());
        List<AttachmentResponse> responsesList = service.findAll(SECOND_MESSAGE_ID);
        Response response = service.download(responsesList.get(0).getId());
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
        assertTrue(((String) response.getHeaders().get("Content-Disposition").get(0)).contains(THIRD_FILE_NAME));
    }

    @Test
    void deleteTest() {
        Response response = service.delete(FIRST_FILE_NAME);
        assertThat(response.getStatus(), is(Response.Status.OK.getStatusCode()));
    }

    private AttachmentResponse getNewAttachment(String fileName) {
        AttachmentResponse attachmentResponse = new AttachmentResponse();
        attachmentResponse.setId(UUID.randomUUID());
        attachmentResponse.setFileName(documentUtils.getFileNameWithDateTimePrefix(fileName));
        attachmentResponse.setShortFileName(fileName);
        return attachmentResponse;
    }

    private Attachment createAttachment() {
        MetadataMap<String, String> map = new MetadataMap();
        map.add("Content-Disposition", String.format("filename=%s", FIRST_FILE_NAME));
        return new Attachment("id",
                new DataHandler(new ByteArrayDataSource(new byte[]{1}, "application/octet-stream")),
                map);
    }
}