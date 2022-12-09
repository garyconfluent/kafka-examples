/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gvidalconfluent.kafka.examples.consumer.internal;

import org.apache.kafka.common.utils.Bytes;

import java.nio.ByteBuffer;

public class Utils {

    public static byte[] longToBytes(long l) {
        return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(l).array();
    }
    public static long bytesToLong(Bytes bytes) {
        {
            long value = 0l;

            // Iterating through for loop
            for (byte b : bytes.get()) {
                // Shifting previous value 8 bits to right and
                // add it with next value
                value = (value << 8) + (b & 255);
            }

            return value;
        }}
}
