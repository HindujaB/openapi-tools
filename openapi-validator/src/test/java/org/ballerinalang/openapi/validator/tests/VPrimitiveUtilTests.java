/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ballerinalang.openapi.validator.tests;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import org.ballerinalang.openapi.validator.BTypeToJsonValidatorUtil;
import org.ballerinalang.openapi.validator.OpenApiValidatorException;
import org.ballerinalang.openapi.validator.ServiceValidator;
import org.ballerinalang.openapi.validator.error.ValidationError;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.wso2.ballerinalang.compiler.semantics.model.symbols.BVarSymbol;
import org.wso2.ballerinalang.compiler.tree.BLangPackage;

import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Unit tests for BJsonSchemaUtil Invalid tests.
 */
public class VPrimitiveUtilTests {
    private static final Path RES_DIR = Paths.get("src/test/resources/project-based-tests/src/recordValidation" +
            "/swagger/").toAbsolutePath();
    private OpenAPI api;
    private OpenAPI newApi;
    private BLangPackage bLangPackage;
    private Schema extractSchema;
    private BVarSymbol extractBVarSymbol;
    private List<ValidationError> validationErrors = new ArrayList<>();

    @Test(enabled = false, description = "Type mismatch with integer")
    public void testIntegerType() throws UnsupportedEncodingException, OpenApiValidatorException {
        Path contractPath = RES_DIR.resolve("validTests/primitive/integerB.yaml");
        api = ServiceValidator.parseOpenAPIFile(contractPath.toString());
        bLangPackage = ValidatorTest.getBlangPackage("recordValidation/ballerina/validTests" +
                "/primitive/integerB.bal");
        extractSchema = ValidatorTest.getSchema(api, "/user/{userId}");
        extractBVarSymbol = ValidatorTest.getBVarSymbol(bLangPackage);
        validationErrors = BTypeToJsonValidatorUtil.validate(extractSchema, extractBVarSymbol);
        Assert.assertTrue(validationErrors.isEmpty());
    }

    @Test(enabled = false, description = "Type mismatch with string")
    public void testStringType() throws UnsupportedEncodingException, OpenApiValidatorException {
        Path contractPath = RES_DIR.resolve("validTests/primitive/stringB.yaml");
        api = ServiceValidator.parseOpenAPIFile(contractPath.toString());
        bLangPackage = ValidatorTest.getBlangPackage("recordValidation/ballerina/validTests/primitive/stringB.bal");
        extractSchema = ValidatorTest.getSchema(api, "/user/{userId}");
        extractBVarSymbol = ValidatorTest.getBVarSymbol(bLangPackage);
        validationErrors = BTypeToJsonValidatorUtil.validate(extractSchema, extractBVarSymbol);
        Assert.assertTrue(validationErrors.isEmpty());
    }

    @Test(enabled = false, description = "Type mismatch with boolean")
    public void testBooleanType() throws UnsupportedEncodingException, OpenApiValidatorException {
        Path contractPath = RES_DIR.resolve("validTests/primitive/booleanB.yaml");
        api = ServiceValidator.parseOpenAPIFile(contractPath.toString());
        bLangPackage = ValidatorTest.getBlangPackage("recordValidation/ballerina/validTests/primitive/booleanB.bal");
        extractSchema = ValidatorTest.getSchema(api, "/user/{userId}");
        extractBVarSymbol = ValidatorTest.getBVarSymbol(bLangPackage);
        validationErrors = BTypeToJsonValidatorUtil.validate(extractSchema, extractBVarSymbol);
        Assert.assertTrue(validationErrors.isEmpty());
    }
}
