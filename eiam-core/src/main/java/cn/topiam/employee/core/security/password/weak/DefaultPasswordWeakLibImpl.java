/*
 * eiam-core - Employee Identity and Access Management Program
 * Copyright © 2020-2023 TopIAM (support@topiam.cn)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cn.topiam.employee.core.security.password.weak;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.util.Objects.nonNull;

/**
 * 弱密码库实现
 *
 * @author TopIAM
 * Created by support@topiam.cn on  2022/8/16 22:00
 */
public class DefaultPasswordWeakLibImpl implements PasswordWeakLib {

    /**
     * This is the default file used for the most common passwords:
     * https://github.com/danielmiessler/SecLists/blob/master/Passwords/Common-Credentials/10k-most-common.txt
     *
     * MIT License
     * Copyright (c) 2018 Daniel Miessler
     * Permission is hereby granted, free of charge, to any person obtaining a copy
     * of this software and associated documentation files (the "Software"), to deal
     * in the Software without restriction, including without limitation the rights
     * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     * copies of the Software, and to permit persons to whom the Software is
     * furnished to do so, subject to the following conditions:
     * The above copyright notice and this permission notice shall be included in all
     * copies or substantial portions of the Software.
     * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     * SOFTWARE.
     */
    private static final String        DEFAULT_DICTIONARY_PATH = "/dictionaries/10k-most-common.txt";

    private static final Logger        logger                  = LoggerFactory
        .getLogger(DefaultPasswordWeakLibImpl.class);
    /**
     * 弱密码字典
     */
    private final Map<String, Boolean> dictionary;

    public DefaultPasswordWeakLibImpl() {
        this.dictionary = new HashMap<>(16);
        safeReadEmbeddedFile();
    }

    /**
     * 读取嵌入文件
     */
    private void safeReadEmbeddedFile() {
        try {
            logger.debug("加载嵌入式弱密码库字典");
            readStream(this.getClass().getResourceAsStream(DEFAULT_DICTIONARY_PATH));
            logger.debug("已加载嵌入式弱密码库字典");
        } catch (Exception e) {
            logger.error("无法加载嵌入式弱密码库字典", e);
        }
    }

    private void readStream(InputStream inputStream) throws IOException {
        if (nonNull(inputStream)) {
            var newEntries = new HashMap<String, Boolean>(16);
            var entriesToKeep = new HashSet<String>();
            var reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if (wordExists(line)) {
                    entriesToKeep.add(line);
                } else {
                    newEntries.put(line, true);
                }
            }
            synchronized (dictionary) {
                dictionary.keySet().retainAll(entriesToKeep);
                dictionary.putAll(newEntries);
            }
        }
    }

    @Override
    public Boolean wordExists(String word) {
        synchronized (dictionary) {
            return Boolean.TRUE.equals(dictionary.get(word));
        }
    }

    /**
     * 获取弱密码列表
     *
     * @return {@link List <String>}
     */
    @Override
    public List<String> getWordList() {
        return dictionary.keySet().stream().toList();
    }
}
