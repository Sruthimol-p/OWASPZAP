/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2017 The ZAP Development Team
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
package org.zaproxy.zap.extension.callback.ui;

import javax.swing.table.TableModel;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.view.table.HistoryReferencesTable;

/** @deprecated (2.11.0) Superseded by the OAST add-on. */
@Deprecated
public class CallbackTable extends HistoryReferencesTable {

    private static final long serialVersionUID = 1L;

    public CallbackTable(CallbackTableModel model) {
        super(model);

        setAutoCreateColumnsFromModel(false);
        setName("Callback Table");
        getColumnExt(Constant.messages.getString("view.href.table.header.note")).setVisible(false);
    }

    @Override
    public void setModel(TableModel tableModel) {
        if (!(tableModel instanceof CallbackTableModel)) {
            throw new IllegalArgumentException(
                    "Parameter tableModel must be a CallbackTableModel.");
        }

        super.setModel(tableModel);
    }

    @Override
    public CallbackTableModel getModel() {
        return (CallbackTableModel) super.getModel();
    }
}
