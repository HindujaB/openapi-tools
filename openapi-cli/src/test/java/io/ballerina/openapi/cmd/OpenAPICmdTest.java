/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.openapi.cmd;

import io.ballerina.cli.launcher.BLauncherException;
import io.ballerina.openapi.exception.BallerinaOpenApiException;
import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * OpenAPI command test suit.
 */
public class OpenAPICmdTest extends OpenAPICommandTest {
    @BeforeTest(description = "This will create a new ballerina project for testing below scenarios.")
    public void setupBallerinaProject() throws IOException {
        super.setup();
    }

    @Test(description = "Test openapi command with help flag")
    public void testOpenAPICmdHelp() throws IOException {
        String[] args = {"-h"};
        OpenApiCmd openApiCommand = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(openApiCommand).parseArgs(args);
        openApiCommand.execute();
        String output = readOutput(true);
        Assert.assertTrue(output.contains("NAME\n      Generate a Ballerina service"));
    }

    @Test(description = "Test openapi command without help flag")
    public void testOpenAPICmdHelpWithoutFlag() throws IOException {
        OpenApiCmd openApiCommand = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(openApiCommand);
        openApiCommand.execute();
        String output = readOutput(true);
        Assert.assertTrue(output.contains("NAME\n      Generate a Ballerina service"));
    }

    @Test(description = "Test openapi gen-service without openapi contract file")
    public void testWithoutOpenApiContract() throws IOException {
        String[] args = {"--input"};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        cmd.execute();
        String output = readOutput(true);
        Assert.assertTrue(output.contains("An OpenAPI definition file is required to generate the service."));
    }

