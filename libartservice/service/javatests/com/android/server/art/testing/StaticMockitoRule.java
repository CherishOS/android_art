/*
 * Copyright (C) 2022 The Android Open Source Project
 *
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
 */

package com.android.server.art.testing;

import static com.android.dx.mockito.inline.extended.ExtendedMockito.mockitoSession;

import com.android.dx.mockito.inline.extended.StaticMockitoSession;
import com.android.dx.mockito.inline.extended.StaticMockitoSessionBuilder;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.mockito.quality.Strictness;

/**
 * Similar to {@link MockitoRule}, but uses {@StaticMockitoSession}, which allows mocking static
 * methods.
 */
public class StaticMockitoRule implements MethodRule {
    private Class<?>[] mClasses;

    public StaticMockitoRule(Class<?>... classes) {
        mClasses = classes;
    }

    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        return new Statement() {
            public void evaluate() throws Throwable {
                StaticMockitoSessionBuilder builder =
                        mockitoSession()
                                .name(target.getClass().getSimpleName() + "." + method.getName())
                                .initMocks(target)
                                .strictness(Strictness.STRICT_STUBS);

                for (Class<?> clazz : mClasses) {
                    builder.mockStatic(clazz);
                }

                StaticMockitoSession session = builder.startMocking();

                try {
                    base.evaluate();
                } finally {
                    session.finishMocking();
                }
            }
        };
    }
}
