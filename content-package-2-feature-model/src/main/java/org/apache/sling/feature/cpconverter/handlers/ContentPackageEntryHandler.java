/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.sling.feature.cpconverter.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.vault.fs.io.Archive;
import org.apache.jackrabbit.vault.fs.io.Archive.Entry;
import org.apache.sling.feature.cpconverter.ContentPackage2FeatureModelConverter;

public final class ContentPackageEntryHandler extends AbstractRegexEntryHandler {

    public ContentPackageEntryHandler() {
        super("(?:jcr_root)?/etc/packages/.+\\.zip");
    }

    @Override
    public void handle(String path, Archive archive, Entry entry, ContentPackage2FeatureModelConverter converter) throws Exception {
        logger.info("Processing sub-content package '{}'...", entry.getName());

        File temporaryContentPackage = File.createTempFile("content-package", entry.getName());

        try (InputStream input = archive.openInputStream(entry);
                OutputStream output = new FileOutputStream(temporaryContentPackage)) {
            IOUtils.copy(input, output);
        }

        converter.processSubPackage(path, temporaryContentPackage);
    }

}
