/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.data.assets

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXTemplateKey
import java.nio.charset.Charset

/**
 * @suppress
 */
object GXBinParser {

    data class GXBinaryData(
        var layer: String = "",
        var databinding: String = "",
        var css: String = "",
        var js: String = ""
    )

    fun parser(bytes: ByteArray): JSONObject {
        val result = JSONObject()
        parserToBinaryData(bytes)?.let { binData ->
            result[GXTemplateKey.GAIAX_LAYER] = binData.layer
            result[GXTemplateKey.GAIAX_DATABINDING] = binData.databinding
            result[GXTemplateKey.GAIAX_CSS] = binData.css
            result[GXTemplateKey.GAIAX_JS] = binData.js
        }
        return result
    }

    private const val FILE_HEAD_NUM_BYTE = 100

    private const val FILE_NUM_BYTE = 4

    private fun convertFourUnSignInt(byteArray: ByteArray): Int = (byteArray[1].toInt() and 0xFF) shl 8 or (byteArray[0].toInt() and 0xFF)

    private fun parserToBinaryData(bytes: ByteArray): GXBinaryData? {
        val binData = GXBinaryData()
        val fileLength = bytes.size

        val headContent = bytes.copyOfRange(0, FILE_HEAD_NUM_BYTE)
        val headReadSize = headContent.size
        if (headReadSize <= 0) {
            return null
        }

        var remaining = fileLength - FILE_HEAD_NUM_BYTE
        var offset = FILE_HEAD_NUM_BYTE
        while (remaining > 0) {

            val nameLengthBA = bytes.copyOfRange(offset, offset + FILE_NUM_BYTE)
            val nameLengthResult = nameLengthBA.size
            val nameLength = convertFourUnSignInt(nameLengthBA)

            offset += nameLengthResult
            remaining -= nameLengthResult

            val nameContent = bytes.copyOfRange(offset, offset + nameLength)
            val nameReadSize = nameContent.size
            val name = String(nameContent, Charset.forName("UTF-8"))

            offset += nameReadSize
            remaining -= nameReadSize

            val contentLengthBA = bytes.copyOfRange(offset, offset + FILE_NUM_BYTE)
            val contentLengthResult = contentLengthBA.size
            val contentLength = convertFourUnSignInt(contentLengthBA)

            offset += contentLengthResult
            remaining -= contentLengthResult

            val contentContent = bytes.copyOfRange(offset, offset + contentLength)
            val contentReadSize = contentContent.size
            val content = String(contentContent, Charset.forName("UTF-8"))

            offset += contentReadSize
            remaining -= contentReadSize

            when (name) {
                GXTemplateKey.GAIAX_INDEX_JSON -> binData.layer = content
                GXTemplateKey.GAIAX_INDEX_DATABINDING -> binData.databinding = content
                GXTemplateKey.GAIAX_INDEX_CSS -> binData.css = content
                GXTemplateKey.GAIAX_INDEX_JS -> binData.js = content
            }
        }
        return binData
    }
}