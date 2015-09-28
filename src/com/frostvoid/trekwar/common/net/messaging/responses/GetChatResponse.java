/*
 * Copyright 2012 FrostVoid Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.frostvoid.trekwar.common.net.messaging.responses;

import com.frostvoid.trekwar.common.ChatLine;
import com.frostvoid.trekwar.common.net.messaging.Response;

import java.util.ArrayList;

/**
 * Sends all the chat lines for a user to the client
 *
 * @author Erlend Aakre
 * @author FrostVoid Software
 * @author http://www.frostvoid.com
 */
public class GetChatResponse extends Response {
    private boolean hasChatLines;
    private ArrayList<ChatLine> chatLines;

    public GetChatResponse(boolean hasChatLines) {
        this.hasChatLines = hasChatLines;
    }

    public void setChatLines(ArrayList<ChatLine> chatLines) {
        this.chatLines = chatLines;
    }

    public boolean hasChatLines() {
        return hasChatLines;
    }

    public ArrayList<ChatLine> getChatLines() {
        return chatLines;
    }
}
