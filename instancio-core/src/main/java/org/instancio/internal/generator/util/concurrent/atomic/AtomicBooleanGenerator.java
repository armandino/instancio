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
package org.instancio.internal.generator.util.concurrent.atomic;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGenerator;

import java.util.concurrent.atomic.AtomicBoolean;

public class AtomicBooleanGenerator extends AbstractGenerator<AtomicBoolean> {

    public AtomicBooleanGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return null; // no generator spec
    }

    @Override
    protected AtomicBoolean tryGenerateNonNull(final Random random) {
        return new AtomicBoolean(random.trueOrFalse());
    }
}
