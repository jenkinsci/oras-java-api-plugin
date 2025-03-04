/*-
 * =LICENSE=
 * ORAS Java SDK
 * ===
 * Copyright (C) 2024 - 2025 ORAS
 * ===
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =LICENSEEND=
 */

package io.jenkins.plugins.oras.java.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import land.oras.ContainerRef;
import land.oras.LocalPath;
import land.oras.Registry;
import land.oras.Tags;
import land.oras.utils.ArchiveUtils;
import land.oras.utils.Const;
import land.oras.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@WireMockTest
@Execution(ExecutionMode.CONCURRENT)
public class SmokeTest {
    @Test
    void shouldListTags(WireMockRuntimeInfo wmRuntimeInfo) {

        // Return data from wiremock
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(WireMock.get(WireMock.urlEqualTo("/v2/library/artifact-text/tags/list"))
                .willReturn(WireMock.okJson(JsonUtils.toJson(new Tags("artifact-text", List.of("latest", "0.1.1"))))));

        // Insecure registry
        Registry registry = Registry.Builder.builder().insecure().build();

        // Test
        List<String> tags = registry.getTags(ContainerRef.parse("%s/library/artifact-text"
                .formatted(wmRuntimeInfo.getHttpBaseUrl().replace("http://", ""))));

        // Assert
        assertEquals(2, tags.size());
        assertEquals("latest", tags.get(0));
        assertEquals("0.1.1", tags.get(1));
    }

    @Test
    void compressionShouldWork(@TempDir Path tempDir) throws Exception {
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.writeString(tempDir.resolve("file1.txt"), "foobar");
        Files.createFile(tempDir.resolve("file2.txt"));
        Files.writeString(tempDir.resolve("file2.txt"), "barfoo");
        LocalPath tar = ArchiveUtils.tar(LocalPath.of(tempDir));
        ArchiveUtils.compress(tar, Const.DEFAULT_BLOB_DIR_MEDIA_TYPE); // gzip
        ArchiveUtils.compress(tar, Const.BLOB_DIR_ZSTD_MEDIA_TYPE); // zstd
    }
}
