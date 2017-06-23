/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.android.apksig.util;

import java.io.IOException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Tests for the {@link DataSource} returned by {@link DataSinks#newInMemoryDataSink()}.
 */
@RunWith(JUnit4.class)
public class InMemoryDataSinkDataSourceTest extends DataSourceTestBase {
    @Override
    protected CloseableWithDataSource createDataSource(byte[] contents) throws IOException {
        ReadableDataSink sink = DataSinks.newInMemoryDataSink();
        sink.consume(contents, 0, contents.length);
        return CloseableWithDataSource.of(sink);
    }
}
