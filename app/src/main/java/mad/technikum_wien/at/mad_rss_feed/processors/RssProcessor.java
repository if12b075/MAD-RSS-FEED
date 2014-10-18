/*
 * Copyright (C) 2012, 2013 the diamond:dogs|group
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
package mad.technikum_wien.at.mad_rss_feed.processors;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import at.diamonddogs.service.processor.XMLProcessor;

/**
 *
 */
public class RssProcessor extends XMLProcessor<ArrayList<String>> {

    public static final int ID = 9302588;
    /**
     * Parses feed post titles only
     */
    @Override
    protected ArrayList<String> parse(Document inputObject) {
        NodeList nodeList = inputObject.getElementsByTagName("title");
        ArrayList<String> ret = new ArrayList<String>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getParentNode().getNodeName().equals("item")) {
                ret.add(nodeList.item(i).getTextContent());
            }
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getProcessorID() {
        return ID;
    }


}
