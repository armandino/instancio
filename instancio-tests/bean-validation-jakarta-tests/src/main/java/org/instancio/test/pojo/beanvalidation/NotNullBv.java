/*
 * Copyright 2022-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.test.pojo.beanvalidation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

@Data
public class NotNullBv {

    //@formatter:off
    @NotNull private String string;
    @NotNull private byte primitiveByte;
    @NotNull private short primitiveShort;
    @NotNull private int primitiveInt;
    @NotNull private long primitiveLong;
    @NotNull private float primitiveFloat;
    @NotNull private double primitiveDouble;
    @NotNull private Byte byteWrapper;
    @NotNull private Short shortWrapper;
    @NotNull private Integer integerWrapper;
    @NotNull private Long longWrapper;
    @NotNull private Float floatWrapper;
    @NotNull private Double doubleWrapper;
    @NotNull private BigInteger bigInteger;
    @NotNull private BigDecimal bigDecimal;
    @NotNull private String[] array;
    @NotNull private Collection<String> collection;
    @NotNull private Map<UUID, Integer> map;
    //@formatter:on
}
