package com.devoxx.genie.service.jan;

import com.devoxx.genie.chatmodel.local.jan.JanModelService;
import com.devoxx.genie.model.jan.Data;
import com.devoxx.genie.ui.settings.DevoxxGenieStateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link JanModelService}.
 *
 * <p>Note: The {@link #testGetModels()} test requires Jan to be running before execution.
 * Ensure that Jan is running on localhost:1337 before running this test, otherwise it
 * will be skipped.</p>
 */
class JanServiceTest extends BaseIntellijTest {

    @Mock
    private DevoxxGenieStateService mockStateService;
    private JanModelService janService;

    @BeforeEach
    @Override
    public void setUpTest() throws Exception {
        super.setUpTest();
        MockitoAnnotations.openMocks(this);

        janService = spy(new JanModelService());

        try (var mockedStatic = mockStatic(DevoxxGenieStateService.class)) {
            mockedStatic.when(DevoxxGenieStateService::getInstance)
                    .thenReturn(mockStateService);

            when(mockStateService.getConfigValue("janModelUrl"))
                    .thenReturn("http://localhost:8080/");
        }
    }

    @Test
    @EnabledIf("isJanRunning")
    void testGetModels() throws IOException {
        List<Data> models = janService.getModels();
        assertThat(models).isNotEmpty();

        models.forEach(model -> {
            assertThat(model).isNotNull();
            assertThat(model.getId()).isNotNull();
            assertThat(model.getName()).isNotNull();
            assertThat(model.getEngine()).isNotNull();
        });
    }

    private boolean isJanRunning() {
        try (var socket = new Socket("localhost", 1337)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
