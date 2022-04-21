/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.generator.time;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.random.RandomProvider;

import java.time.YearMonth;

public class YearMonthGenerator extends AbstractTemporalGenerator<YearMonth> {

    private static final YearMonth MIN = YearMonth.of(1970, 1);
    private static final YearMonth MAX = YearMonth.now().plusYears(50);

    public YearMonthGenerator(final GeneratorContext context) {
        super(context, MIN, MAX);
    }

    @Override
    YearMonth now() {
        return YearMonth.now();
    }

    @Override
    YearMonth getEarliestFuture() {
        return YearMonth.now().plusMonths(1);
    }

    @Override
    void validateRange() {
        ApiValidator.isTrue(min.isBefore(max), "Start value must be before end: %s, %s", min, max);
    }

    @Override
    public YearMonth generate(final RandomProvider random) {
        int minMonth = min.getYear() * 12 + min.getMonthValue();
        int maxMonth = max.getYear() * 12 + max.getMonthValue();
        int result = random.intRange(minMonth, maxMonth);

        return YearMonth.of(result / 12, result % 12 + 1);
    }
}