    @Test(description = "Test openapi gen-service for successful service generation")
    public void testSuccessfulServiceGeneration() throws IOException {
        Path petstoreYaml = resourceDir.resolve(Paths.get("petstore.yaml"));
        String[] args = {"--input", petstoreYaml.toString(), "-o", this.tmpDir.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        cmd.execute();
        Path expectedSchemaFile = resourceDir.resolve(Paths.get("expected_gen", "petstore_schema.bal"));
        Stream<String> expectedSchemaLines = Files.lines(expectedSchemaFile);
        String expectedSchemaContent = expectedSchemaLines.collect(Collectors.joining("\n"));
        expectedSchemaLines.close();
        if (Files.exists(this.tmpDir.resolve("client.bal")) &&
                Files.exists(this.tmpDir.resolve("petstore_service.bal")) &&
                Files.exists(this.tmpDir.resolve("types.bal")) &&
                Files.exists(this.tmpDir.resolve("tests/test.bal"))) {
            //Compare schema contents
            Stream<String> schemaLines = Files.lines(this.tmpDir.resolve("types.bal"));
            String generatedSchema = schemaLines.collect(Collectors.joining("\n"));
            schemaLines.close();
            generatedSchema = (generatedSchema.trim()).replaceAll("\\s+", "");
            expectedSchemaContent = (expectedSchemaContent.trim()).replaceAll("\\s+", "");
            if (expectedSchemaContent.equals(generatedSchema)) {
                Assert.assertTrue(true);
                deleteGeneratedFiles(false);
            } else {
                Assert.fail("Expected content and actual generated content is mismatched for: "
                        + petstoreYaml.toString());
                deleteGeneratedFiles(false);
            }
        } else {
            Assert.fail("Service generation failed.");
        }
    }

    @Test(description = "Test openapi to ballerina generation with license headers")
    public void testGenerationWithLicenseHeaders() throws IOException {
        Path petstoreYaml = resourceDir.resolve(Paths.get("petstore.yaml"));
        Path licenseHeader = resourceDir.resolve(Paths.get("license.txt"));
        String[] args = {"--input", petstoreYaml.toString(), "-o", this.tmpDir.toString(), "--prefix",
                licenseHeader.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        cmd.execute();
        Path expectedSchemaFile = resourceDir.resolve(Paths.get("expected_gen",
                "petstore_schema_with_license.bal"));
        Stream<String> expectedSchemaLines = Files.lines(expectedSchemaFile);
        String expectedSchemaContent = expectedSchemaLines.collect(Collectors.joining("\n"));
        expectedSchemaLines.close();
        if (Files.exists(this.tmpDir.resolve("client.bal")) &&
                Files.exists(this.tmpDir.resolve("petstore_service.bal")) &&
                Files.exists(this.tmpDir.resolve("types.bal")) &&
                Files.exists(this.tmpDir.resolve("tests/test.bal"))) {
            //Compare schema contents
            Stream<String> schemaLines = Files.lines(this.tmpDir.resolve("types.bal"));
            String generatedSchema = schemaLines.collect(Collectors.joining("\n"));
            schemaLines.close();
            generatedSchema = (generatedSchema.trim()).replaceAll("\\s+", "");
            expectedSchemaContent = (expectedSchemaContent.trim()).replaceAll("\\s+", "");
            if (expectedSchemaContent.equals(generatedSchema)) {
                Assert.assertTrue(true);
                deleteGeneratedFiles(false);
            } else {
                Assert.fail("Expected content and actual generated content is mismatched for: "
                        + petstoreYaml.toString());
                deleteGeneratedFiles(false);
            }
        } else {
            Assert.fail("Generation failed.");
        }
    }

    @Test(description = "Test openapi to ballerina generation with no new line license headers")
    public void testGenerationWithLicenseHeadersWithOneNewLine() throws IOException {
        Path petstoreYaml = resourceDir.resolve(Paths.get("petstore.yaml"));
        Path licenseHeader = resourceDir.resolve(Paths.get("license_with_new_line.txt"));
        String[] args = {"--input", petstoreYaml.toString(), "-o", this.tmpDir.toString(), "--prefix",
                licenseHeader.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        cmd.execute();
        Path expectedSchemaFile = resourceDir.resolve(Paths.get("expected_gen",
                "generated_client_with_license.bal"));
        Stream<String> expectedSchemaLines = Files.lines(expectedSchemaFile);
        String expectedClientContent = expectedSchemaLines.collect(Collectors.joining("\n"));
        expectedSchemaLines.close();
        if (Files.exists(this.tmpDir.resolve("client.bal")) &&
                Files.exists(this.tmpDir.resolve("petstore_service.bal")) &&
                Files.exists(this.tmpDir.resolve("types.bal")) &&
                Files.exists(this.tmpDir.resolve("tests/test.bal"))) {
            //Compare schema contents
            Stream<String> clientLines = Files.lines(this.tmpDir.resolve("client.bal"));
            String generatedClientContent = clientLines.collect(Collectors.joining("\n"));
            clientLines.close();
            generatedClientContent = (generatedClientContent.trim()).replaceAll("\\s+", "");
            expectedClientContent = (expectedClientContent.trim()).replaceAll("\\s+", "");
            if (expectedClientContent.equals(generatedClientContent)) {
                Assert.assertTrue(true);
                deleteGeneratedFiles(false);
            } else {
                Assert.fail("Expected content and actual generated content is mismatched for: "
                        + petstoreYaml.toString());
                deleteGeneratedFiles(false);
            }
        } else {
            Assert.fail("Generation failed.");
        }
    }

    @Test(description = "Test openapi to ballerina generation with license headers and test suit")
    public void testGenerationOfTestSuiteWithLicenseHeaders() throws IOException {
        Path petstoreYaml = resourceDir.resolve(Paths.get("petstore_with_auth.yaml"));
        Path licenseHeader = resourceDir.resolve(Paths.get("license.txt"));
        String[] args = {"--input", petstoreYaml.toString(), "-o", this.tmpDir.toString(), "--prefix",
                licenseHeader.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        cmd.execute();
        Path expectedConfigFilePath = resourceDir.resolve(Paths.get("expected_gen",
                "api_key_config.toml"));
        Stream<String> expectedSchemaLines = Files.lines(expectedConfigFilePath);
        String expectedConfig = expectedSchemaLines.collect(Collectors.joining("\n"));
        expectedSchemaLines.close();
        if (Files.exists(this.tmpDir.resolve("client.bal")) &&
                Files.exists(this.tmpDir.resolve("petstore_with_auth_service.bal")) &&
                Files.exists(this.tmpDir.resolve("types.bal")) &&
                Files.exists(this.tmpDir.resolve("tests/Config.toml")) &&
                Files.exists(this.tmpDir.resolve("tests/test.bal"))) {
            //Compare schema contents
            Stream<String> configContent = Files.lines(this.tmpDir.resolve("tests/Config.toml"));
            String generatedConfig = configContent.collect(Collectors.joining("\n"));
            configContent.close();
            generatedConfig = (generatedConfig.trim()).replaceAll("\\s+", "");
            expectedConfig = (expectedConfig.trim()).replaceAll("\\s+", "");
            if (expectedConfig.equals(generatedConfig)) {
                Assert.assertTrue(true);
                deleteGeneratedFiles(true);
            } else {
                Assert.fail("Expected content and actual generated content is mismatched for: "
                        + petstoreYaml.toString());
                deleteGeneratedFiles(true);
            }
        } else {
            Assert.fail("Generation failed.");
        }
    }

    @Test(description = "Test exception when invalid prefix file given")
    public void testInvalidPrefixFile() throws IOException, BallerinaOpenApiException {
        Path petstoreYaml = resourceDir.resolve(Paths.get("petstore.yaml"));
        Path licenseHeader = resourceDir.resolve(Paths.get("licence.txt"));
        String[] args = {"--input", petstoreYaml.toString(), "-o", this.tmpDir.toString(), "--prefix",
                licenseHeader.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        cmd.execute();
        String output = readOutput(true);
        Assert.assertTrue(output.contains("Invalid prefix file path : "));
    }

    @Test(description = "Test openapi gen-service for .yml file service generation")
    public void testSuccessfulServiceGenerationForYML() throws IOException {
        Path petstoreYaml = resourceDir.resolve(Paths.get("petstore.yml"));
        String[] args = {"--input", petstoreYaml.toString(), "-o", this.tmpDir.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);
        try {
            cmd.execute();
        } catch (BLauncherException e) {
        }
        Path expectedSchemaFile = resourceDir.resolve(Paths.get("expected_gen", "petstore_schema.bal"));
        Stream<String> expectedSchemaLines = Files.lines(expectedSchemaFile);
        String expectedSchemaContent = expectedSchemaLines.collect(Collectors.joining("\n"));
        expectedSchemaLines.close();
        if (Files.exists(this.tmpDir.resolve("client.bal")) &&
                Files.exists(this.tmpDir.resolve("petstore_service.bal")) &&
                Files.exists(this.tmpDir.resolve("types.bal")) &&
                Files.exists(this.tmpDir.resolve("tests/test.bal"))) {
            //Compare schema contents
            Stream<String> schemaLines = Files.lines(this.tmpDir.resolve("types.bal"));
            String generatedSchema = schemaLines.collect(Collectors.joining("\n"));
            schemaLines.close();
            generatedSchema = (generatedSchema.trim()).replaceAll("\\s+", "");
            expectedSchemaContent = (expectedSchemaContent.trim()).replaceAll("\\s+", "");
            if (expectedSchemaContent.equals(generatedSchema)) {
                Assert.assertTrue(true);
                deleteGeneratedFiles(false);
            } else {
                Assert.fail("Expected content and actual generated content is mismatched for: "
                        + petstoreYaml.toString());
                deleteGeneratedFiles(false);
            }
        } else {
            Assert.fail("Service generation failed.");
        }
    }

    @Test(description = "Test ballerina to openapi")
    public void testBallerinaToOpenAPIGeneration() {
        Path petstoreBal = resourceDir.resolve(Paths.get("bal-files/ballerinaFile.bal"));
        String[] args = {"--input", petstoreBal.toString(), "-o", this.tmpDir.toString()};
        OpenApiCmd cmd = new OpenApiCmd(printStream, tmpDir, false);
        new CommandLine(cmd).parseArgs(args);

        String output = "";
        try {
            cmd.execute();
        } catch (BLauncherException e) {
            output = e.getDetailedMessages().get(0);
            Assert.fail(output);
        }
    }

    // Delete the generated files
    private void deleteGeneratedFiles(boolean isConfigGenerated) throws IOException {
        File serviceFile = new File(this.tmpDir.resolve("petstore_service.bal").toString());
        File clientFile = new File(this.tmpDir.resolve("client.bal").toString());
        File schemaFile = new File(this.tmpDir.resolve("types.bal").toString());
        File testFile = new File(this.tmpDir.resolve("tests/test.bal").toString());
        File testDir = new File(this.tmpDir.resolve("tests").toString());
        serviceFile.delete();
        clientFile.delete();
        schemaFile.delete();
        testFile.delete();
        if (isConfigGenerated) {
            File configFile = new File(this.tmpDir.resolve("tests/Config.toml").toString());
            configFile.delete();
        }
        FileUtils.deleteDirectory(testDir);
    }

    @Test(description = "getRelative path")
    public void getRelativePath() {
        OpenApiCmd cmd = new OpenApiCmd();
        File resource01 = new File("dir1/test.txt");
        String target01 = "dir1/dir2";
        File resource02 = new File("dir1/dir2/dir3/test.txt");
        String target02 = "dir1/dir2";
        File resource03 = new File("dir2/dir3/dir4/test.txt");
        String target03 = "dir/dir1";
        Assert.assertTrue((cmd.getRelativePath(resource01, target01).toString()).equals("../test.txt") ||
                (cmd.getRelativePath(resource01, target01).toString()).equals("..\\test.txt"));
        Assert.assertTrue((cmd.getRelativePath(resource02, target02).toString()).equals("dir3/test.txt") ||
                (cmd.getRelativePath(resource02, target02).toString()).equals("dir3\\test.txt"));
        Assert.assertTrue((cmd.getRelativePath(resource03, target03).toString()).
                equals("../../dir2/dir3/dir4/test.txt") || (cmd.getRelativePath(resource03, target03).toString()).
                equals("..\\..\\dir2\\dir3\\dir4\\test.txt"));
    }

    @AfterTest
    public void clean() {
        System.setErr(null);
        System.setOut(null);
    }
}
