/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.sling.cli.impl.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;

@Component(service = VoteThreadFinder.class)
public class VoteThreadFinder {
    
    public EmailThread findVoteThread(String releaseName) throws IOException {
        try ( CloseableHttpClient client = HttpClients.createDefault() ) {
            
            URI uri = new URIBuilder("https://lists.apache.org/api/stats.lua")
                .addParameter("domain", "sling.apache.org")
                .addParameter("list", "dev")
                .addParameter("d", "lte=1M")
                .addParameter("q", "[VOTE] Release " + releaseName)
                .build();
            
            HttpGet get = new HttpGet(uri);
            try ( CloseableHttpResponse response = client.execute(get)) {
                try ( InputStream content = response.getEntity().getContent();
                        InputStreamReader reader = new InputStreamReader(content)) {
                    if ( response.getStatusLine().getStatusCode() != 200 )
                        throw new IOException("Status line : " + response.getStatusLine());
                    Gson gson = new Gson();
                    return gson.fromJson(reader, EmailThread.class);
                }
            }
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
