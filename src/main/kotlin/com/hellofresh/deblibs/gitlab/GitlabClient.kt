/*
 * Copyright (c) 2018. The DebLibs Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hellofresh.deblibs.gitlab

import com.hellofresh.deblibs.Adapters
import com.hellofresh.deblibs.BaseClient
import com.squareup.moshi.JsonAdapter
import okhttp3.Response
import java.net.HttpURLConnection

class GitlabClient(
    private val id: String,
    private val token: String,
    private val gitlabIssue: GitlabIssue
) : BaseClient {

    private val moshiAdapter: JsonAdapter<GitlabIssue> = Adapters.adapter()

    override fun run() {
        createIssue()
    }

    private fun createIssue() {
        val (response, status) = makePostRequest()
        if (status != HttpURLConnection.HTTP_CREATED) {
            if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                error("404 Repository at '$id' was not found")
            }
            error("Could not create github issue: $status ${response?.message}\n$gitlabIssue")
        }
    }

    private fun makePostRequest(): Pair<Response?, Int> {
        val json = moshiAdapter.toJson(gitlabIssue)
        return postRequest(
            json,
            "https://gitlab.com/api/v4/projects/$id/issues",
            "Private-Token",
            token
        )
    }
}
